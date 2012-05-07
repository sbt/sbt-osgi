
organization := "com.typesafe.sbtosgi"

name := "sbtosgi"

// version is defined in version.sbt in order to support sbt-release

sbtPlugin := true

libraryDependencies ++= Seq(
  "biz.aQute" % "bndlib" % "1.50.0",
  "org.specs2" %% "specs2" % "1.6.1" % "test",
  "junit" % "junit" % "4.7" % "test"
)

scalacOptions ++= Seq("-unchecked", "-deprecation")

publishTo <<= (version) { version =>
  val (name, url) =
    if (version endsWith "SNAPSHOT")
      "ivy-snapshots" -> "http://repo.typesafe.com/typesafe/ivy-snapshots/"
    else
      "ivy-releases" -> "http://repo.typesafe.com/typesafe/ivy-releases/"
  Some(Resolver.url(name, new URL(url))(Resolver.ivyStylePatterns))
}

publishMavenStyle := false

sbtrelease.Release.releaseSettings

scalariformSettings

scriptedSettings
