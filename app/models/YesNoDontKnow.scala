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

package models

sealed trait YesNoDontKnow

object YesNoDontKnow extends Enumerable.Implicits {

  case object Yes extends WithName("yes") with YesNoDontKnow
  case object No extends WithName("no") with YesNoDontKnow
  case object DontKnow extends WithName("dontKnow") with YesNoDontKnow

  val values: Seq[YesNoDontKnow] = Seq(
    Yes, No, DontKnow
  )

  def fromBoolean(v: Option[Boolean]): Option[YesNoDontKnow] = v match {
    case Some(value) => if (value) Some(No) else Some(Yes)
    case None => Some(DontKnow)
  }

  implicit val enumerable: Enumerable[YesNoDontKnow] =
    Enumerable(values.map(v => v.toString -> v): _*)

}