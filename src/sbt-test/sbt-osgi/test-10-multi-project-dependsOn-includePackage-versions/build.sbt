import com.typesafe.sbt.osgi.SbtOsgi
import com.typesafe.sbt.osgi.OsgiKeys

inThisBuild(
  Seq(
    organization := "com.typesafe.sbt",
    homepage := Some(url("https://github.com/woq-blended/blended")),
    version := "1.2.3",
    libraryDependencies += "org.osgi" % "org.osgi.core" % "4.3.0" % "provided",
    licenses += ("Apache 2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
  )
)

lazy val root = project
  .in(file("."))
  .aggregate(proj1, proj2)

lazy val proj1 = project
  .in(file("proj1"))
  .enablePlugins(SbtOsgi)
  .settings(
    name := "proj1",
    OsgiKeys.bundleSymbolicName := "proj1",
    OsgiKeys.bundleVersion := version.value,
    OsgiKeys.exportPackage := Seq("proj1")
  )

lazy val proj2 = project
  .in(file("proj2"))
  .enablePlugins(SbtOsgi)
  .dependsOn(proj1)
  .settings(
    name := "proj2",
    OsgiKeys.bundleSymbolicName := "proj2",
    OsgiKeys.bundleVersion := version.value,
    OsgiKeys.exportPackage := Seq("proj2")
  )

TaskKey[Unit]("verifyBundle") := {
  import java.io.IOException
  import java.util.zip.ZipFile
  import scala.io.Source

  val newLine = System.getProperty("line.separator")

  {
    val file = OsgiKeys.bundle.in(proj1).value
    val zipFile = new ZipFile(file)

    // Verify manifest
    // in essence, we make sure package proj1 gets exported with a version
    val manifestIn = zipFile.getInputStream(zipFile.getEntry("META-INF/MANIFEST.MF"))
    try {
      val lines = Source.fromInputStream(manifestIn).getLines().toList
      val allLines = lines.mkString(newLine)
      val butWas = newLine + "But was:" + newLine + allLines

      val export = Seq("Export-Package: ", "proj1;", s"""version="${version.value}"""")

      if (!(lines.exists(l => export.forall(s => l.containsSlice(s))))) {
        sys.error(s"""Expected ${export.mkString("'", "' and '", "'")} in manifest!""" + butWas)
      }
    } catch {
      case e: IOException => sys.error("Expected to be able to read the manifest, but got exception!" + newLine + e)
    } finally manifestIn.close()
  }

  {
    val file = OsgiKeys.bundle.in(proj2).value
    val zipFile = new ZipFile(file)

    // Verify manifest
    // in essence, we make sure, package proj2 gets imported with a proper version range
    // in sbt-osgi <= 0.9.4, the version range was missing
    val manifestIn = zipFile.getInputStream(zipFile.getEntry("META-INF/MANIFEST.MF"))
    try {
      val lines = Source.fromInputStream(manifestIn).getLines().toList
      val allLines = lines.mkString(newLine)
      val butWas = newLine + "But was:" + newLine + allLines

      val rangeFrom = version.value.split("[.]").take(2).mkString(".")
      val rangeTo = version.value.split("[.]").head.toInt + 1
      val expected = Seq("Import-Package: ", s"""proj1;version="[${rangeFrom},${rangeTo})"""")

      if (!(lines.exists(l => expected.forall(s => l.containsSlice(s))))) {
        sys.error(s"""Expected ${expected.mkString("'", "' and '", "'")} in manifest!""" + butWas)
      }
    } catch {
      case e: IOException => sys.error("Expected to be able to read the manifest, but got exception!" + newLine + e)
    } finally manifestIn.close()
  }

}
