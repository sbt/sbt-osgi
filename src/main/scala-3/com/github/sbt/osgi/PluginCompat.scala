package com.github.sbt.osgi

import java.net.{ URI, URL }

import sbt.librarymanagement.License

private[osgi] object PluginCompat {
  def licenses(v: Seq[License]): Seq[(String, URL)] =
    v.map(l => l.spdxId -> new URL(l.uri.toString))
  def apiUrl(v: Option[URI]): Option[URL] =
    v.map(u => new URL(u.toString))
  type ManifestAttributes = sbt.PackageOption.ManifestAttributes
}
