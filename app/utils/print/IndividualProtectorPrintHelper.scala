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
import controllers.individual.amend.{routes => amendRts}
import controllers.individual.{routes => rts}
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.individual._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class IndividualProtectorPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def apply(userAnswers: UserAnswers, provisional: Boolean, protectorName: String)(implicit messages: Messages): AnswerSection = {

    val bound = answerRowConverter.bind(userAnswers, protectorName)

    def answerRows(mode: Mode): Seq[Option[AnswerRow]] = Seq(
      bound.nameQuestion(NamePage, "individualProtector.name", rts.NameController.onPageLoad(mode).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage, "individualProtector.dateOfBirthYesNo", rts.DateOfBirthYesNoController.onPageLoad(mode).url),
      bound.dateQuestion(DateOfBirthPage, "individualProtector.dateOfBirth", rts.DateOfBirthController.onPageLoad(mode).url),
      bound.yesNoQuestion(NationalInsuranceNumberYesNoPage, "individualProtector.nationalInsuranceNumberYesNo", rts.NationalInsuranceNumberYesNoController.onPageLoad(mode).url),
      bound.ninoQuestion(NationalInsuranceNumberPage, "individualProtector.nationalInsuranceNumber", rts.NationalInsuranceNumberController.onPageLoad(mode).url),
      bound.yesNoQuestion(AddressYesNoPage, "individualProtector.addressYesNo", rts.AddressYesNoController.onPageLoad(mode).url),
      bound.yesNoQuestion(LiveInTheUkYesNoPage, "individualProtector.liveInTheUkYesNo", rts.LiveInTheUkYesNoController.onPageLoad(mode).url),
      bound.addressQuestion(UkAddressPage, "individualProtector.ukAddress", rts.UkAddressController.onPageLoad(mode).url),
      bound.addressQuestion(NonUkAddressPage, "individualProtector.nonUkAddress", rts.NonUkAddressController.onPageLoad(mode).url)
    )

    lazy val add: Seq[AnswerRow] = (
      answerRows(NormalMode) ++
        Seq(
          bound.yesNoQuestion(PassportDetailsYesNoPage, "individualProtector.passportDetailsYesNo", addRts.PassportDetailsYesNoController.onPageLoad().url),
          bound.passportDetailsQuestion(PassportDetailsPage, "individualProtector.passportDetails", addRts.PassportDetailsController.onPageLoad().url),
          bound.yesNoQuestion(IdCardDetailsYesNoPage, "individualProtector.idCardDetailsYesNo", addRts.IdCardDetailsYesNoController.onPageLoad().url),
          bound.idCardDetailsQuestion(IdCardDetailsPage, "individualProtector.idCardDetails", addRts.IdCardDetailsController.onPageLoad().url),
          bound.dateQuestion(StartDatePage, "individualProtector.startDate", addRts.StartDateController.onPageLoad().url)
        )
      ).flatten

    lazy val amend: Seq[AnswerRow] = (
      answerRows(CheckMode) ++
        Seq(
          bound.yesNoQuestion(PassportOrIdCardDetailsYesNoPage, "individualProtector.passportOrIdCardDetailsYesNo", amendRts.PassportOrIdCardDetailsYesNoController.onPageLoad().url),
          bound.passportOrIdCardDetailsQuestion(PassportOrIdCardDetailsPage, "individualProtector.passportOrIdCardDetails", amendRts.PassportOrIdCardDetailsController.onPageLoad().url)
        )
      ).flatten

    AnswerSection(
      None,
      if (provisional) add else amend
    )
  }
}
