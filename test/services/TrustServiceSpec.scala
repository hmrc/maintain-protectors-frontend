/*
 * Copyright 2021 HM Revenue & Customs
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
import connectors.TrustsConnector
import models.protectors.{BusinessProtector, Protectors}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class TrustServiceSpec extends SpecBase {

  private val identifier: String = "utr"
  implicit val hc: HeaderCarrier = HeaderCarrier()

  "TrustService" when {

    val mockConnector = mock[TrustsConnector]

    val service = new TrustServiceImpl(mockConnector)

    ".getBusinessUtrs" must {

      "return empty list" when {

        "no businesses" in {

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(Nil, Nil)))

          val result = Await.result(service.getBusinessUtrs(identifier, None), Duration.Inf)

          result mustBe Nil
        }

        "there are businesses but they don't have a UTR" in {

          val businesses = List(
            BusinessProtector("Business", None, None, None, LocalDate.parse("2000-01-01"), provisional = true)
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(Nil, businesses)))

          val result = Await.result(service.getBusinessUtrs(identifier, None), Duration.Inf)

          result mustBe Nil
        }

        "there is a business with a UTR but it's the same index as the one we're amending" in {

          val businesses = List(
            BusinessProtector("Business", Some("utr"), None, None, LocalDate.parse("2000-01-01"), provisional = true)
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(Nil, businesses)))

          val result = Await.result(service.getBusinessUtrs(identifier, Some(0)), Duration.Inf)

          result mustBe Nil
        }
      }

      "return UTRs" when {

        "businesses have UTRs and we're adding (i.e. no index)" in {

          val businesses = List(
            BusinessProtector("Business 1", Some("utr1"), None, None, LocalDate.parse("2000-01-01"), provisional = true),
            BusinessProtector("Business 2", Some("utr2"), None, None, LocalDate.parse("2000-01-01"), provisional = true)
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(Nil, businesses)))

          val result = Await.result(service.getBusinessUtrs(identifier, None), Duration.Inf)

          result mustBe List("utr1", "utr2")
        }

        "businesses have UTRs and we're amending" in {

          val businesses = List(
            BusinessProtector("Business 1", Some("utr1"), None, None, LocalDate.parse("2000-01-01"), provisional = true),
            BusinessProtector("Business 2", Some("utr2"), None, None, LocalDate.parse("2000-01-01"), provisional = true)
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(Nil, businesses)))

          val result = Await.result(service.getBusinessUtrs(identifier, Some(0)), Duration.Inf)

          result mustBe List("utr2")
        }
      }
    }
  }

}
