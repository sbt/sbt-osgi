package com.typesafe.sbt.osgi

import sbt._
import sbt.Keys._

object SbtCompat {

  def packageBinBundle: Def.Setting[Artifact] = {
    artifact in (Compile, packageBin) ~= (_.withType("bundle"))
  }
}
