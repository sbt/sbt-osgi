import sbt._

object Library {

  // Versions
  val bndVersion = "2.1.0"
  val specs2Version = "1.14"
  val armVersion = "1.3"

  // Libraries
  val bndLib = "biz.aQute.bnd" % "bndlib" % bndVersion
  val specs2 = "org.specs2" %% "specs2" % specs2Version
  val arm = "com.jsuereth" %% "scala-arm" % armVersion
}

object Dependencies {

  import Library._

  val sbtOsgi = List(
    bndLib,
    specs2  % "test",
    arm
  )
}
