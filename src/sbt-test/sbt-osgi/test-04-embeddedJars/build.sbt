import sbtcompat.PluginCompat._

lazy val test04 = (project in file(""))
  .enablePlugins(SbtOsgi)
  .settings(
    OsgiKeys.embeddedJars := Def.uncached {
      implicit val conv: xsbti.FileConverter = fileConverter.value
      toFiles((Compile / externalDependencyClasspath).value).filter(_.getName.startsWith("junit"))
    }
  )

organization := "com.github.sbt"

name := "sbt-osgi-test"

version := "1.2.3"

osgiSettings

libraryDependencies += "junit" % "junit" % "4.11" // Not in test scope here!

TaskKey[Unit]("verifyBundle") := {
  import java.io.IOException
  import java.util.zip.ZipFile
  import scala.io.Source
  val file = OsgiKeys.bundleFile.value
  val newLine = System.getProperty("line.separator")
  val zipFile = new ZipFile(file)
  // Verify bundle content
  val sampleJar = zipFile.getEntry("junit-4.11.jar")
  assert(sampleJar != null, "Expected 'junit-4.11.jar' inside of the bundle")
  // Verify manifest
  val manifestIn = zipFile.getInputStream(zipFile.getEntry("META-INF/MANIFEST.MF"))
  try {
    val lines = Source.fromInputStream(manifestIn).getLines().toList
    val allLines = lines mkString newLine
    val butWas = newLine + "But was:" + newLine + allLines
    if (!(lines contains "Bundle-ClassPath: .,junit-4.11.jar"))
      sys.error("Expected 'Bundle-ClassPath: .,junit-4.11.jar' in manifest!" + butWas)
  } catch {
    case e: IOException => sys.error("Expected to be able to read the manifest, but got exception!" + newLine + e)
  } finally manifestIn.close()
}
