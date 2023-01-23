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
import connectors.TrustsConnector
import models.protectors.{BusinessProtector, IndividualProtector, Protectors}
import models.{Name, NationalInsuranceNumber}
import org.mockito.ArgumentMatchers.any
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class TrustServiceSpec extends SpecBase {

  private val identifier: String = "utr"
  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private val business: BusinessProtector = BusinessProtector(
    name = "Business 1",
    utr = None,
    countryOfResidence = None,
    address = None,
    entityStart = LocalDate.parse("2000-01-01"),
    provisional = true
  )

  private val individual: IndividualProtector = IndividualProtector(
    name = Name("Joe", None, "Bloggs"),
    dateOfBirth = None,
    countryOfNationality = None,
    identification = None,
    countryOfResidence = None,
    address = None,
    mentalCapacityYesNo = None,
    entityStart = LocalDate.parse("2000-01-01"),
    provisional = true
  )

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
            business.copy(utr = None)
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(Nil, businesses)))

          val result = Await.result(service.getBusinessUtrs(identifier, None), Duration.Inf)

          result mustBe Nil
        }

        "there is a business with a UTR but it's the same index as the one we're amending" in {

          val businesses = List(
            business.copy(utr = Some("utr"))
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
            business.copy(utr = Some("utr1")),
            business.copy(utr = Some("utr2"))
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(Nil, businesses)))

          val result = Await.result(service.getBusinessUtrs(identifier, None), Duration.Inf)

          result mustBe List("utr1", "utr2")
        }

        "businesses have UTRs and we're amending" in {

          val businesses = List(
            business.copy(utr = Some("utr1")),
            business.copy(utr = Some("utr2"))
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(Nil, businesses)))

          val result = Await.result(service.getBusinessUtrs(identifier, Some(0)), Duration.Inf)

          result mustBe List("utr2")
        }
      }
    }

    ".getIndividualNinos" must {

      "return empty list" when {

        "no individuals" in {

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(Nil, Nil)))

          val result = Await.result(service.getIndividualNinos(identifier, None), Duration.Inf)

          result mustBe Nil
        }

        "there are individuals but they don't have a NINo" in {

          val individuals = List(
            individual.copy(identification = None)
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(individuals, Nil)))

          val result = Await.result(service.getIndividualNinos(identifier, None), Duration.Inf)

          result mustBe Nil
        }

        "there is an individual with a NINo but it's the same index as the one we're amending" in {

          val individuals = List(
            individual.copy(identification = Some(NationalInsuranceNumber("nino")))
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(individuals, Nil)))

          val result = Await.result(service.getIndividualNinos(identifier, Some(0)), Duration.Inf)

          result mustBe Nil
        }
      }

      "return NINos" when {

        "individuals have NINos and we're adding (i.e. no index)" in {

          val individuals = List(
            individual.copy(identification = Some(NationalInsuranceNumber("nino1"))),
            individual.copy(identification = Some(NationalInsuranceNumber("nino2")))
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(individuals, Nil)))

          val result = Await.result(service.getIndividualNinos(identifier, None), Duration.Inf)

          result mustBe List("nino1", "nino2")
        }

        "individuals have NINos and we're amending" in {

          val individuals = List(
            individual.copy(identification = Some(NationalInsuranceNumber("nino1"))),
            individual.copy(identification = Some(NationalInsuranceNumber("nino2")))
          )

          when(mockConnector.getProtectors(any())(any(), any()))
            .thenReturn(Future.successful(Protectors(individuals, Nil)))

          val result = Await.result(service.getIndividualNinos(identifier, Some(0)), Duration.Inf)

          result mustBe List("nino2")
        }
      }
    }
  }

}
