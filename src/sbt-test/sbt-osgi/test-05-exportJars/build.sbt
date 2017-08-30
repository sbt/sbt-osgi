val projectSettings = osgiSettings ++ Seq(
  exportJars := true
)

lazy val p1 = (project in file("p1")).enablePlugins(SbtOsgi).settings(projectSettings: _*)
lazy val p2 = (project in file("p2")).enablePlugins(SbtOsgi).settings(projectSettings: _*) dependsOn (p1)

lazy val root = (project in file("."))
  .settings(projectSettings: _*)
  .enablePlugins(SbtOsgi)
  .aggregate(p1, p2)
