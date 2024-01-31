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

package com.github.sbt.osgi

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object SbtOsgi extends AutoPlugin {

  override lazy val trigger: PluginTrigger = noTrigger

  override lazy val requires: Plugins = JvmPlugin

  override lazy val projectSettings: Seq[Def.Setting[_]] = defaultOsgiSettings

  override lazy val globalSettings: Seq[Def.Setting[_]] = defaultGlobalSettings

  object autoImport {
    type OsgiManifestHeaders = com.github.sbt.osgi.OsgiManifestHeaders

    val OsgiKeys = com.github.sbt.osgi.OsgiKeys

    lazy val osgiSettings: Seq[Setting[_]] = Seq(
      Compile / packageBin / packagedArtifact := Scoped
        .mkTuple2((Compile / packageBin / artifact).value, OsgiKeys.bundle.value),
      Compile / packageBin / artifact ~= (_.withType("bundle"))
    )
  }

  lazy val defaultOsgiSettings: Seq[Setting[_]] = {
    import OsgiKeys._
    Seq(
      (Compile / exportJars) := false,
      bundle := Osgi.bundleTask(
        manifestHeaders.value,
        additionalHeaders.value,
        (Compile / fullClasspath).value.map(_.data) ++ (Compile / internalDependencyAsJars).value.map(_.data),
        (Compile / packageBin / artifactPath).value,
        (Compile / resourceDirectories).value,
        embeddedJars.value,
        explodedJars.value,
        failOnUndecidedPackage.value,
        (Compile / sourceDirectories).value,
        (Compile / packageBin / packageOptions).value,
        packageWithJVMJar.value,
        cacheStrategy.value,
        streams.value
      ),
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
        requireCapability.value
      ),
      bundleSymbolicName := Osgi.defaultBundleSymbolicName(organization.value, normalizedName.value),
      privatePackage := bundleSymbolicName(name => List(name + ".*")).value,
      bundleVersion := version.value
    )
  }

  lazy val defaultGlobalSettings: Seq[Setting[_]] = {
    import OsgiKeys._
    Seq(
      bundle := file(""),
      bundleActivator := None,
      bundleSymbolicName := "",
      bundleVersion := "",
      bundleRequiredExecutionEnvironment := Nil,
      dynamicImportPackage := Nil,
      exportPackage := Nil,
      importPackage := List("*"),
      fragmentHost := None,
      privatePackage := Nil,
      requireBundle := Nil,
      failOnUndecidedPackage := false,
      requireCapability := Osgi.requireCapabilityTask,
      additionalHeaders := Map.empty,
      embeddedJars := Nil,
      explodedJars := Nil,
      packageWithJVMJar := false,
      cacheStrategy := None
    )
  }
}
