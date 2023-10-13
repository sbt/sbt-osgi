lazy val scala212 = "2.12.18"
ThisBuild / crossScalaVersions := Seq(scala212)
ThisBuild / scalaVersion := scala212
ThisBuild / dynverSonatypeSnapshots := true
ThisBuild / version := {
  val orig = (ThisBuild / version).value
  if (orig.endsWith("-SNAPSHOT")) "0.10.0-SNAPSHOT"
  else orig
}

name := "sbt-osgi"
enablePlugins(SbtPlugin)
libraryDependencies ++= Dependencies.sbtOsgi
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-encoding", "UTF-8"
)
(pluginCrossBuild / sbtVersion) := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.2.8"
  }
}
scriptedLaunchOpts += "-Xmx1024m"
scriptedLaunchOpts += s"-Dproject.version=${version.value}"
// scriptedBufferLog := false

ThisBuild / licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
ThisBuild / organization := "com.github.sbt"
ThisBuild / homepage := Some(url("https://github.com/sbt/sbt-osgi"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/sbt/sbt-osgi"),
    "scm:git@github.com:sbt/sbt-osgi.git"
  )
)
ThisBuild / description := "sbt plugin for creating OSGi bundles"
ThisBuild / developers := List(
  Developer(
    id = "hseeberger",
    name = "Heiko Seeberger",
    email = "@hseeberger",
    url = url("https://github.com/hseeberger")
  ),
)
