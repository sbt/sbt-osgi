package com.github.sbt.osgi

import java.net.{ URI, URL }

import sbt.librarymanagement.License
import sbt._
import sbt.Keys._

private[osgi] object PluginCompat {
  // sbt 2.1 uses internal JARs for project dependencies; retain their OSGi manifest.
  val internalBundleSettings: Seq[Def.Setting[?]] = Seq(
    Compile / packageInternal := Def.uncached(OsgiKeys.bundle.value)
  )
  def licenses(v: Seq[License]): Seq[(String, URL)] =
    v.map(l => l.spdxId -> l.uri.toURL)
  def apiUrl(v: Option[URI]): Option[URL] =
    v.map(u => new URI(u.toString).toURL)
  type ManifestAttributes = sbt.PackageOption.ManifestAttributes
}
