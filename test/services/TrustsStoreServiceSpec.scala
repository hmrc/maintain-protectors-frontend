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

package services

import base.SpecBase
import connectors.TrustsStoreConnector
import models.TaskStatus.Completed
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

class TrustsStoreServiceSpec extends SpecBase with ScalaFutures {

  private val mockConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

  private val featureFlagService = new TrustsStoreService(mockConnector)

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  "TrustsStoreService" when {

    ".updateTaskStatus" must {
      "call trusts store connector" in {

        when(mockConnector.updateTaskStatus(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(OK, "")))

        val result = featureFlagService.updateTaskStatus("identifier", Completed)

        result.futureValue.status mustBe OK
        verify(mockConnector).updateTaskStatus(eqTo("identifier"), eqTo(Completed))(any(), any())
      }
    }
  }
}
