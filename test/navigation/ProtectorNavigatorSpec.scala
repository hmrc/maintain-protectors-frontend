/*
 * Copyright 2024 HM Revenue & Customs
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

package navigation

import base.SpecBase
import models.Constant.MAX
import models.protectors.{BusinessProtector, IndividualProtector, Protectors}
import models.{Name, NormalMode, ProtectorType}

import java.time.LocalDate

class ProtectorNavigatorSpec extends SpecBase {

  private val navigator: ProtectorNavigator = injector.instanceOf[ProtectorNavigator]

  "ProtectorNavigator" when {

    ".addProtectorRoute" when {

      "individuals maxed out" must {
        "redirect to business name page" in {

          val settlor = IndividualProtector(
            name = Name(firstName = "Joe", middleName = None, lastName = "Bloggs"),
            dateOfBirth = None,
            countryOfNationality = None,
            countryOfResidence = None,
            identification = None,
            address = None,
            mentalCapacityYesNo = None,
            entityStart = LocalDate.parse("2020-01-01"),
            provisional = false
          )

          val protectors = Protectors(List.fill(MAX)(settlor), Nil)

          navigator.addProtectorRoute(protectors).url mustBe
            controllers.business.routes.NameController.onPageLoad(NormalMode).url
        }
      }

      "businesses maxed out" must {
        "redirect to individual name page" in {

          val settlor = BusinessProtector(
            name = "Amazon",
            utr = None,
            countryOfResidence = None,
            address = None,
            entityStart = LocalDate.parse("2020-01-01"),
            provisional = false
          )

          val protectors = Protectors(Nil, List.fill(MAX)(settlor))

          navigator.addProtectorRoute(protectors).url mustBe
            controllers.individual.routes.NameController.onPageLoad(NormalMode).url
        }
      }

      "neither maxed out" must {
        "redirect to add now page" in {

          val protectors = Protectors(Nil, Nil)

          navigator.addProtectorRoute(protectors).url mustBe
            controllers.routes.AddNowController.onPageLoad().url
        }
      }
    }

    ".addSettlorNowRoute" when {

      "individual" must {
        "redirect to individual name page" in {

          navigator.addProtectorNowRoute(ProtectorType.IndividualProtector).url mustBe
            controllers.individual.routes.NameController.onPageLoad(NormalMode).url
        }
      }

      "business" must {
        "redirect to business name page" in {

          navigator.addProtectorNowRoute(ProtectorType.BusinessProtector).url mustBe
            controllers.business.routes.NameController.onPageLoad(NormalMode).url
        }
      }
    }
  }

}
