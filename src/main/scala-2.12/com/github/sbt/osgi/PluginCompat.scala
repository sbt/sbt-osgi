package com.github.sbt.osgi

import java.net.URL

private[osgi] object PluginCompat {
  def licenses(v: Seq[(String, URL)]): Seq[(String, URL)] = v
  def apiUrl(v: Option[URL]): Option[URL] = v
  type ManifestAttributes = sbt.Package.ManifestAttributes
}
