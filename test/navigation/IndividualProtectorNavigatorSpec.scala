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
import controllers.individual.add.{routes => addRts}
import controllers.individual.amend.{routes => amendRts}
import controllers.individual.{routes => rts}
import models.{CheckMode, CombinedPassportOrIdCard, Mode, NormalMode, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.individual._

import java.time.LocalDate

class IndividualProtectorNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  private val navigator = injector.instanceOf[IndividualProtectorNavigator]

  private val index: Int = 0
  private val nino = "AA000000A"
  private val passportOrId = CombinedPassportOrIdCard("FR", "num", LocalDate.parse("2020-01-01"))

  "Individual protector navigator" when {

      "taxable" must {

        "add journey navigation" must {

          val mode: Mode = NormalMode
          val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = true)

          "Name page -> Do you know date of birth page" in {
            navigator.nextPage(NamePage, mode, baseAnswers)
              .mustBe(rts.DateOfBirthYesNoController.onPageLoad(mode))
          }

          "Do you know date of birth page" when {
            val page = DateOfBirthYesNoPage

            "-> Yes -> Date of birth page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.DateOfBirthController.onPageLoad(mode))
            }

            "-> No -> Do you know country of nationality page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(mode))
            }
          }

          "Date of birth page -> Do you know country of nationality page" in {
            navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
              .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "Do you know country of nationality page" when {
            val page = CountryOfNationalityYesNoPage

            "-> Yes -> Has UK country of nationality page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityUkYesNoController.onPageLoad(mode))
            }

            "-> No -> Do you know NINO page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode))
            }
          }

          "Has UK country of nationality page" when {
            val page = CountryOfNationalityUkYesNoPage

            "-> Yes -> Do you know NINO page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode))
            }

            "-> No -> Country of nationality page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityController.onPageLoad(mode))
            }
          }

          "Country of nationality page -> Do you know NINO page" in {
            navigator.nextPage(CountryOfNationalityPage, mode, baseAnswers)
              .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }

          "Do you know NINO page" when {
            val page = NationalInsuranceNumberYesNoPage

            "-> Yes -> NINO page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.NationalInsuranceNumberController.onPageLoad(mode))
            }

            "-> No -> Do you know country of residence page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
            }
          }

          "NINO page -> Do you know country of residence page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberPage, nino).success.value

            navigator.nextPage(NationalInsuranceNumberPage, mode, answers)
              .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Do you know country of residence page" when {
            val page = CountryOfResidenceYesNoPage

            "-> Yes -> Has UK country of residence page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceUkYesNoController.onPageLoad(mode))
            }

            "-> No" when {
              "NINO provided -> Has mental capacity page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberYesNoPage, true).success.value
                  .set(NationalInsuranceNumberPage, nino).success.value
                  .set(page, false).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
              }

              "NINO not provided -> Do you know address page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberYesNoPage, false).success.value
                  .set(page, false).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(rts.AddressYesNoController.onPageLoad(mode))
              }
            }
          }

          "Has UK country of residence page" when {
            val page = CountryOfResidenceUkYesNoPage

            "-> Yes" when {
              "NINO provided -> Has mental capacity page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberYesNoPage, true).success.value
                  .set(NationalInsuranceNumberPage, nino).success.value
                  .set(page, true).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
              }

              "NINO not provided -> Do you know address page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberYesNoPage, false).success.value
                  .set(page, true).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(rts.AddressYesNoController.onPageLoad(mode))
              }
            }

            "-> No -> Country of residence page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceController.onPageLoad(mode))
            }
          }

          "Country of residence page" when {
            val page = CountryOfResidencePage
            "NINO provided -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(NationalInsuranceNumberYesNoPage, true).success.value
                .set(NationalInsuranceNumberPage, nino).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }

            "NINO not provided -> Do you know address page" in {
              val answers = baseAnswers
                .set(NationalInsuranceNumberYesNoPage, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.AddressYesNoController.onPageLoad(mode))
            }
          }

          "Do you know address page" when {
            val page = AddressYesNoPage

            "-> Yes -> Is address in UK page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.LiveInTheUkYesNoController.onPageLoad(mode))
            }

            "-> No -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }
          }

          "Is address in UK page" when {
            val page = LiveInTheUkYesNoPage

            "-> Yes -> UK address page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.UkAddressController.onPageLoad(mode))
            }

            "-> No -> Non-UK address page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.NonUkAddressController.onPageLoad(mode))
            }
          }

          "UK address page" when {
            "combined passport/id card details not present" must {
              "-> Do you know passport details yes/no page" in {
                navigator.nextPage(UkAddressPage, mode, baseAnswers)
                  .mustBe(rts.PassportDetailsYesNoController.onPageLoad(mode))
              }
            }

            "combined passport/id card details yes/no present" must {
              "-> Do you know passport or ID card details yes/no page" in {
                val answers = baseAnswers
                  .set(PassportOrIdCardDetailsYesNoPage, false).success.value

                navigator.nextPage(UkAddressPage, mode, answers)
                  .mustBe(rts.PassportOrIdCardDetailsYesNoController.onPageLoad())
              }
            }

            "combined passport/id card details present" must {
              "-> Do you know passport or ID card details yes/no page" in {
                val answers = baseAnswers
                  .set(PassportOrIdCardDetailsPage, passportOrId).success.value

                navigator.nextPage(UkAddressPage, mode, answers)
                  .mustBe(rts.PassportOrIdCardDetailsYesNoController.onPageLoad())
              }
            }
          }

          "Non-UK address page" when {
            "combined passport/id card details not present" must {
              "-> Do you know passport details yes/no page" in {
                navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
                  .mustBe(rts.PassportDetailsYesNoController.onPageLoad(mode))
              }
            }

            "combined passport/id card details yes/no present" must {
              "-> Do you know passport or ID card details yes/no page" in {
                val answers = baseAnswers
                  .set(PassportOrIdCardDetailsYesNoPage, false).success.value

                navigator.nextPage(NonUkAddressPage, mode, answers)
                  .mustBe(rts.PassportOrIdCardDetailsYesNoController.onPageLoad())
              }
            }

            "combined passport/id card details present" must {
              "-> Do you know passport or ID card details yes/no page" in {
                val answers = baseAnswers
                  .set(PassportOrIdCardDetailsPage, passportOrId).success.value

                navigator.nextPage(NonUkAddressPage, mode, answers)
                  .mustBe(rts.PassportOrIdCardDetailsYesNoController.onPageLoad())
              }
            }
          }

          "Do you know passport details page" when {
            val page = PassportDetailsYesNoPage

            "-> Yes -> Passport details page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.PassportDetailsController.onPageLoad(mode))
            }

            "-> No -> Do you know ID card details page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.IdCardDetailsYesNoController.onPageLoad(mode))
            }
          }

          "Passport details page -> Has mental capacity page" in {
            navigator.nextPage(PassportDetailsPage, mode, baseAnswers)
              .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Do you know ID card details page" when {
            val page = IdCardDetailsYesNoPage

            "-> Yes -> ID card details page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.IdCardDetailsController.onPageLoad(mode))
            }

            "-> No -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }
          }

          "ID card details page -> Has mental capacity page" in {
            navigator.nextPage(IdCardDetailsPage, mode, baseAnswers)
              .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Has mental capacity page -> Start date page" in {
            navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
              .mustBe(addRts.StartDateController.onPageLoad())
          }

          "Start date page -> Check details" in {
            navigator.nextPage(StartDatePage, mode, baseAnswers)
              .mustBe(addRts.CheckDetailsController.onPageLoad())
          }

        }

        "amend journey navigation" must {

          val mode: Mode = CheckMode
          val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = true)
            .set(IndexPage, index).success.value

          "Name page -> Do you know date of birth page" in {
            navigator.nextPage(NamePage, mode, baseAnswers)
              .mustBe(rts.DateOfBirthYesNoController.onPageLoad(mode))
          }

          "Do you know date of birth page" when {
            val page = DateOfBirthYesNoPage

            "-> Yes -> Date of birth page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.DateOfBirthController.onPageLoad(mode))
            }

            "-> No -> Do you know country of nationality page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(mode))
            }
          }

          "Date of birth page -> Do you know country of nationality page" in {
            navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
              .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "Do you know country of nationality page" when {
            val page = CountryOfNationalityYesNoPage

            "-> Yes -> Has UK country of nationality page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityUkYesNoController.onPageLoad(mode))
            }

            "-> No -> Do you know NINO page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode))
            }
          }

          "Has UK country of nationality page" when {
            val page = CountryOfNationalityUkYesNoPage

            "-> Yes -> Do you know NINO page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode))
            }

            "-> No -> Country of nationality page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityController.onPageLoad(mode))
            }
          }

          "Country of nationality page -> Do you know NINO page" in {
            navigator.nextPage(CountryOfNationalityPage, mode, baseAnswers)
              .mustBe(rts.NationalInsuranceNumberYesNoController.onPageLoad(mode))
          }

          "Do you know NINO page" when {
            val page = NationalInsuranceNumberYesNoPage

            "-> Yes -> NINO page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.NationalInsuranceNumberController.onPageLoad(mode))
            }

            "-> No -> Do you know country of residence page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
            }
          }

          "NINO page -> Do you know country of residence page" in {
            val answers = baseAnswers
              .set(NationalInsuranceNumberPage, nino).success.value

            navigator.nextPage(NationalInsuranceNumberPage, mode, answers)
              .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Do you know country of residence page" when {
            val page = CountryOfResidenceYesNoPage

            "-> Yes -> Has UK country of residence page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceUkYesNoController.onPageLoad(mode))
            }

            "-> No" when {
              "NINO provided -> Has mental capacity page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberYesNoPage, true).success.value
                  .set(NationalInsuranceNumberPage, nino).success.value
                  .set(page, false).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
              }

              "NINO not provided -> Do you know address page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberYesNoPage, false).success.value
                  .set(page, false).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(rts.AddressYesNoController.onPageLoad(mode))
              }
            }
          }

          "Has UK country of residence page" when {
            val page = CountryOfResidenceUkYesNoPage

            "-> Yes" when {
              "NINO provided -> Has mental capacity page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberYesNoPage, true).success.value
                  .set(NationalInsuranceNumberPage, nino).success.value
                  .set(page, true).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
              }

              "NINO not provided -> Do you know address page" in {
                val answers = baseAnswers
                  .set(NationalInsuranceNumberYesNoPage, false).success.value
                  .set(page, true).success.value

                navigator.nextPage(page, mode, answers)
                  .mustBe(rts.AddressYesNoController.onPageLoad(mode))
              }
            }

            "-> No -> Country of residence page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceController.onPageLoad(mode))
            }
          }

          "Country of residence page" when {
            val page = CountryOfResidencePage
            "NINO provided -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(NationalInsuranceNumberYesNoPage, true).success.value
                .set(NationalInsuranceNumberPage, nino).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }

            "NINO not provided -> Do you know address page" in {
              val answers = baseAnswers
                .set(NationalInsuranceNumberYesNoPage, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.AddressYesNoController.onPageLoad(mode))
            }
          }

          "Do you know address page" when {
            val page = AddressYesNoPage

            "-> Yes -> Is address in UK page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.LiveInTheUkYesNoController.onPageLoad(mode))
            }

            "-> No -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }
          }

          "Is address in UK page" when {
            val page = LiveInTheUkYesNoPage

            "-> Yes -> UK address page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.UkAddressController.onPageLoad(mode))
            }

            "-> No -> Non-UK address page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.NonUkAddressController.onPageLoad(mode))
            }
          }

          "UK address page" when {
            "combined passport/id card details not present" must {
              "-> Do you know passport details yes/no page" in {
                navigator.nextPage(UkAddressPage, mode, baseAnswers)
                  .mustBe(rts.PassportDetailsYesNoController.onPageLoad(mode))
              }
            }

            "combined passport/id card details yes/no present" must {
              "-> Mental Capacity yes/no page" in {
                val answers = baseAnswers
                  .set(PassportOrIdCardDetailsYesNoPage, false).success.value

                navigator.nextPage(UkAddressPage, mode, answers)
                  .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
              }
            }

            "combined passport/id card details present" must {
              "-> Mental Capacity yes/no page" in {
                val answers = baseAnswers
                  .set(PassportOrIdCardDetailsPage, passportOrId).success.value

                navigator.nextPage(UkAddressPage, mode, answers)
                  .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
              }
            }
          }

          "Non-UK address page" when {
            "combined passport/id card details not present" must {
              "-> Do you know passport details yes/no page" in {
                navigator.nextPage(NonUkAddressPage, mode, baseAnswers)
                  .mustBe(rts.PassportDetailsYesNoController.onPageLoad(mode))
              }
            }

            "combined passport/id card details yes/no present" must {
              "-> Mental Capacity yes/no page" in {
                val answers = baseAnswers
                  .set(PassportOrIdCardDetailsYesNoPage, false).success.value

                navigator.nextPage(NonUkAddressPage, mode, answers)
                  .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
              }
            }

            "combined passport/id card details present" must {
              "-> Mental Capacity yes/no page" in {
                val answers = baseAnswers
                  .set(PassportOrIdCardDetailsPage, passportOrId).success.value

                navigator.nextPage(NonUkAddressPage, mode, answers)
                  .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
              }
            }
          }

          "Do you know passport details page" when {
            val page = PassportDetailsYesNoPage

            "-> Yes -> Passport details page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.PassportDetailsController.onPageLoad(mode))
            }

            "-> No -> Do you know ID card details page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.IdCardDetailsYesNoController.onPageLoad(mode))
            }
          }

          "Passport details page -> Has mental capacity page" in {
            navigator.nextPage(PassportDetailsPage, mode, baseAnswers)
              .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Do you know ID card details page" when {
            val page = IdCardDetailsYesNoPage

            "-> Yes -> ID card details page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.IdCardDetailsController.onPageLoad(mode))
            }

            "-> No -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }
          }

          "ID card details page -> Has mental capacity page" in {
            navigator.nextPage(IdCardDetailsPage, mode, baseAnswers)
              .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Has mental capacity page -> Check Details page" in {
            navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
              .mustBe(amendRts.CheckDetailsController.renderFromUserAnswers(index))
          }

        }

      }

      "non-taxable" must {

        "add journey navigation" must {

          val mode: Mode = NormalMode
          val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = false)

          "Name page -> Do you know date of birth page" in {
            navigator.nextPage(NamePage, mode, baseAnswers)
              .mustBe(rts.DateOfBirthYesNoController.onPageLoad(mode))
          }

          "Do you know date of birth page" when {
            val page = DateOfBirthYesNoPage

            "-> Yes -> Date of birth page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.DateOfBirthController.onPageLoad(mode))
            }

            "-> No -> Do you know country of nationality page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(mode))
            }
          }

          "Date of birth page -> Do you know country of nationality page" in {
            navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
              .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "Do you know country of nationality page" when {
            val page = CountryOfNationalityYesNoPage

            "-> Yes -> Has UK country of nationality page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityUkYesNoController.onPageLoad(mode))
            }

            "-> No -> Do you know country of residence page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
            }
          }

          "Has UK country of nationality page" when {
            val page = CountryOfNationalityUkYesNoPage

            "-> Yes -> Do you know country of residence page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
            }

            "-> No -> Country of nationality page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityController.onPageLoad(mode))
            }
          }

          "Country of nationality page -> Do you know country of residence page" in {
            navigator.nextPage(CountryOfNationalityPage, mode, baseAnswers)
              .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Do you know country of residence page" when {
            val page = CountryOfResidenceYesNoPage

            "-> Yes -> Has UK country of residence page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceUkYesNoController.onPageLoad(mode))
            }

            "-> No -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }
          }

          "Has UK country of residence page" when {
            val page = CountryOfResidenceUkYesNoPage

            "-> Yes -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }

            "-> No -> Country of residence page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceController.onPageLoad(mode))
            }
          }

          "Country of residence page -> Has mental capacity page" in {
            navigator.nextPage(CountryOfResidencePage, mode, baseAnswers)
              .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Has mental capacity page -> Start date page" in {
            navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
              .mustBe(addRts.StartDateController.onPageLoad())
          }

          "Start date page -> Check details" in {
            navigator.nextPage(StartDatePage, mode, baseAnswers)
              .mustBe(addRts.CheckDetailsController.onPageLoad())
          }

        }

        "amend journey navigation" must {

          val mode: Mode = CheckMode
          val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = false)
            .set(IndexPage, index).success.value

          "Name page -> Do you know date of birth page" in {
            navigator.nextPage(NamePage, mode, baseAnswers)
              .mustBe(rts.DateOfBirthYesNoController.onPageLoad(mode))
          }

          "Do you know date of birth page" when {
            val page = DateOfBirthYesNoPage

            "-> Yes -> Date of birth page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.DateOfBirthController.onPageLoad(mode))
            }

            "-> No -> Do you know country of nationality page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(mode))
            }
          }

          "Date of birth page -> Do you know country of nationality page" in {
            navigator.nextPage(DateOfBirthPage, mode, baseAnswers)
              .mustBe(rts.CountryOfNationalityYesNoController.onPageLoad(mode))
          }

          "Do you know country of nationality page" when {
            val page = CountryOfNationalityYesNoPage

            "-> Yes -> Has UK country of nationality page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityUkYesNoController.onPageLoad(mode))
            }

            "-> No -> Do you know country of residence page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
            }
          }

          "Has UK country of nationality page" when {
            val page = CountryOfNationalityUkYesNoPage

            "-> Yes -> Do you know country of residence page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
            }

            "-> No -> Country of nationality page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfNationalityController.onPageLoad(mode))
            }
          }

          "Country of nationality page -> Do you know country of residence page" in {
            navigator.nextPage(CountryOfNationalityPage, mode, baseAnswers)
              .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(mode))
          }

          "Do you know country of residence page" when {
            val page = CountryOfResidenceYesNoPage

            "-> Yes -> Has UK country of residence page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceUkYesNoController.onPageLoad(mode))
            }

            "-> No -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }
          }

          "Has UK country of residence page" when {
            val page = CountryOfResidenceUkYesNoPage

            "-> Yes -> Has mental capacity page" in {
              val answers = baseAnswers
                .set(page, true).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
            }

            "-> No -> Country of residence page" in {
              val answers = baseAnswers
                .set(page, false).success.value

              navigator.nextPage(page, mode, answers)
                .mustBe(rts.CountryOfResidenceController.onPageLoad(mode))
            }
          }

          "Country of residence page -> Has mental capacity page" in {
            navigator.nextPage(CountryOfResidencePage, mode, baseAnswers)
              .mustBe(rts.MentalCapacityYesNoController.onPageLoad(mode))
          }

          "Has mental capacity page -> Check Details page" in {
            navigator.nextPage(MentalCapacityYesNoPage, mode, baseAnswers)
              .mustBe(amendRts.CheckDetailsController.renderFromUserAnswers(index))
          }

        }

      }
  }
}
