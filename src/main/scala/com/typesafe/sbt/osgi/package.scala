package com.typesafe.sbt

package object osgi {
  @deprecated("Use com.github.sbt.osgi.OsgiKeys instead", "0.10.0")
  val OsgiKeys = com.github.sbt.osgi.OsgiKeys
  @deprecated("Use com.github.sbt.osgi.OsgiKeys instead", "0.10.0")
  type OsgiKeys = com.github.sbt.osgi.OsgiKeys
  @deprecated("Use com.github.sbt.osgi.OsgiManifestHeaders instead", "0.10.0")
  type OsgiManifestHeaders = com.github.sbt.osgi.OsgiManifestHeaders
  @deprecated("Use com.github.sbt.osgi.SbtOsgi instead", "0.10.0")
  val SbtOsgi = com.github.sbt.osgi.SbtOsgi
}
