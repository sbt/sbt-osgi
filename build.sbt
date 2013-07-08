organization := "com.typesafe.sbt"

name := "sbt-osgi"

version := "0.6.0-SNAPSHOT"

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

publishTo := {
  import Classpaths._
  val repo = if (isSnapshot.value) sbtPluginSnapshots else sbtPluginReleases
  Some(repo)
}

publishMavenStyle := false
