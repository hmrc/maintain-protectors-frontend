/*
 * Copyright 2023 HM Revenue & Customs
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

import models.UserAnswers
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.mongo.test.MongoSupport

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global

class PlaybackRepositorySpec extends AnyWordSpec with Matchers
  with ScalaFutures with OptionValues with MongoSupport with MongoSuite with BeforeAndAfterEach {

  private lazy val repository: PlaybackRepository = new PlaybackRepository(mongoComponent, config)

  override def beforeEach(): Unit =
    await(repository.collection.deleteMany(BsonDocument()).toFuture())

  "a playback repository" should {

    "return None when no UserAnswers exists" in {
      val internalId: String = "Int-328969d0-557e-2559-96ba-074d0597107e"
      val utr: String = "utr-identifier"
      val sessionId: String = "sessionId"

      repository.get(internalId, utr, sessionId) .futureValue mustBe None
    }

    "return UserAnswers when one exists" in {
      val internalId: String = "Int-328969d0-557e-0987-96ba-123d4567890e"
      val identifier = "identifier"
      val sessionId: String = "sessionId"

      val newId: String = s"$internalId-$identifier-$sessionId"
      val userAnswers: UserAnswers = UserAnswers(internalId, identifier, sessionId, newId, LocalDate.now())

      repository.get(internalId, identifier, sessionId).futureValue mustBe None

      repository.set(userAnswers).futureValue mustBe true

      val dbUserAnswer = repository.get(internalId, identifier, sessionId).futureValue
      dbUserAnswer.map(_.copy(updatedAt = userAnswers.updatedAt)) mustBe Some(userAnswers)
    }

    "return UserAnswers after an update" in {
      val internalId: String = "Int-328969d0-557e-0987-96ba-123d4567890e"
      val identifier = "identifier"
      val sessionId: String = "sessionId"

      val newId: String = s"$internalId-$identifier-$sessionId"
      val userAnswers: UserAnswers = UserAnswers(internalId, identifier, sessionId, newId, LocalDate.now())
      val updatedUserAnswers: UserAnswers = userAnswers.copy(data = Json.obj("key" -> "something"), isTaxable = false)

      repository.get(internalId, identifier, sessionId).futureValue mustBe None

      repository.set(userAnswers).futureValue mustBe true

      val dbUserAnswer = repository.get(internalId, identifier, sessionId).futureValue
      dbUserAnswer.map(_.copy(updatedAt = userAnswers.updatedAt)) mustBe Some(userAnswers)

      //update

      repository.set(updatedUserAnswers).futureValue mustBe true

      val testUpdatedUserAnswers = repository.get(internalId, identifier, sessionId).futureValue
      testUpdatedUserAnswers.map(_.copy(updatedAt = userAnswers.updatedAt)) mustBe Some(updatedUserAnswers)
    }
  }
}
