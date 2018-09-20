organization := "com.typesafe.sbt"

name := "sbt-osgi"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

libraryDependencies ++= Dependencies.sbtOsgi

version := "0.9.5-SNAPSHOT"

crossSbtVersions := Seq("0.13.17", "1.1.6")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-encoding", "UTF-8"
)

sbtPlugin := true

publishMavenStyle := false
