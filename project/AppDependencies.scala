import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"            % "0.18.8-play27",
    "uk.gov.hmrc"       %% "govuk-template"                 % "5.60.0-play-27",
    "uk.gov.hmrc"       %% "play-health"                    % "3.15.0-play-27",
    "uk.gov.hmrc"       %% "domain"                         % "5.10.0-play-27",
    "com.typesafe.play" %% "play-json-joda"                 % "2.7.4",
    "uk.gov.hmrc"       %% "play-ui"                        % "9.0.0-play-27",
    "com.typesafe.play" %% "play-json-joda"                 % "2.7.4",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.4.0-play-26",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-27"     % "3.4.0",
    "uk.gov.hmrc"       %% "play-language"                  % "4.5.0-play-27"
  )

  val test = Seq(
    "org.scalatest"               %% "scalatest"              % "3.0.8",
    "org.scalatestplus.play"      %% "scalatestplus-play"     % "4.0.3",
    "org.pegdown"                 %  "pegdown"                % "1.6.0",
    "org.jsoup"                   %  "jsoup"                  % "1.12.1",
    "wolfendale"                  %% "scalacheck-gen-regexp"  % "0.1.2",
    "com.typesafe.play"           %% "play-test"              % PlayVersion.current,
    "org.mockito"                 %  "mockito-all"            % "1.10.19",
    "org.scalacheck"              %% "scalacheck"             % "1.14.3",
    "com.github.tomakehurst"      % "wiremock-standalone"     % "2.25.1"
  )

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion
  )
}
