
import sbtrelease._

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

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

seq(posterousSettings: _*)

(email in Posterous) <<= PropertiesKeys.properties(_ get "posterous.email")

(password in Posterous) <<= PropertiesKeys.properties(_ get "posterous.password")

seq(propertiesSettings: _*)

seq(Release.releaseSettings: _*)

ReleaseKeys.releaseProcess <<= thisProjectRef { ref =>
  import ReleaseStateTransformations._
  Seq[ReleasePart](
    initialGitChecks,
    checkSnapshotDependencies,
    releaseTask(check in Posterous in ref),
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseTask(publish in Global in ref),
    releaseTask(publish in Posterous in ref),
    setNextVersion,
    commitNextVersion
  )
}

seq(scalariformSettings: _*)

seq(scriptedSettings: _*)
