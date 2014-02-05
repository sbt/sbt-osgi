import sbt._
import Keys._
import com.typesafe.sbt.osgi.SbtOsgi._

object Test05Build extends Build {
  val projectSettings = Defaults.defaultSettings ++ osgiSettings ++ Seq {
    exportJars := true
  }

  val p1 = Project("p1",file("p1"),settings = projectSettings)
  val p2 = Project("p2",file("p2"),settings = projectSettings) dependsOn (p1)
}

