
organization := "com.typesafe.sbt"

name := "sbt-osgi-test"

version := "1.2.3"

libraryDependencies += "org.osgi" % "org.osgi.core" % "4.3.0" % "provided"

osgiSettings

OsgiKeys.bundleActivator := Some("com.typesafe.sbt.osgi.test.internal.Activator")

OsgiKeys.dynamicImportPackage := Seq("scala.*")

OsgiKeys.exportPackage := Seq("com.typesafe.sbt.osgi.test")

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
  } catch {
    case e: IOException => error("Expected to be able to read the manifest, but got exception!" + newLine + e)
  } finally manifestIn.close()
  // Verify resources
  val propertiesEntry = zipFile.getEntry("foo.properties")
  if (propertiesEntry != null) {
    val resourcesIn = zipFile.getInputStream(propertiesEntry)
    try {
      val lines = Source.fromInputStream(resourcesIn).getLines().toList
      val allLines = lines mkString newLine
      val butWas = newLine + "But was:" + newLine + allLines
      if (!(lines contains "foo = bar"))
        error("Expected 'foo = bar' in properties!" + butWas)
    } catch {
      case e: Exception => error("Expected to be able to read the properties, but got exception!" + newLine + e)
    } finally resourcesIn.close()
  } else error("Expected to find 'foo.properties' in the bundle JAR, but was not there!")
}
