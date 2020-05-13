/*
 * Copyright 2020 HM Revenue & Customs
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

package extractors

import com.google.inject.Inject
import models.protectors.BusinessProtector
import models.{Address, NonUkAddress, UkAddress, UserAnswers}
import pages.business._

import scala.util.Try

class BusinessProtectorExtractor @Inject()() {

  def apply(answers: UserAnswers, business : BusinessProtector, index: Int): Try[UserAnswers] =
    answers.deleteAtPath(pages.business.basePath)
      .flatMap(_.set(NamePage, business.name))
      .flatMap(answers => extractAddress(business.address, answers))
      .flatMap(answers => extractUtr(business.utr, answers))
      .flatMap(_.set(StartDatePage, business.entityStart))
      .flatMap(_.set(IndexPage, index))

  private def extractUtr(utr: Option[String], answers: UserAnswers) : Try[UserAnswers] = {
    utr match {
      case Some(utr) =>
        answers.set(UtrYesNoPage, true)
        .flatMap(_.set(UtrPage, utr))
      case _ => answers.set(UtrYesNoPage, false)
    }
  }

  private def extractAddress(address: Option[Address], answers: UserAnswers) : Try[UserAnswers] = {
    address match {
      case Some(uk: UkAddress) =>
        answers.set(AddressYesNoPage, true)
          .flatMap(_.set(AddressUkYesNoPage, true))
          .flatMap(_.set(UkAddressPage, uk))
      case Some(nonUk: NonUkAddress) =>
        answers.set(AddressYesNoPage, true)
          .flatMap(_.set(AddressUkYesNoPage, false))
          .flatMap(_.set(NonUkAddressPage, nonUk))
      case _ =>
        answers.set(AddressYesNoPage, false)
    }
  }
}