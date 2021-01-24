name := "strava"

scalaVersion := "2.13.4"

scalacOptions ++= List("-deprecation", "-Xlint")

val http4sVersion          = "1.0-57-0368053"
val DoobieVersion          = "0.10.0"
val CirceVersion           =  "0.13.0"
val CirceGenericExVersion  = "0.12.2"
val CirceConfigVersion     = "0.7.0"
val CatsVersion            = "2.1.1"
val CatsEffectVersion      = "3.0.0-M5"
val FlywayVersion          = "6.1.0"
val MySqlVersion           = "8.0.22"
val Redis4CatsVersion      = "0.10.0"
val Log4catsVersion        = "1.0.0"
val Slf4jVersion           = "1.7.25"

libraryDependencies ++= Seq(
  "org.typelevel"     %% "cats-core"            % CatsVersion,
  "org.http4s"        %% "http4s-client"        % http4sVersion,
  "org.http4s"        %% "http4s-blaze-server"  % http4sVersion,
  "org.http4s"        %% "http4s-blaze-client"  % http4sVersion,
  "org.http4s"        %% "http4s-dsl"           % http4sVersion,
  "org.http4s"        %% "http4s-circe"         % http4sVersion,
  "io.circe"          %% "circe-parser"         % CirceVersion,
  "io.circe"          %% "circe-generic"        % CirceVersion,
  "io.circe"          %% "circe-literal"        % CirceVersion,
  "io.circe"          %% "circe-generic-extras" % CirceGenericExVersion,
  "io.circe"          %% "circe-parser"         % CirceVersion,
  "io.circe"          %% "circe-config"         % CirceConfigVersion,
  "org.tpolecat"      %% "doobie-core"          % DoobieVersion,
  "org.tpolecat"      %% "doobie-h2"            % DoobieVersion,
  "org.tpolecat"      %% "doobie-scalatest"     % DoobieVersion,
  "org.tpolecat"      %% "doobie-hikari"        % DoobieVersion,
  "org.flywaydb"      % "flyway-core"           % FlywayVersion,
  "mysql"             % "mysql-connector-java"  % MySqlVersion,
  "dev.profunktor"    %% "redis4cats-effects"   % Redis4CatsVersion,
  "dev.profunktor"    %% "redis4cats-log4cats"  % Redis4CatsVersion,
  "io.chrisdavenport" %% "log4cats-slf4j"       % Log4catsVersion,
  "org.slf4j"         % "slf4j-simple"          % Slf4jVersion % Test,
)
