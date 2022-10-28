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
import models.UtrSession
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
class ActiveSessionRepository @Inject()(mongoComponent: MongoComponent,
                                        config: FrontendAppConfig
                                       )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[UtrSession](
    mongoComponent = mongoComponent,
    collectionName = "session",
    domainFormat = Format(UtrSession.reads, UtrSession.writes),
    indexes = Seq(
      IndexModel(
        Indexes.ascending("updatedAt"),
        IndexOptions()
          .name("session-updated-at-index")
          .expireAfter(config.cachettlSessionInSeconds, TimeUnit.SECONDS)
          .unique(false)
      ),
      IndexModel(
        Indexes.ascending("utr"),
        IndexOptions()
          .name("utr-index")
          .unique(false)
      )
    ),
    replaceIndexes = config.dropIndexes
  ) with Logging {

  def get(internalId: String): Future[Option[UtrSession]] = {

    logger.debug(s"ActiveSessionRepository getting active utr for $internalId")

    val selector = equal("internalId", internalId)
    val modifier = Updates.set("updatedAt", LocalDateTime.now())

    val updateOption = new FindOneAndUpdateOptions().upsert(false).returnDocument(ReturnDocument.AFTER)

    collection.findOneAndUpdate(selector, modifier, updateOption).toFutureOption()

  }

  def set(session: UtrSession): Future[Boolean] = {

    val selector = equal("internalId", session.internalId)

    val newSession = session.copy(updatedAt = LocalDateTime.now)

    val replaceOptions = new ReplaceOptions().upsert(true)

    collection.replaceOne(selector, newSession, replaceOptions).headOption().map(_.exists(_.wasAcknowledged()))
  }
}
