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

package controllers.business.remove

import controllers.actions.StandardActionSets
import forms.DateRemovedFromTrustFormProvider
import javax.inject.Inject
import models.{ProtectorType, RemoveProtector}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.business.remove.WhenRemovedView

import scala.concurrent.{ExecutionContext, Future}

class WhenRemovedController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       standardActionSets: StandardActionSets,
                                       formProvider: DateRemovedFromTrustFormProvider,
                                       trust: TrustService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: WhenRemovedView,
                                       trustService: TrustService
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getBusinessProtector(request.userAnswers.utr, index).map {
        protector =>
          val form = formProvider.withPrefixAndEntityStartDate("businessProtector.whenRemoved", protector.entityStart)
          Ok(view(form, index, protector.name))
      }
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForUtr.async {
    implicit request =>

      trust.getBusinessProtector(request.userAnswers.utr, index).flatMap {
        protector =>
          val form = formProvider.withPrefixAndEntityStartDate("businessProtector.whenRemoved", protector.entityStart)
          form.bindFromRequest().fold(
            formWithErrors => {
              Future.successful(BadRequest(view(formWithErrors, index, protector.name)))
            },
            value =>
              trustService.removeProtector(request.userAnswers.utr, RemoveProtector(ProtectorType.BusinessProtector, index, value)).map(_ =>
                Redirect(controllers.routes.AddAProtectorController.onPageLoad())
              )
          )
      }
  }
}
