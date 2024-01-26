lazy val test01 = (project in file(".")).enablePlugins(SbtOsgi)

organization := "com.github.sbt"

name := "sbt-osgi-test"

version := "1.2.3"

libraryDependencies += "org.osgi" % "org.osgi.core" % "4.3.0" % "provided"

osgiSettings

OsgiKeys.bundleActivator := Some("com.github.sbt.osgi.test.internal.Activator")

OsgiKeys.dynamicImportPackage := Seq("scala.*")

// we set an explicit internal package
OsgiKeys.privatePackage := Seq("com.github.sbt.osgi.internal")

// we set what to export one specific one
OsgiKeys.exportPackage := Seq("com.github.sbt.osgi.test.exportme")

// yet we "forget" on purpose to decide what to do about undecided!
// however we also enable the fail setting:
OsgiKeys.failOnUndecidedPackage := true

OsgiKeys.bundleRequiredExecutionEnvironment := Seq("JavaSE-1.7", "JavaSE-1.8")

apiURL := Some(url("https://github.com/sbt/sbt-osgi"))

licenses += ("license" -> url("http://license.license"))

TaskKey[Unit]("verifyBundle") := {
  import java.io.IOException
  import java.util.zip.ZipFile
  import scala.io.Source
  val file = OsgiKeys.bundle.value
  val newLine = System.getProperty("line.separator")
  val zipFile = new ZipFile(file)
  // Verify manifest
  val manifestIn = zipFile.getInputStream(zipFile.getEntry("META-INF/MANIFEST.MF"))
  try {
    val lines = Source.fromInputStream(manifestIn).getLines().toList
    val allLines = lines mkString newLine
    val butWas = newLine + "But was:" + newLine + allLines
    if (!(lines contains "Bundle-Activator: com.github.sbt.osgi.test.internal.Activator"))
      sys.error("Expected 'Bundle-Activator: com.github.sbt.osgi.test.internal.Activator' in manifest!" + butWas)
    if (!(lines contains "Bundle-Description: sbt-osgi-test"))
      sys.error("Expected 'Bundle-Description: sbt-osgi-test' in manifest!" + butWas)
    if (!(lines contains "Bundle-DocURL: https://github.com/sbt/sbt-osgi"))
      sys.error("Expected 'Bundle-DocURL: https://github.com/sbt/sbt-osgi' in manifest!" + butWas)
    if (!(lines contains "Bundle-License: http://license.license;description=license"))
      sys.error("Expected 'Bundle-License: http://license.license;description=license' in manifest!" + butWas)
    if (!(lines contains "Bundle-Name: sbt-osgi-test"))
      sys.error("Expected 'Bundle-Name: sbt-osgi-test' in manifest!" + butWas)
    if (!(lines contains "Bundle-RequiredExecutionEnvironment: JavaSE-1.7,JavaSE-1.8"))
      sys.error("Expected 'Bundle-RequiredExecutionEnvironment: JavaSE-1.7,JavaSE-1.8' in manifest!" + butWas)
    if (!(lines contains "Bundle-Vendor: com.github.sbt"))
      sys.error("Expected 'Bundle-Vendor: com.github.sbt' in manifest!" + butWas)
    if (!(lines contains "Bundle-SymbolicName: com.github.sbt.osgi.test"))
      sys.error("Expected 'Bundle-SymbolicName: com.github.sbt.osgi.test' in manifest!" + butWas)
    if (!(lines contains "Bundle-Version: 1.2.3"))
      sys.error("Expected 'Bundle-Version: 1.2.3' in manifest!" + butWas)
    if (!(lines contains "DynamicImport-Package: scala.*"))
      sys.error("Expected 'DynamicImport-Package: scala.*' in manifest!" + butWas)
    if (!(lines exists (_ containsSlice "Export-Package: com.github.sbt.osgi.test")))
      sys.error("Expected 'Export-Package: com.github.sbt.osgi.test' in manifest!" + butWas)
    if (!(lines exists (l => (l containsSlice "org.osgi.framework") && (l containsSlice "Import-Package: "))))
      sys.error("""Expected 'Import-Package: ' and 'org.osgi.framework' in manifest!""" + butWas)
    if (!(lines contains "Private-Package: com.github.sbt.osgi.test.internal"))
      sys.error("Expected 'Private-Package: com.github.sbt.osgi.test.internal' in manifest!" + butWas)
  } catch {
    case e: IOException => sys.error("Expected to be able to read the manifest, but got exception!" + newLine + e)
  } finally manifestIn.close()
}
