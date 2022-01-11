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

package utils.print

import java.time.LocalDate

import controllers.business.{routes => rts}
import base.SpecBase
import models.{CheckMode, NonUkAddress, NormalMode, UkAddress}
import pages.business._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class BusinessProtectorPrintHelperSpec extends SpecBase {

  private val name: String = "Name"
  private val utr: String = "1234567890"
  private val ukAddress: UkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  private val nonUkAddress: NonUkAddress = NonUkAddress("value 1", "value 2", None, "DE")

  "BusinessProtectorPrintHelper" must {

    val userAnswers = emptyUserAnswers
      .set(NamePage, name).success.value
      .set(UtrYesNoPage, true).success.value
      .set(UtrPage, utr).success.value
      .set(CountryOfResidenceYesNoPage, true).success.value
      .set(CountryOfResidenceUkYesNoPage, false).success.value
      .set(CountryOfResidencePage, "FR").success.value
      .set(AddressYesNoPage, true).success.value
      .set(AddressUkYesNoPage, true).success.value
      .set(UkAddressPage, ukAddress).success.value
      .set(NonUkAddressPage, nonUkAddress).success.value
      .set(StartDatePage, LocalDate.of(2020, 1, 1)).success.value

    val helper = injector.instanceOf[BusinessProtectorPrintHelper]

    "generate add business protector section for all possible data" in {

      val mode = NormalMode

      val result = helper(userAnswers, adding = true, name)

      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = messages("businessProtector.name.checkYourAnswersLabel"), answer = Html("Name"), changeUrl = Some(rts.NameController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.utrYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(rts.UtrYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.utr.checkYourAnswersLabel", name), answer = Html("1234567890"), changeUrl = Some(rts.UtrController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.countryOfResidenceYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(rts.CountryOfResidenceYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.countryOfResidenceUkYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = Some(rts.CountryOfResidenceUkYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.countryOfResidence.checkYourAnswersLabel", name), answer = Html("France"), changeUrl = Some(rts.CountryOfResidenceController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.addressYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(rts.AddressYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.addressUkYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(rts.AddressUkYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.ukAddress.checkYourAnswersLabel", name), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = Some(rts.UkAddressController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.nonUkAddress.checkYourAnswersLabel", name), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = Some(rts.NonUkAddressController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.startDate.checkYourAnswersLabel", name), answer = Html("1 January 2020"), changeUrl = Some(rts.StartDateController.onPageLoad().url))
        )
      )
    }

    "generate amend business protector section for all possible data" in {

      val mode = CheckMode

      val result = helper(userAnswers, adding = false, name)

      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = messages("businessProtector.name.checkYourAnswersLabel"), answer = Html("Name"), changeUrl = Some(rts.NameController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.utrYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(rts.UtrYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.utr.checkYourAnswersLabel", name), answer = Html("1234567890"), changeUrl = Some(rts.UtrController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.countryOfResidenceYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(rts.CountryOfResidenceYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.countryOfResidenceUkYesNo.checkYourAnswersLabel", name), answer = Html("No"), changeUrl = Some(rts.CountryOfResidenceUkYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.countryOfResidence.checkYourAnswersLabel", name), answer = Html("France"), changeUrl = Some(rts.CountryOfResidenceController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.addressYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(rts.AddressYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.addressUkYesNo.checkYourAnswersLabel", name), answer = Html("Yes"), changeUrl = Some(rts.AddressUkYesNoController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.ukAddress.checkYourAnswersLabel", name), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = Some(rts.UkAddressController.onPageLoad(mode).url)),
          AnswerRow(label = messages("businessProtector.nonUkAddress.checkYourAnswersLabel", name), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = Some(rts.NonUkAddressController.onPageLoad(mode).url))
        )
      )
    }
  }
}
