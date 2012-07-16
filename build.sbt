
organization := "com.typesafe.sbtosgi"

name := "sbtosgi"

// version is defined in 0.2.0-SNAPSHOT in order to support sbt-release

sbtPlugin := true

libraryDependencies ++= Seq(
  "biz.aQute" % "bndlib" % "1.50.0",
  "org.specs2" %% "specs2" % "1.11" % "test",
  "junit" % "junit" % "4.7" % "test"
)

scalacOptions ++= Seq("-unchecked", "-deprecation")

publishTo <<= isSnapshot(if (_) Some(Classpaths.typesafeSnapshots) else Some(Classpaths.typesafeReleases))

publishMavenStyle := false

releaseSettings

scalariformSettings

scriptedSettings

scriptedLaunchOpts += "-Xmx1024m"
