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
import sbt.plugins.JvmPlugin

object SbtOsgi extends AutoPlugin {

  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = JvmPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = defaultOsgiSettings

  object autoImport {
    type OsgiManifestHeaders = com.typesafe.sbt.osgi.OsgiManifestHeaders

    val OsgiKeys = com.typesafe.sbt.osgi.OsgiKeys

    def osgiSettings: Seq[Setting[_]] = Seq(
      packagedArtifact in (Compile, packageBin) := Scoped.mkTuple2((artifact in (Compile, packageBin)).value, OsgiKeys.bundle.value),
      SbtCompat.packageBinBundle)
  }

  def defaultOsgiSettings: Seq[Setting[_]] = {
    import OsgiKeys._
    Seq(
      bundle := Osgi.bundleTask(
        manifestHeaders.value,
        additionalHeaders.value,
        (dependencyClasspathAsJars in Compile).value.map(_.data) ++ (products in Compile).value,
        (artifactPath in (Compile, packageBin)).value,
        (resourceDirectories in Compile).value,
        embeddedJars.value,
        explodedJars.value,
        failOnUndecidedPackage.value,
        (sourceDirectories in Compile).value,
        (packageOptions in (Compile, packageBin)).value,
        streams.value),
      manifestHeaders := OsgiManifestHeaders(
        bundleActivator.value,
        description.value,
        apiURL.value,
        licenses.value,
        name.value,
        bundleRequiredExecutionEnvironment.value,
        organizationName.value,
        bundleSymbolicName.value,
        bundleVersion.value,
        dynamicImportPackage.value,
        exportPackage.value,
        importPackage.value,
        fragmentHost.value,
        privatePackage.value,
        requireBundle.value,
        requireCapability.value),
      bundleActivator := None,
      bundleSymbolicName := Osgi.defaultBundleSymbolicName(organization.value, normalizedName.value),
      bundleVersion := version.value,
      bundleRequiredExecutionEnvironment := Nil,
      dynamicImportPackage := Nil,
      exportPackage := Nil,
      importPackage := List("*"),
      fragmentHost := None,
      privatePackage := bundleSymbolicName(name => List(name + ".*")).value,
      requireBundle := Nil,
      failOnUndecidedPackage := false,
      requireCapability := Osgi.requireCapabilityTask(),
      additionalHeaders := Map.empty,
      embeddedJars := Nil,
      explodedJars := Nil)
  }
}
