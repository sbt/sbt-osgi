
organization := "com.typesafe.sbt"

name := "sbt-osgi"

// version is defined in version.sbt in order to support sbt-release

sbtPlugin := true

libraryDependencies ++= Seq(
  "biz.aQute" % "bndlib" % "1.50.0",
  "org.specs2" %% "specs2" % "1.12.2" % "test",
  "junit" % "junit" % "4.7" % "test"
)

scalacOptions ++= Seq("-unchecked", "-deprecation")

publishTo <<= isSnapshot(if (_) Some(Classpaths.sbtPluginSnapshots) else Some(Classpaths.sbtPluginReleases))

publishMavenStyle := false

releaseSettings

scalariformSettings

scriptedSettings

scriptedLaunchOpts += "-Xmx1024m"
