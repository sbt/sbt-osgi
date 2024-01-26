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

trait OsgiKeys {

  lazy val bundle: TaskKey[File] =
    TaskKey[File](prefix("Bundle"), "Create an OSGi bundle.")

  lazy val manifestHeaders: TaskKey[OsgiManifestHeaders] =
    TaskKey[OsgiManifestHeaders](prefix("ManifestHeaders"), "The aggregated manifest headers.")

  lazy val bundleActivator: SettingKey[Option[String]] =
    SettingKey[Option[String]](prefix("BundleActivator"), "Optional value for *Bundle-Activator* header.")

  lazy val bundleSymbolicName: SettingKey[String] =
    SettingKey[String](prefix("BundleSymbolicName"), "Value for *Bundle-SymbolicName* header.")

  lazy val bundleVersion: SettingKey[String] =
    SettingKey[String](prefix("BundleVersion"), "Value for *Bundle-Version* header.")

  lazy val bundleRequiredExecutionEnvironment: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](
      prefix("BundleRequiredExecutionEnvironment"),
      "Value for *Bundle-RequiredExecutionEnvironment* header."
    )

  lazy val dynamicImportPackage: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](prefix("DynamicImportPackage"), "Values for *Dynamic-ImportPackage* header.")

  lazy val exportPackage: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](prefix("ExportPackage"), "Values for *Export-Package* header.")

  lazy val importPackage: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](prefix("import-package"), "Values for *Import-Package* header.")

  lazy val fragmentHost: SettingKey[Option[String]] =
    SettingKey[Option[String]](prefix("FragmentHost"), "Optional value for *Fragment-Host* header.")

  lazy val privatePackage: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](prefix("PrivatePackage"), "Values for *Private-Package* header.")

  lazy val requireBundle: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](prefix("RequireBundle"), "Values for *Require-Bundle* header.")

  lazy val additionalHeaders: SettingKey[Map[String, String]] =
    SettingKey[Map[String, String]](prefix("AdditionalHeaders"), "Additional headers to pass to BND.")

  lazy val embeddedJars: TaskKey[Seq[File]] =
    TaskKey[Seq[File]](prefix("EmbeddedJars"), "Jar files to be embedded inside the bundle.")

  lazy val explodedJars: TaskKey[Seq[File]] =
    TaskKey[Seq[File]](prefix("ExplodedJars"), "Jar files to be exploded into the bundle.")

  lazy val requireCapability: TaskKey[String] =
    TaskKey[String](
      prefix("RequireCapability"),
      "Value for *Require-Capability* header. If not" +
        "specified defaults to 'osgi.ee;filter:=\"(&(osgi.ee=JavaSE)(version=*PROJECT JAVA VERSION*))\"'."
    )

  lazy val failOnUndecidedPackage: SettingKey[Boolean] =
    SettingKey[Boolean](
      prefix("FailOnUndecidedPackage"),
      "Fail the build if a package is neither exported or private." +
        "Without this setting such classes might be just transparently removed from the resulting artifact!"
    )

  lazy val packageWithJVMJar: SettingKey[Boolean] =
    SettingKey[Boolean](
      prefix("PackageWithJVMJar"),
      "Use the JVM jar tools to craft the bundle instead of the one from BND." +
        "Without this setting the produced bundle are detected as corrupted by recent JVMs"
    )

  lazy val cacheStrategy: SettingKey[Option[CacheStrategy]] =
    SettingKey[Option[CacheStrategy]](
      prefix("CacheBundle"),
      "Do not build a new bundle if a bundle already exists and has been crafted from identical inputs"
    )

  private def prefix(key: String) = "osgi" + key

  sealed trait CacheStrategy

  object CacheStrategy {
    object Hash extends CacheStrategy
    object LastModified extends CacheStrategy
  }
}

object OsgiKeys extends OsgiKeys
