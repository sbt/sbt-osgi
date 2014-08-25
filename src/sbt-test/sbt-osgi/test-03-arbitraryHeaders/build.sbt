lazy val test03 = (project in file ("")).enablePlugins(SbtOsgi)

organization := "com.typesafe.sbt"

name := "sbt-osgi-test"

version := "1.2.3"

osgiSettings

OsgiKeys.additionalHeaders := Map(
  "Main-Class" -> "com.typesafe.sbt.osgi.test.App"
)

TaskKey[Unit]("verify-bundle") <<= OsgiKeys.bundle map { file =>
  import java.io.IOException
  import java.util.zip.ZipFile
  import scala.io.Source
  val newLine = System.getProperty("line.separator")
  val zipFile = new ZipFile(file)
  // Verify manifest
  val manifestIn = zipFile.getInputStream(zipFile.getEntry("META-INF/MANIFEST.MF"))
  try {
    val lines = Source.fromInputStream(manifestIn).getLines().toList
    val allLines = lines mkString newLine
    val butWas = newLine + "But was:" + newLine + allLines
    if (!(lines contains "Main-Class: com.typesafe.sbt.osgi.test.App"))
      error("Expected 'Main-Class: com.typesafe.sbt.osgi.test.App' in manifest!" + butWas)
  } catch {
    case e: IOException => error("Expected to be able to read the manifest, but got exception!" + newLine + e)
  } finally manifestIn.close()
}
