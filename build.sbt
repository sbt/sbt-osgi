lazy val scala212 = "2.12.20"
lazy val scala3 = "3.7.2"

ThisBuild / crossScalaVersions := Seq(scala212, scala3)
ThisBuild / scalaVersion := scala212

ThisBuild / (pluginCrossBuild / sbtVersion) := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.5.8"
    case _      => "2.0.0-RC4"
  }
}

ThisBuild / scriptedSbt := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.10.10"
    case _      => "2.0.0-RC4"
  }
}

// So that publishLocal doesn't continuously create new versions
def versionFmt(out: sbtdynver.GitDescribeOutput): String = {
  val snapshotSuffix =
    if (out.isSnapshot()) "-SNAPSHOT"
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

ThisBuild / githubWorkflowOSes := Seq("ubuntu-latest", "macos-latest", "windows-latest")

ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec.temurin("8"),
  JavaSpec.temurin("11"),
  JavaSpec.temurin("17"),
  JavaSpec.temurin("21")
)

ThisBuild / githubWorkflowBuildMatrixExclusions += MatrixExclude(Map("java" -> "temurin@8", "os" -> "macos-latest"))

name := "sbt-osgi"
enablePlugins(SbtPlugin)
libraryDependencies ++= Dependencies.sbtOsgi
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-encoding",
  "UTF-8"
)

scalacOptions ++= {
  if (insideCI.value) {
    val log = sLog.value
    log.info("Running in CI, enabling Scala2 optimizer")
    Seq(
      "-opt-inline-from:<sources>",
      "-opt:l:inline"
    )
  } else Nil
}

scriptedLaunchOpts += "-Xmx1024m"
scriptedLaunchOpts ++= Seq("-Dplugin.version=" + version.value)
scriptedLaunchOpts += "-debug"
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
    id = "eed3si9n",
    name = "Eugene Yokota",
    email = "eed3si9n@gmail.com",
    url = url("https://github.com/eed3si9n")
  ),
  Developer(
    id = "hseeberger",
    name = "Heiko Seeberger",
    email = "@hseeberger",
    url = url("https://github.com/hseeberger")
  ),
  Developer(
    id = "mdedetrich",
    name = "Matthew de Detrich",
    email = "mdedetrich@gmail.com",
    url = url("https://github.com/mdedetrich")
  ),
  Developer(
    id = "romainreuillon",
    name = "Romain Reuillon",
    email = "romain.reuillon@iscpif.fr",
    url = url("https://github.com/romainreuillon")
  )
)
