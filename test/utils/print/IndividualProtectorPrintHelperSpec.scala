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

import base.SpecBase
import controllers.individual.add.{routes => addRts}
import controllers.individual.{routes => rts}
import models.YesNoDontKnow.{DontKnow, Yes}
import models.{CheckMode, CombinedPassportOrIdCard, IdCard, Mode, Name, NonUkAddress, NormalMode, Passport, UkAddress, UserAnswers}
import pages.individual._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class IndividualProtectorPrintHelperSpec extends SpecBase {

  private val name: Name = Name("First", Some("Middle"), "Last")
  private val ukAddress: UkAddress = UkAddress("value 1", "value 2", None, None, "AB1 1AB")
  private val country = "DE"
  private val nonUkAddress: NonUkAddress = NonUkAddress("value 1", "value 2", None, country)

  private val helper: IndividualProtectorPrintHelper = injector.instanceOf[IndividualProtectorPrintHelper]

  private val baseAnswers: UserAnswers = emptyUserAnswers
    .set(NamePage, name).success.value
    .set(DateOfBirthYesNoPage, true).success.value
    .set(DateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
    .set(CountryOfNationalityYesNoPage, true).success.value
    .set(CountryOfNationalityUkYesNoPage, false).success.value
    .set(CountryOfNationalityPage, country).success.value
    .set(NationalInsuranceNumberYesNoPage, true).success.value
    .set(NationalInsuranceNumberPage, "AA000000A").success.value
    .set(CountryOfResidenceYesNoPage, true).success.value
    .set(CountryOfResidenceUkYesNoPage, false).success.value
    .set(CountryOfResidencePage, country).success.value
    .set(AddressYesNoPage, true).success.value
    .set(LiveInTheUkYesNoPage, true).success.value
    .set(UkAddressPage, ukAddress).success.value
    .set(NonUkAddressPage, nonUkAddress).success.value
    .set(MentalCapacityYesNoPage, Yes).success.value
    .set(StartDatePage, LocalDate.of(2020, 1, 1)).success.value

  "IndividualProtectorPrintHelper" must {

    "generate individual protector section for all possible data" when {

      "adding" in {

        val mode: Mode = NormalMode

        val userAnswers = baseAnswers
          .set(PassportDetailsYesNoPage, true).success.value
          .set(PassportDetailsPage, Passport("GB", "1234567890", LocalDate.of(2030, 10, 10))).success.value
          .set(IdCardDetailsYesNoPage, true).success.value
          .set(IdCardDetailsPage, IdCard("GB", "1234567890", LocalDate.of(2030, 10, 10))).success.value

        val result = helper(userAnswers, adding = true, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("individualProtector.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(rts.NameController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.DateOfBirthYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("10 October 2010"), changeUrl = Some(rts.DateOfBirthController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.CountryOfNationalityYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfNationalityUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(rts.CountryOfNationalityUkYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfNationality.checkYourAnswersLabel", name.displayName), answer = Html("Germany"), changeUrl = Some(rts.CountryOfNationalityController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), changeUrl = Some(rts.NationalInsuranceNumberController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.CountryOfResidenceYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfResidenceUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(rts.CountryOfResidenceUkYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfResidence.checkYourAnswersLabel", name.displayName), answer = Html("Germany"), changeUrl = Some(rts.CountryOfResidenceController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.AddressYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.LiveInTheUkYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.ukAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = Some(rts.UkAddressController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.nonUkAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = Some(rts.NonUkAddressController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.passportDetailsYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.PassportDetailsYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.passportDetails.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />1234567890<br />10 October 2030"), changeUrl = Some(rts.PassportDetailsController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.idCardDetailsYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.IdCardDetailsYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.idCardDetails.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />1234567890<br />10 October 2030"), changeUrl = Some(rts.IdCardDetailsController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.MentalCapacityYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.startDate.checkYourAnswersLabel", name.displayName), answer = Html("1 January 2020"), changeUrl = Some(addRts.StartDateController.onPageLoad().url))
          )
        )
      }

      "amending" in {

        val mode: Mode = CheckMode

        val userAnswers = baseAnswers
          .set(PassportOrIdCardDetailsYesNoPage, true).success.value
          .set(PassportOrIdCardDetailsPage, CombinedPassportOrIdCard("GB", "1234567890", LocalDate.of(2030, 10, 10))).success.value

        val result = helper(userAnswers, adding = false, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("individualProtector.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(rts.NameController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.dateOfBirthYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.DateOfBirthYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.dateOfBirth.checkYourAnswersLabel", name.displayName), answer = Html("10 October 2010"), changeUrl = Some(rts.DateOfBirthController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfNationalityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.CountryOfNationalityYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfNationalityUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(rts.CountryOfNationalityUkYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfNationality.checkYourAnswersLabel", name.displayName), answer = Html("Germany"), changeUrl = Some(rts.CountryOfNationalityController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.nationalInsuranceNumberYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.nationalInsuranceNumber.checkYourAnswersLabel", name.displayName), answer = Html("AA 00 00 00 A"), changeUrl = Some(rts.NationalInsuranceNumberController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfResidenceYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.CountryOfResidenceYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfResidenceUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("No"), changeUrl = Some(rts.CountryOfResidenceUkYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.countryOfResidence.checkYourAnswersLabel", name.displayName), answer = Html("Germany"), changeUrl = Some(rts.CountryOfResidenceController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.addressYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.AddressYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.liveInTheUkYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.LiveInTheUkYesNoController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.ukAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />AB1 1AB"), changeUrl = Some(rts.UkAddressController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.nonUkAddress.checkYourAnswersLabel", name.displayName), answer = Html("value 1<br />value 2<br />Germany"), changeUrl = Some(rts.NonUkAddressController.onPageLoad(mode).url)),
            AnswerRow(label = messages("individualProtector.passportOrIdCardDetailsYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = None),
            AnswerRow(label = messages("individualProtector.passportOrIdCardDetails.checkYourAnswersLabel", name.displayName), answer = Html("United Kingdom<br />Number ending 7890<br />10 October 2030"), changeUrl = None),
            AnswerRow(label = messages("individualProtector.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("Yes"), changeUrl = Some(rts.MentalCapacityYesNoController.onPageLoad(mode).url))
          )
        )
      }
    }

    "adding with unknown mental capacity data must show `I don’t know or not provided`" in {

        val userAnswers = emptyUserAnswers
          .set(NamePage, name).success.value
          .set(MentalCapacityYesNoPage, DontKnow).success.value

        val result = helper(userAnswers, adding = true, name.displayName)
        result mustBe AnswerSection(
          headingKey = None,
          rows = Seq(
            AnswerRow(label = messages("individualProtector.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(rts.NameController.onPageLoad(NormalMode).url)),
            AnswerRow(label = messages("individualProtector.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("I don’t know or not provided"), changeUrl = Some(rts.MentalCapacityYesNoController.onPageLoad(NormalMode).url)),
          )
        )
      }

    "amending with unknown mental capacity data must show `I don’t know or not provided`" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage, name).success.value
        .set(MentalCapacityYesNoPage, DontKnow).success.value

      val result = helper(userAnswers, adding = false, name.displayName)
      result mustBe AnswerSection(
        headingKey = None,
        rows = Seq(
          AnswerRow(label = messages("individualProtector.name.checkYourAnswersLabel"), answer = Html("First Middle Last"), changeUrl = Some(rts.NameController.onPageLoad(CheckMode).url)),
          AnswerRow(label = messages("individualProtector.mentalCapacityYesNo.checkYourAnswersLabel", name.displayName), answer = Html("I don’t know or not provided"), changeUrl = Some(rts.MentalCapacityYesNoController.onPageLoad(CheckMode).url)),
        )
      )
    }

  }
}
