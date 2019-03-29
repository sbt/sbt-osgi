organization := "com.typesafe.sbt"

name := "sbt-osgi"

enablePlugins(SbtPlugin)

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

libraryDependencies ++= Dependencies.sbtOsgi

crossSbtVersions := Seq("0.13.18", "1.2.8")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-encoding", "UTF-8"
)

publishMavenStyle := false
