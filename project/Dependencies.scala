import sbt._

object Library {

  // Versions
  val bndVersion = "7.1.0"
  val specs2Version = "4.23.0"

  // Libraries
  val bndLib = "biz.aQute.bnd" % "biz.aQute.bndlib" % bndVersion
  val specs2 = "org.specs2"   %% "specs2-core"      % specs2Version
}

object Dependencies {
  import Library._

  val sbtOsgi = List(bndLib, specs2 % Test)
}
