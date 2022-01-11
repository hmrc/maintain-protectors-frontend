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

package connectors

import config.FrontendAppConfig
import models.protectors.{BusinessProtector, IndividualProtector, Protectors}
import models.{RemoveProtector, TrustDetails}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustsConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private val trustsUrl: String = s"${config.trustsUrl}/trusts"
  private val protectorsUrl: String = s"$trustsUrl/protectors"

  def getTrustDetails(identifier: String)
                     (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustDetails] = {
    val url: String = s"${trustsUrl}/trust-details/$identifier/transformed"
    http.GET[TrustDetails](url)
  }

  def getProtectors(identifier: String)
                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Protectors] = {
    val url: String = s"$protectorsUrl/$identifier/transformed"
    http.GET[Protectors](url)
  }

  def addIndividualProtector(identifier: String, protector: IndividualProtector)
                            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$protectorsUrl/add-individual/$identifier"
    http.POST[JsValue, HttpResponse](url, Json.toJson(protector))
  }

  def amendIndividualProtector(identifier: String, index: Int, individual: IndividualProtector)
                              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$protectorsUrl/amend-individual/$identifier/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(individual))
  }

  def addBusinessProtector(identifier: String, protector: BusinessProtector)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$protectorsUrl/add-business/$identifier"
    http.POST[JsValue, HttpResponse](url, Json.toJson(protector))
  }

  def amendBusinessProtector(identifier: String, index: Int, protector: BusinessProtector)
                            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$protectorsUrl/amend-business/$identifier/$index"
    http.POST[JsValue, HttpResponse](url, Json.toJson(protector))
  }

  def removeProtector(identifier: String, protector: RemoveProtector)
                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$protectorsUrl/$identifier/remove"
    http.PUT[JsValue, HttpResponse](url, Json.toJson(protector))
  }

  def isTrust5mld(identifier: String)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    val url: String = s"$trustsUrl/$identifier/is-trust-5mld"
    http.GET[Boolean](url)
  }

}
