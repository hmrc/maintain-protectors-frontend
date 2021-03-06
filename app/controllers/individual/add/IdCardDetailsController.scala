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

package controllers.individual.add

import config.annotations.IndividualProtector
import controllers.actions._
import controllers.actions.individual.NameRequiredAction
import forms.IdCardDetailsFormProvider
import javax.inject.Inject
import models.{IdCard, NormalMode}
import navigation.Navigator
import pages.individual.IdCardDetailsPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import views.html.individual.add.IdCardDetailsView

import scala.concurrent.{ExecutionContext, Future}

class IdCardDetailsController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionRepository: PlaybackRepository,
                                         @IndividualProtector navigator: Navigator,
                                         standardActionSets: StandardActionSets,
                                         nameAction: NameRequiredAction,
                                         formProvider: IdCardDetailsFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: IdCardDetailsView,
                                         val countryOptions: CountryOptions
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[IdCard] = formProvider.withPrefix("individualProtector")

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForIdentifier.andThen(nameAction) {
    implicit request =>

      val preparedForm = request.userAnswers.get(IdCardDetailsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, request.protectorName))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.verifiedForIdentifier.andThen(nameAction).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, request.protectorName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IdCardDetailsPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IdCardDetailsPage, NormalMode, updatedAnswers))
      )
  }
}
