/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package repositories

import config.FrontendAppConfig
import models.UserAnswers
import org.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model._
import play.api.Logging
import play.api.libs.json._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlaybackRepository @Inject()(mongoComponent: MongoComponent,
                                   config: FrontendAppConfig
                                  )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[UserAnswers](
    mongoComponent = mongoComponent,
    collectionName = "user-answers",
    domainFormat = Format(UserAnswers.reads, UserAnswers.writes),
    indexes = Seq(
      IndexModel(
        Indexes.ascending("updatedAt"),
        IndexOptions()
          .name("user-answers-updated-at-index")
          .expireAfter(config.cachettlplaybackInSeconds, TimeUnit.SECONDS)
          .unique(false)
      ),
      IndexModel(
        Indexes.ascending("newId"),
        IndexOptions()
          .name("internal-id-and-utr-and-sessionId-compound-index")
          .unique(false)
      )
    ),
    replaceIndexes = config.dropIndexes
  ) with Logging {

  private def selector(internalId: String, utr: String, sessionId: String): Bson =
    equal("newId", s"$internalId-$utr-$sessionId")

  def get(internalId: String, utr: String, sessionId: String): Future[Option[UserAnswers]] = {

    logger.debug(s"PlaybackRepository getting user answers for $internalId")

    val modifier = Updates.set("updatedAt", LocalDateTime.now)

    val updateOption = new FindOneAndUpdateOptions().upsert(false).returnDocument(ReturnDocument.BEFORE)

    collection.findOneAndUpdate(selector(internalId, utr, sessionId), modifier, updateOption).toFutureOption()
  }

  def set(userAnswers: UserAnswers): Future[Boolean] = {

    val newUserAnswers = userAnswers.copy(updatedAt = LocalDateTime.now)

    val replaceOptions = ReplaceOptions().upsert(true)

    collection.replaceOne(selector(userAnswers.internalId, userAnswers.identifier, userAnswers.sessionId), newUserAnswers, replaceOptions)
      .headOption().map(_.exists(_.wasAcknowledged()))
  }
}
