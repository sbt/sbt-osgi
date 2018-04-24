lazy val test02 = (project in file ("")).enablePlugins(SbtOsgi)

organization := "com.typesafe.sbt"

name := "sbt-osgi-test"

version := "1.2.3"

libraryDependencies += "org.osgi" % "org.osgi.core" % "4.3.0" % "provided"

osgiSettings

OsgiKeys.bundleActivator := Some("com.typesafe.sbt.osgi.test.internal.Activator")

OsgiKeys.dynamicImportPackage := Seq("scala.*")

OsgiKeys.exportPackage := Seq("com.typesafe.sbt.osgi.test")

TaskKey[Unit]("verifyBundle") := {
  import java.io.IOException
  import java.util.jar.JarFile
  import scala.io.Source
  val newLine = System.getProperty("line.separator")
  val jarFile = new JarFile(OsgiKeys.bundle.value)
  // Verify manifest
  try {
    val manifest = jarFile.getManifest
    assert(manifest != null, "No MANIFEST.MF in JAR file")
    val attributes = manifest.getMainAttributes
    val includeResource = attributes.getValue("Include-Resource")
    assert(includeResource == null, "MANIFEST.MF contains unexpected Include-Resource attribute; value=" + includeResource)
  } catch {
    case e: IOException => sys.error("Expected to be able to read the manifest, but got exception!" + newLine + e)
  }
  // Verify resources
  val propertiesEntry = jarFile.getEntry("foo.properties")
  if (propertiesEntry != null) {
    val resourcesIn = jarFile.getInputStream(propertiesEntry)
    try {
      val lines = Source.fromInputStream(resourcesIn).getLines().toList
      val allLines = lines mkString newLine
      val butWas = newLine + "But was:" + newLine + allLines
      if (!(lines contains "foo = bar"))
        sys.error("Expected 'foo = bar' in properties!" + butWas)
    } catch {
      case e: Exception => sys.error("Expected to be able to read the properties, but got exception!" + newLine + e)
    } finally resourcesIn.close()
  } else sys.error("Expected to find 'foo.properties' in the bundle JAR, but was not there!")
}
