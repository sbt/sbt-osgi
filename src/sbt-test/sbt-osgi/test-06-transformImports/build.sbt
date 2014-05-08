
organization := "com.typesafe.sbt"

name := "sbt-osgi-test"

version := "1.2.3"

osgiSettings

OsgiKeys.importPackage += "com.example.foo"

OsgiKeys.transformImports := OsgiKeys.simpleImportVersionTransformer(Seq(
  "com.example.*" -> "42.41.40"
))

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
    if (!(lines contains "Import-Package: com.example.foo;version=\"42.41.40\""))
      error("Expected 'Import-Package: com.example.foo;version=\"42.41.40\"' in manifest!" + butWas)
  } catch {
    case e: IOException => error("Expected to be able to read the manifest, but got exception!" + newLine + e)
  } finally manifestIn.close()
}
