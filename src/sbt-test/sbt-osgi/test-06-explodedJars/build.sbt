
organization := "com.typesafe.sbt"

name := "sbt-osgi-test"

version := "1.2.3"

osgiSettings

OsgiKeys.explodedJars += file("tiny.jar")

TaskKey[Unit]("verify-bundle") <<= OsgiKeys.bundle map { file =>
  import scala.collection.JavaConverters._
  import java.util.jar.JarFile
  val (tiny, bundle) = (new JarFile("tiny.jar"), new JarFile(file))
  // Everything in tiny other than its manifest should exist in bundle
  val entries = tiny.entries.asScala.toList.map(e => (e, Option(bundle.getEntry(e.getName))))
  entries.filterNot(_._1.getName.startsWith("META-INF/")).foreach {
    case (te, None)     => sys.error("Expected entry " + te + " not found.")
    case (te, Some(be)) => if (te.getSize != be.getSize) sys.error("Unequal sizes for " + te)
  }
  // Our manifest should be okay
  if (bundle.getManifest.getMainAttributes.getValue("Bundle-SymbolicName") == null)
    sys.error("Target manifest is incorrect.")
}

