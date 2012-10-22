/*
 * Copyright 2011 Typesafe Inc.
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

object OsgiKeys {

  val bundle: TaskKey[File] =
    TaskKey[File](
      prefix("bundle"),
      "Create an OSGi bundle."
    )

  val manifestHeaders: SettingKey[OsgiManifestHeaders] =
    SettingKey[OsgiManifestHeaders](
      prefix("manifest-headers"),
      "The aggregated manifest headers."
    )

  val bundleActivator: SettingKey[Option[String]] =
    SettingKey[Option[String]](
      prefix("bundle-activator"),
      "Optional value for *Bundle-Activator* header."
    )

  val bundleSymbolicName: SettingKey[String] =
    SettingKey[String](
      prefix("bundle-symbolic-name"),
      "Value for *Bundle-SymbolicName* header."
    )

  val bundleVersion: SettingKey[String] =
    SettingKey[String](
      prefix("bundle-version"),
      "Value for *Bundle-Version* header."
    )

  val dynamicImportPackage: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](
      prefix("dynamic-import-package"),
      "Values for *Dynamic-ImportPackage* header."
    )

  val exportPackage: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](
      prefix("export-package"),
      "Values for *Export-Package* header."
    )

  val importPackage: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](
      prefix("import-package"),
      "Values for *Import-Package* header."
    )

  val fragmentHost: SettingKey[Option[String]] =
    SettingKey[Option[String]](
      prefix("fragment-host"),
      "Optional value for *Fragment-Host* header."
    )

  val privatePackage: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](
      prefix("private-package"),
      "Values for *Private-Package* header."
    )

  val requireBundle: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](
      prefix("require-bundle"),
      "Values for *Require-Bundle* header."
    )

  val additionalHeaders: SettingKey[Map[String, String]] =
    SettingKey[Map[String, String]](
      prefix("additional-headers"),
      "Additional headers to pass to BND."
    )

  private def prefix(key: String) = "osgi-" + key
}
