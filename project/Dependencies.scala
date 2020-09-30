import sbt._

object Library {

  // Versions
  val bndVersion = "5.1.2"
  val specs2Version = "4.10.3"

  // Libraries
  val bndLib = "biz.aQute.bnd" % "biz.aQute.bndlib" % bndVersion
  val specs2 = "org.specs2" %% "specs2-core" % specs2Version
}

object Dependencies {

  import Library._

  val sbtOsgi = List(
    bndLib,
    specs2 % "test")
}
