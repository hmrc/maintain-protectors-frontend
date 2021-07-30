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

package controllers

import controllers.actions.StandardActionSets
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.InfoView

import javax.inject.Inject

class InfoController @Inject()(
                                override val messagesApi: MessagesApi,
                                actions: StandardActionSets,
                                val controllerComponents: MessagesControllerComponents,
                                view: InfoView
                              ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.verifiedForIdentifier {
    implicit request =>
      Ok(view())
  }

  def onSubmit(): Action[AnyContent] = actions.verifiedForIdentifier {
    Redirect(controllers.routes.AddNowController.onPageLoad())
  }

}
