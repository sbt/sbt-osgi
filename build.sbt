organization := "com.typesafe.sbt"

name := "sbt-osgi"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

libraryDependencies ++= Dependencies.sbtOsgi

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-language:_",
  "-target:jvm-1.6",
  "-encoding", "UTF-8"
)

sbtPlugin := true

publishMavenStyle := false
