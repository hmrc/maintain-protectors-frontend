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

package utils.mappers

import base.SpecBase
import models.Constant.GB
import models.YesNoDontKnow.{No, Yes}
import models.protectors.IndividualProtector
import models.{CombinedPassportOrIdCard, IdCard, Name, NationalInsuranceNumber, NonUkAddress, Passport, UkAddress}
import pages.individual._

import java.time.LocalDate

class IndividualProtectorMapperSpec extends SpecBase {

  private val name = Name("First", None, "Last")
  private val dateOfBirth = LocalDate.parse("2010-02-03")
  private val startDate = LocalDate.parse("2019-03-09")
  private val ukAddress = UkAddress("line1", "line2", Some("line3"), Some("line4"), "POSTCODE")
  private val nonUkAddress = NonUkAddress("line1", "line2", Some("line3"), "country")
  private val nino = "AA123456A"

  private val mapper = injector.instanceOf[IndividualProtectorMapper]

  "IndividualProtectorMapper" when {

      "generate individual model with nino" in {

        val userAnswers = emptyUserAnswers
          .set(NamePage, name).success.value
          .set(DateOfBirthPage, dateOfBirth).success.value
          .set(NationalInsuranceNumberYesNoPage, true).success.value
          .set(NationalInsuranceNumberPage, nino).success.value
          .set(StartDatePage, startDate).success.value

        val result = mapper(userAnswers).get

        result.name mustBe name
        result.dateOfBirth mustBe Some(dateOfBirth)
        result.identification mustBe Some(NationalInsuranceNumber(nino))
        result.address mustBe None
        result.entityStart mustBe startDate
      }

      "generate individual model with UK address" in {

        val userAnswers = emptyUserAnswers
          .set(NamePage, name).success.value
          .set(DateOfBirthPage, dateOfBirth).success.value
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(PassportDetailsYesNoPage, false).success.value
          .set(IdCardDetailsYesNoPage, false).success.value
          .set(AddressYesNoPage, true).success.value
          .set(LiveInTheUkYesNoPage, true).success.value
          .set(UkAddressPage, ukAddress).success.value
          .set(StartDatePage, startDate).success.value

        val result = mapper(userAnswers).get

        result.name mustBe name
        result.dateOfBirth mustBe Some(dateOfBirth)
        result.identification mustBe None
        result.address mustBe Some(ukAddress)
        result.entityStart mustBe startDate
      }

      "generate individual model with non-UK address" in {

        val userAnswers = emptyUserAnswers
          .set(NamePage, name).success.value
          .set(DateOfBirthPage, dateOfBirth).success.value
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(PassportDetailsYesNoPage, false).success.value
          .set(IdCardDetailsYesNoPage, false).success.value
          .set(AddressYesNoPage, true).success.value
          .set(LiveInTheUkYesNoPage, false).success.value
          .set(NonUkAddressPage, nonUkAddress).success.value
          .set(StartDatePage, startDate).success.value

        val result = mapper(userAnswers).get

        result.name mustBe name
        result.dateOfBirth mustBe Some(dateOfBirth)
        result.identification mustBe None
        result.address mustBe Some(nonUkAddress)
        result.entityStart mustBe startDate
      }

      "generate individual model with passport" in {

        val passport = Passport("SP", "123456789", LocalDate.of(2024, 8, 16))

        val userAnswers = emptyUserAnswers
          .set(NamePage, name).success.value
          .set(DateOfBirthPage, dateOfBirth).success.value
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(AddressYesNoPage, true).success.value
          .set(LiveInTheUkYesNoPage, false).success.value
          .set(NonUkAddressPage, nonUkAddress).success.value
          .set(PassportDetailsYesNoPage, true).success.value
          .set(PassportDetailsPage, passport).success.value
          .set(StartDatePage, startDate).success.value

        val result = mapper(userAnswers).get

        result.name mustBe name
        result.dateOfBirth mustBe Some(dateOfBirth)
        result.identification mustBe Some(passport.asCombined)
        result.address mustBe Some(nonUkAddress)
        result.entityStart mustBe startDate
      }

      "generate individual model with id card" in {

        val idcard = IdCard("SP", "123456789", LocalDate.of(2024, 8, 16))

        val userAnswers = emptyUserAnswers
          .set(NamePage, name).success.value
          .set(DateOfBirthPage, dateOfBirth).success.value
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(AddressYesNoPage, true).success.value
          .set(LiveInTheUkYesNoPage, true).success.value
          .set(UkAddressPage, ukAddress).success.value
          .set(PassportDetailsYesNoPage, false).success.value
          .set(IdCardDetailsYesNoPage, true).success.value
          .set(IdCardDetailsPage, idcard).success.value
          .set(StartDatePage, startDate).success.value

        val result = mapper(userAnswers).get

        result.name mustBe name
        result.dateOfBirth mustBe Some(dateOfBirth)
        result.identification mustBe Some(idcard.asCombined)
        result.address mustBe Some(ukAddress)
        result.entityStart mustBe startDate
      }

      "generate individual model with neither nino nor address" in {

        val userAnswers = emptyUserAnswers
          .set(NamePage, name).success.value
          .set(DateOfBirthPage, dateOfBirth).success.value
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(AddressYesNoPage, false).success.value
          .set(StartDatePage, startDate).success.value

        val result = mapper(userAnswers).get

        result.name mustBe name
        result.dateOfBirth mustBe Some(dateOfBirth)
        result.identification mustBe None
        result.address mustBe None
        result.entityStart mustBe startDate
      }

      "generate individual model with passport or ID card" in {

        val passportOrIdCard = CombinedPassportOrIdCard("SP", "123456789", LocalDate.of(2024, 8, 16))

        val userAnswers = emptyUserAnswers
          .set(NamePage, name).success.value
          .set(DateOfBirthYesNoPage, false).success.value
          .set(NationalInsuranceNumberYesNoPage, false).success.value
          .set(AddressYesNoPage, true).success.value
          .set(LiveInTheUkYesNoPage, true).success.value
          .set(UkAddressPage, ukAddress).success.value
          .set(PassportOrIdCardDetailsYesNoPage, true).success.value
          .set(PassportOrIdCardDetailsPage, passportOrIdCard).success.value
          .set(StartDatePage, startDate).success.value

        val result = mapper(userAnswers).get

        result.name mustBe name
        result.dateOfBirth mustBe None
        result.identification mustBe Some(passportOrIdCard)
        result.address mustBe Some(ukAddress)
        result.entityStart mustBe startDate
      }

      "taxable" when {

        "UK country of nationality, UK country of residence, and legally incapable" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(DateOfBirthYesNoPage, false).success.value
            .set(CountryOfNationalityYesNoPage, true).success.value
            .set(CountryOfNationalityUkYesNoPage, true).success.value
            .set(NationalInsuranceNumberYesNoPage, true).success.value
            .set(NationalInsuranceNumberPage, nino).success.value
            .set(CountryOfResidenceYesNoPage, true).success.value
            .set(CountryOfResidenceUkYesNoPage, true).success.value
            .set(MentalCapacityYesNoPage, No).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers).get

          result mustBe IndividualProtector(
            name = name,
            dateOfBirth = None,
            countryOfNationality = Some(GB),
            identification = Some(NationalInsuranceNumber(nino)),
            countryOfResidence = Some(GB),
            address = None,
            mentalCapacityYesNo = Some(No),
            entityStart = startDate,
            provisional = true
          )
        }
      }

      "non-taxable" must {

        "non-UK country of nationality, non-UK country of residence, and legally capable" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage, name).success.value
            .set(DateOfBirthYesNoPage, false).success.value
            .set(CountryOfNationalityYesNoPage, true).success.value
            .set(CountryOfNationalityUkYesNoPage, false).success.value
            .set(CountryOfNationalityPage, "FR").success.value
            .set(CountryOfResidenceYesNoPage, true).success.value
            .set(CountryOfResidenceUkYesNoPage, false).success.value
            .set(CountryOfResidencePage, "FR").success.value
            .set(MentalCapacityYesNoPage, Yes).success.value
            .set(StartDatePage, startDate).success.value

          val result = mapper(userAnswers).get

          result mustBe IndividualProtector(
            name = name,
            dateOfBirth = None,
            countryOfNationality = Some("FR"),
            identification = None,
            countryOfResidence = Some("FR"),
            address = None,
            mentalCapacityYesNo = Some(Yes),
            entityStart = startDate,
            provisional = true
          )
        }
      }

  }

}
