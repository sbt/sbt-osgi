lazy val scala212 = "2.12.18"
ThisBuild / crossScalaVersions := Seq(scala212)
ThisBuild / scalaVersion := scala212
ThisBuild / dynverSonatypeSnapshots := true

// So that publishLocal doesn't continuously create new versions
def versionFmt(out: sbtdynver.GitDescribeOutput): String = {
  val snapshotSuffix = if
    (out.isSnapshot()) "-SNAPSHOT"
  else ""
  out.ref.dropPrefix + snapshotSuffix
}

def fallbackVersion(d: java.util.Date): String = s"HEAD-${sbtdynver.DynVer timestamp d}"

ThisBuild / version := dynverGitDescribeOutput.value.mkVersion(versionFmt, fallbackVersion(dynverCurrentDate.value))
ThisBuild / dynver := {
  val d = new java.util.Date
  sbtdynver.DynVer.getGitDescribeOutput(d).mkVersion(versionFmt, fallbackVersion(d))
}

ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "scripted")))

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(
    RefPredicate.StartsWith(Ref.Tag("v")),
    RefPredicate.Equals(Ref.Branch("main"))
  )
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    commands = List("ci-release"),
    name = Some("Publish project"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

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
scriptedLaunchOpts ++= Seq("-Dplugin.version=" + version.value)
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
