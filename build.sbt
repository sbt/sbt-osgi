
organization := "com.typesafe.sbt"

name := "sbt-osgi"

// TODO Move version to version.sbt in order to support sbt-release
version := "0.6.0-SNAPSHOT"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-language:_",
  "-target:jvm-1.6",
  "-encoding", "UTF-8"
)

libraryDependencies ++= Seq(
  "biz.aQute.bnd" % "bndlib" % "2.1.0",
  "org.specs2" %% "specs2" % "1.14" % "test",
  "com.jsuereth" %% "scala-arm" % "1.3"
)

sbtPlugin := true

publishTo := { 
  import Classpaths._
  val repo = if (isSnapshot.value) sbtPluginSnapshots else sbtPluginReleases
  Some(repo)
}

publishMavenStyle := false
