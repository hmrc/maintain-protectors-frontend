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

package pages.business

import java.time.LocalDate

import models.{NonUkAddress, UserAnswers}
import pages.behaviours.PageBehaviours


class UtrYesNoPageSpec extends PageBehaviours {

  private val internalId = "id"
  private val identifier = "utr"
  private val sessionId = "sessionId"
  private val newId = s"$internalId-$identifier-$sessionId"

  "UtrYesNoPage" must {

    beRetrievable[Boolean](UtrYesNoPage)

    beSettable[Boolean](UtrYesNoPage)

    beRemovable[Boolean](UtrYesNoPage)

    "implement cleanup logic when NO selected" in {
      val userAnswers = UserAnswers(internalId, identifier, sessionId, newId, LocalDate.now)
        .set(UtrPage, "1234567890")
        .flatMap(_.set(UtrYesNoPage, false))

      userAnswers.get.get(UtrPage) mustNot be(defined)
    }

    "implement cleanup logic when YES selected" in {
      val userAnswers = UserAnswers(identifier, internalId, sessionId, newId, LocalDate.now)
        .set(AddressYesNoPage, true)
        .flatMap(_.set(AddressUkYesNoPage, false))
        .flatMap(_.set(NonUkAddressPage, NonUkAddress("line1", "line2", None,"country")))
        .flatMap(_.set(UtrYesNoPage, true))

      userAnswers.get.get(AddressYesNoPage) mustNot be(defined)
      userAnswers.get.get(AddressUkYesNoPage) mustNot be(defined)
      userAnswers.get.get(NonUkAddressPage) mustNot be(defined)
    }
  }
}
