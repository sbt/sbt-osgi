
organization := "com.typesafe.sbtosgi"

name := "sbtosgi-test"

version := "1.2.3"

libraryDependencies += "org.osgi" % "org.osgi.core" % "4.3.0" % "provided"

seq(osgiSettings: _*)

OsgiKeys.bundleActivator := Some("com.typesafe.sbtosgi.test.internal.Activator")

OsgiKeys.dynamicImportPackage := Seq("scala.*")

OsgiKeys.exportPackage := Seq("com.typesafe.sbtosgi.test")

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
    if (!(lines contains "Bundle-Activator: com.typesafe.sbtosgi.test.internal.Activator"))
      error("Expected 'Bundle-Activator: com.typesafe.sbtosgi.test.internal.Activator' in manifest!" + butWas)
    if (!(lines contains "Bundle-SymbolicName: com.typesafe.sbtosgi.test"))
      error("Expected 'Bundle-SymbolicName: com.typesafe.sbtosgi.test' in manifest!" + butWas)
    if (!(lines contains "Bundle-Version: 1.2.3"))
      error("Expected 'Bundle-Version: 1.2.3' in manifest!" + butWas)
    if (!(lines contains "DynamicImport-Package: scala.*"))
      error("Expected 'DynamicImport-Package: scala.*' in manifest!" + butWas)
    if (!(lines exists (_ containsSlice "Export-Package: com.typesafe.sbtosgi.test")))
      error("Expected 'Export-Package: com.typesafe.sbtosgi.test' in manifest!" + butWas)
    if (!(lines exists (l => (l containsSlice "org.osgi.framework") && (l containsSlice "Import-Package: "))))
      error("""Expected 'Import-Package: ' and 'org.osgi.framework' in manifest!""" + butWas) 
    if (!(lines contains "Private-Package: com.typesafe.sbtosgi.test.internal"))
      error("Expected 'Private-Package: com.typesafe.sbtosgi.test.internal' in manifest!" + butWas)
  } catch {
    case e: IOException => error("Expected to be able to read the manifest, but got exception!" + newLine + e)
  } finally manifestIn.close()
}
