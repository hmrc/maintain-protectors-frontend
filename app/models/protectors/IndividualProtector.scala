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

package models.protectors

import models.YesNoDontKnow.{No, Yes}
import models.{Address, IndividualIdentification, Name, YesNoDontKnow}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

final case class IndividualProtector(name: Name,
                                     dateOfBirth: Option[LocalDate],
                                     countryOfNationality: Option[String] = None,
                                     identification: Option[IndividualIdentification],
                                     countryOfResidence: Option[String] = None,
                                     address: Option[Address],
                                     mentalCapacityYesNo: Option[YesNoDontKnow] = None,
                                     entityStart: LocalDate,
                                     provisional: Boolean) extends Protector

object IndividualProtector extends ProtectorReads {

  def readMentalCapacity: Reads[Option[YesNoDontKnow]] =
    (__ \ Symbol("legallyIncapable")).readNullable[Boolean].flatMap[Option[YesNoDontKnow]] { x: Option[Boolean] =>
      Reads(_ => JsSuccess(YesNoDontKnow.fromBoolean(x)))
    }

  def legallyIncapableWrites: Writes[YesNoDontKnow] = {
    case Yes => JsBoolean(false)
    case No  => JsBoolean(true)
    case _   => JsNull
  }

  implicit val reads: Reads[IndividualProtector] = (
    (__ \ Symbol("name")).read[Name] and
      (__ \ Symbol("dateOfBirth")).readNullable[LocalDate] and
      (__ \ Symbol("nationality")).readNullable[String] and
      __.lazyRead(readNullableAtSubPath[IndividualIdentification](__ \ Symbol("identification"))) and
      (__ \ Symbol("countryOfResidence")).readNullable[String] and
      __.lazyRead(readNullableAtSubPath[Address](__ \ Symbol("identification") \ Symbol("address"))) and
      readMentalCapacity and
      (__ \ "entityStart").read[LocalDate] and
      (__ \ "provisional").readWithDefault(false)
    )(IndividualProtector.apply _)

  implicit val writes: Writes[IndividualProtector] = (
    (__ \ Symbol("name")).write[Name] and
      (__ \ Symbol("dateOfBirth")).writeNullable[LocalDate] and
      (__ \ Symbol("nationality")).writeNullable[String] and
      (__ \ Symbol("identification")).writeNullable[IndividualIdentification] and
      (__ \ Symbol("countryOfResidence")).writeNullable[String] and
      (__ \ Symbol("identification") \ Symbol("address")).writeNullable[Address] and
      (__ \ Symbol("legallyIncapable")).writeNullable[YesNoDontKnow](legallyIncapableWrites) and
      (__ \ "entityStart").write[LocalDate] and
      (__ \ "provisional").write[Boolean]
    )(unlift(IndividualProtector.unapply))

}
