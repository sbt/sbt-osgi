/*
 * Copyright 2011-2013 Typesafe Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.typesafe.sbt.osgi

import sbt._
import sbt.Keys._

object SbtOsgi extends Plugin {

  type OsgiManifestHeaders = com.typesafe.sbt.osgi.OsgiManifestHeaders

  val OsgiKeys = com.typesafe.sbt.osgi.OsgiKeys

  def osgiSettings: Seq[Setting[_]] = defaultOsgiSettings ++ Seq(
    packagedArtifact in (Compile, packageBin) <<= (artifact in (Compile, packageBin), OsgiKeys.bundle).identityMap,
    artifact in (Compile, packageBin) ~= (_.copy(`type` = "bundle"))
  )

  def defaultOsgiSettings: Seq[Setting[_]] = {
    import OsgiKeys._
    Seq(
      bundle <<= (
        manifestHeaders,
        additionalHeaders,
        fullClasspath in Compile,
        artifactPath in (Compile, packageBin),
        resourceDirectories in Compile,
        embeddedJars,
        streams
      ) map Osgi.bundleTask,
      manifestHeaders <<= (
        bundleActivator,
        bundleSymbolicName,
        bundleVersion,
        dynamicImportPackage,
        exportPackage,
        importPackage,
        fragmentHost,
        privatePackage,
        requireBundle
      )(OsgiManifestHeaders),
      bundleActivator := None,
      bundleSymbolicName <<= (organization, name)(Osgi.defaultBundleSymbolicName),
      bundleVersion <<= version,
      dynamicImportPackage := Nil,
      exportPackage := Nil,
      importPackage := List("*"),
      fragmentHost := None,
      privatePackage <<= bundleSymbolicName(name => List(name + ".*")),
      requireBundle := Nil,
      additionalHeaders := Map.empty,
      embeddedJars := Nil
    )
  }
}
