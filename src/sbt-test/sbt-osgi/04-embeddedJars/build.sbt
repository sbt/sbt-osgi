
organization := "com.typesafe.sbt"

name := "sbt-osgi-test"

version := "1.2.3"

osgiSettings

OsgiKeys.embeddedJars <<= Keys.externalDependencyClasspath in Compile map {
  deps => deps filter (d => d.data.getName startsWith "jerkson") map (d => d.data)
}

libraryDependencies += "io.backchat.jerkson" %% "jerkson" % "0.7.0"


TaskKey[Unit]("verify-bundle") <<= OsgiKeys.bundle map { file =>
  import java.io.IOException
  import java.util.zip.ZipFile
  import scala.io.Source
  val newLine = System.getProperty("line.separator")
  val zipFile = new ZipFile(file)
  // Verify bundle content
  val sampleJar = zipFile.getEntry("jerkson_2.9.2-0.7.0.jar")
  assert(sampleJar != null, "Expected 'jerkson_2.9.2-0.7.0.jar' inside of the bundle")  
  // Verify manifest
  val manifestIn = zipFile.getInputStream(zipFile.getEntry("META-INF/MANIFEST.MF"))
  try {
    val lines = Source.fromInputStream(manifestIn).getLines().toList
    val allLines = lines mkString newLine
    val butWas = newLine + "But was:" + newLine + allLines
    if (!(lines contains "Bundle-ClassPath: .,jerkson_2.9.2-0.7.0.jar"))
      error("Expected 'Bundle-ClassPath: .,jerkson_2.9.2-0.7.0.jar' in manifest!" + butWas)
  } catch {
    case e: IOException => error("Expected to be able to read the manifest, but got exception!" + newLine + e)
  } finally manifestIn.close()
}
