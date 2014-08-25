import sbt._
import Keys._
import com.typesafe.sbt.osgi.SbtOsgi
import com.typesafe.sbt.osgi.SbtOsgi.autoImport._

object Test05Build extends Build {
  val projectSettings = osgiSettings ++ Seq {
    exportJars := true
  }

  val p1 = Project("p1",file("p1")).enablePlugins(SbtOsgi).settings(projectSettings: _*)
  val p2 = Project("p2",file("p2")).enablePlugins(SbtOsgi).settings(projectSettings: _*) dependsOn (p1)
}

