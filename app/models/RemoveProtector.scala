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

package models

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

case class RemoveProtector(`type`: ProtectorType, index : Int, endDate: LocalDate)

object RemoveProtector {

  implicit val writes : Writes[RemoveProtector] =
    (
      (JsPath \ "type").write[ProtectorType](ProtectorType.writesToTrusts) and
      (JsPath \ "index").write[Int] and
      (JsPath \ "endDate").write[LocalDate]
    ).apply(unlift(RemoveProtector.unapply))

  def apply(`type`: ProtectorType, index: Int): RemoveProtector =  RemoveProtector(`type`, index, LocalDate.now)

}
