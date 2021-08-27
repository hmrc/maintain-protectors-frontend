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

package utils.print

import com.google.inject.Inject
import controllers.individual.add.{routes => addRts}
import controllers.individual.{routes => rts}
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.individual._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class IndividualProtectorPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers, adding: Boolean, protectorName: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, protectorName)

    def answerRows: Seq[AnswerRow] = {
      val mode: Mode = if (adding) NormalMode else CheckMode
      Seq(
        bound.nameQuestion(NamePage, "individualProtector.name", rts.NameController.onPageLoad(mode).url),
        bound.yesNoQuestion(DateOfBirthYesNoPage, "individualProtector.dateOfBirthYesNo", rts.DateOfBirthYesNoController.onPageLoad(mode).url),
        bound.dateQuestion(DateOfBirthPage, "individualProtector.dateOfBirth", rts.DateOfBirthController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfNationalityYesNoPage, "individualProtector.countryOfNationalityYesNo", rts.CountryOfNationalityYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfNationalityUkYesNoPage, "individualProtector.countryOfNationalityUkYesNo", rts.CountryOfNationalityUkYesNoController.onPageLoad(mode).url),
        bound.countryQuestion(CountryOfNationalityUkYesNoPage, CountryOfNationalityPage, "individualProtector.countryOfNationality", rts.CountryOfNationalityController.onPageLoad(mode).url),
        bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individualProtector.nationalInsuranceNumberYesNo", rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
        bound.ninoQuestion(NationalInsuranceNumberPage, "individualProtector.nationalInsuranceNumber", rts.NationalInsuranceNumberController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfResidenceYesNoPage, "individualProtector.countryOfResidenceYesNo", rts.CountryOfResidenceYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(CountryOfResidenceUkYesNoPage, "individualProtector.countryOfResidenceUkYesNo", rts.CountryOfResidenceUkYesNoController.onPageLoad(mode).url),
        bound.countryQuestion(CountryOfResidenceUkYesNoPage, CountryOfResidencePage, "individualProtector.countryOfResidence", rts.CountryOfResidenceController.onPageLoad(mode).url),
        bound.yesNoQuestion(AddressYesNoPage, "individualProtector.addressYesNo", rts.AddressYesNoController.onPageLoad(mode).url),
        bound.yesNoQuestion(LiveInTheUkYesNoPage, "individualProtector.liveInTheUkYesNo", rts.LiveInTheUkYesNoController.onPageLoad(mode).url),
        bound.addressQuestion(UkAddressPage, "individualProtector.ukAddress", rts.UkAddressController.onPageLoad(mode).url),
        bound.addressQuestion(NonUkAddressPage, "individualProtector.nonUkAddress", rts.NonUkAddressController.onPageLoad(mode).url),
        bound.yesNoQuestion(PassportDetailsYesNoPage, "individualProtector.passportDetailsYesNo", rts.PassportDetailsYesNoController.onPageLoad(mode).url),
        bound.passportDetailsQuestion(PassportDetailsPage, "individualProtector.passportDetails", rts.PassportDetailsController.onPageLoad(mode).url),
        bound.yesNoQuestion(IdCardDetailsYesNoPage, "individualProtector.idCardDetailsYesNo", rts.IdCardDetailsYesNoController.onPageLoad(mode).url),
        bound.idCardDetailsQuestion(IdCardDetailsPage, "individualProtector.idCardDetails", rts.IdCardDetailsController.onPageLoad(mode).url),
        bound.yesNoQuestion(PassportOrIdCardDetailsYesNoPage, "individualProtector.passportOrIdCardDetailsYesNo", rts.PassportOrIdCardDetailsYesNoController.onPageLoad(mode).url),
        bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, "individualProtector.passportOrIdCardDetails", rts.PassportOrIdCardDetailsController.onPageLoad(mode).url),
        bound.yesNoQuestion(MentalCapacityYesNoPage, "individualProtector.mentalCapacityYesNo", rts.MentalCapacityYesNoController.onPageLoad(mode).url),
        if (adding) bound.dateQuestion(StartDatePage, "individualProtector.startDate", addRts.StartDateController.onPageLoad().url) else None
      ).flatten
    }

    AnswerSection(headingKey = None, rows = answerRows)
  }
}
