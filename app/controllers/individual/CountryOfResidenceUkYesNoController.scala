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

package controllers.individual

import config.annotations.IndividualProtector
import controllers.actions.StandardActionSets
import controllers.actions.individual.NameRequiredAction
import forms.YesNoFormProvider
import models.Mode
import navigation.Navigator
import pages.individual.CountryOfResidenceUkYesNoPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.individual.CountryOfResidenceUkYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfResidenceUkYesNoController @Inject()(
                                                     val controllerComponents: MessagesControllerComponents,
                                                     standardActionSets: StandardActionSets,
                                                     formProvider: YesNoFormProvider,
                                                     view: CountryOfResidenceUkYesNoView,
                                                     repository: PlaybackRepository,
                                                     @IndividualProtector navigator: Navigator,
                                                     nameAction: NameRequiredAction
                                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = formProvider.withPrefix("individualProtector.countryOfResidenceUkYesNo")

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForIdentifier.andThen(nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CountryOfResidenceUkYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, request.protectorName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForIdentifier.andThen(nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, request.protectorName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfResidenceUkYesNoPage, value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CountryOfResidenceUkYesNoPage, mode, updatedAnswers))
      )
  }
}
