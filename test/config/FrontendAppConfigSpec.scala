/*
 * Copyright 2026 HM Revenue & Customs
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

package config

import base.SpecBase
import play.api.i18n.{Lang, MessagesImpl}

class FrontendAppConfigSpec extends SpecBase {

  private val appConfig = app.injector.instanceOf[FrontendAppConfig]

  "have the correct languageMap" in {
    appConfig.languageMap mustBe Map(
      "english" -> Lang("en"),
      "cymraeg" -> Lang("cy")
    )
  }

  "return the correct route to switch language - EN" in {
    val enCall = appConfig.routeToSwitchLanguage("en")
    enCall.url mustBe "/maintain-a-trust/protectors/language/en"
  }

  "return the correct route to switch language - CY" in {
    val cyCall = appConfig.routeToSwitchLanguage("cy")
    cyCall.url mustBe "/maintain-a-trust/protectors/language/cy"
  }

  "helplineUrl in English mode return trusts helpline URL" in {
    val messages = MessagesImpl(Lang("en"), messagesApi)
    appConfig.helplineUrl(
      messages
    ) mustBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/trusts"
  }

  "helplineUrl in Welsh mode return trusts helpline URL" in {
    val messages = MessagesImpl(Lang("cy"), messagesApi)
    appConfig.helplineUrl(
      messages
    ) mustBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/welsh-language-helplines"
  }

}
