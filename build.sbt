organization := "com.typesafe.sbt"

name := "sbt-osgi"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

libraryDependencies ++= Dependencies.sbtOsgi

crossSbtVersions := Seq("0.13.16", "1.1.1")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-encoding", "UTF-8"
)

sbtPlugin := true

publishMavenStyle := false
