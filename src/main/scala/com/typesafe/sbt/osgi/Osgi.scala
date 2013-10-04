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

import aQute.bnd.osgi.Builder
import aQute.bnd.osgi.Constants._
import java.util.Properties
import sbt._
import sbt.Keys._

private object Osgi {

  def bundleTask(
    headers: OsgiManifestHeaders,
    additionalHeaders: Map[String, String],
    fullClasspath: Seq[Attributed[File]],
    artifactPath: File,
    resourceDirectories: Seq[File],
    embeddedJars: Seq[File]): File = {
    val builder = new Builder
    builder.setClasspath(fullClasspath map (_.data) toArray)
    builder.setProperties(headersToProperties(headers, additionalHeaders))
    //builder.setProperty(aQute.lib.osgi.Constants.INCLUDE_RESOURCE, "")
    includeResourceProperty(resourceDirectories, embeddedJars) foreach (dirs =>
      builder.setProperty(INCLUDERESOURCE, dirs)
    )
    bundleClasspathProperty(embeddedJars) foreach (jars =>
      builder.setProperty(BUNDLE_CLASSPATH, jars)
    )
    val jar = builder.build
    jar.write(artifactPath)
    artifactPath
  }

  def headersToProperties(headers: OsgiManifestHeaders, additionalHeaders: Map[String, String]): Properties = {
    import headers._
    val properties = new Properties
    properties.put(BUNDLE_SYMBOLICNAME, bundleSymbolicName)
    properties.put(BUNDLE_VERSION, bundleVersion)
    bundleActivator foreach (properties.put(BUNDLE_ACTIVATOR, _))
    seqToStrOpt(dynamicImportPackage)(id) foreach (properties.put(DYNAMICIMPORT_PACKAGE, _))
    seqToStrOpt(exportPackage)(id) foreach (properties.put(EXPORT_PACKAGE, _))
    seqToStrOpt(importPackage)(id) foreach (properties.put(IMPORT_PACKAGE, _))
    fragmentHost foreach (properties.put(FRAGMENT_HOST, _))
    seqToStrOpt(privatePackage)(id) foreach (properties.put(PRIVATE_PACKAGE, _))
    seqToStrOpt(requireBundle)(id) foreach (properties.put(REQUIRE_BUNDLE, _))
    additionalHeaders foreach { case (k, v) => properties.put(k, v) }
    properties
  }

  def seqToStrOpt[A](seq: Seq[A])(f: A => String): Option[String] =
    if (seq.isEmpty) None else Some(seq map f mkString ",")

  def includeResourceProperty(resourceDirectories: Seq[File], embeddedJars: Seq[File]) =
    seqToStrOpt(resourceDirectories ++ embeddedJars)(_.getAbsolutePath)

  def bundleClasspathProperty(embeddedJars: Seq[File]) =
    seqToStrOpt(embeddedJars)(_.getName) map (".," + _)

  def defaultBundleSymbolicName(organization: String, name: String): String = {
    val organizationParts = parts(organization)
    val nameParts = parts(name)
    val partsWithoutOverlap = (organizationParts.lastOption, nameParts.headOption) match {
      case (Some(last), Some(head)) if (last == head) => organizationParts ++ nameParts.tail
      case _ => organizationParts ++ nameParts
    }
    partsWithoutOverlap mkString "."
  }

  def id(s: String) = s

  def parts(s: String) = s split "[.-]" filterNot (_.isEmpty)
}
