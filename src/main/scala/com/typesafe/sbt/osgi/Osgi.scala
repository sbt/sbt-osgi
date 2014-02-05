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
import resource._
import scala.collection.JavaConversions._
import java.io.{ FileInputStream, FileOutputStream }

private object Osgi {
  def bundleTask(
    headers: OsgiManifestHeaders,
    additionalHeaders: Map[String, String],
    fullClasspath: Seq[Attributed[File]],
    artifactPath: File,
    resourceDirectories: Seq[File],
    embeddedJars: Seq[File],
    streams: TaskStreams, 
    target: File): File = {

    //this is a cache file generated to check that the bnd parameters have not changed
    val manifest = target / "manifest.xml"

    val props = headersToProperties(headers, additionalHeaders)
    val oldProps = new Properties()

    //loads the previous set of headers into oldProps if we have a manifest.xml
    if (manifest.exists) managed(new FileInputStream(manifest)) foreach oldProps.load
    //saves the new properties into manifest.xml if they are different from the previous properties.
    if (!oldProps.equals(props)) managed(new FileOutputStream(manifest)) foreach (props.store(_, ""))

    //A helper function that turns a File object f into a flat array of Files, expanding f into its child files if f is a directory. If f has directories within it
    //  it expands those recursively.
    def expandClasspath(f: File): Array[File] = if (f.isDirectory) f.listFiles() flatMap expandClasspath else Array(f)

    //FileFunction.cached produces a function that takes a Set[File] and runs the function if the files in that set have changed at all, producing another Set[File].
    //  if the contents of the input set have not changed, the cached function returns the previously generated set of files. cachedFunction checks if the lastModified
    //  date of the input files has changed, and if the output set already exists or not. It runs the generator function if either the input set last modified dates are
    //  off or the generated files do not exist.
    val cachedFunction = FileFunction.cached(target / "package-cache", FilesInfo.lastModified, FilesInfo.exists) {
      (changes: Set[File]) ⇒
        val builder = new Builder
        builder.setClasspath(fullClasspath map (_.data) toArray)
        builder.setProperties(props)
        includeResourceProperty(resourceDirectories filter (_.exists), embeddedJars) foreach (dirs ⇒
          builder.setProperty(INCLUDE_RESOURCE, dirs)
        )
        bundleClasspathProperty(embeddedJars) foreach (jars ⇒
          builder.setProperty(BUNDLE_CLASSPATH, jars)
        )
        
        
	// Write to a temporary file to prevent trying to simultaneously read from and write to the
	// same jar file in exportJars mode (which causes a NullPointerException).        
        val tmpArtifactPath = file(artifactPath.absolutePath + ".tmp")
        
        // builder.build is not thread-safe because it uses a static SimpleDateFormat.  This ensures
	// that all calls to builder.build are serialized
        val jar = synchronized {
          builder.build
        }
        
        val log = streams.log
        builder.getWarnings foreach (s => log.warn(s"bnd: $s"))
        builder.getErrors foreach (s => log.error(s"bnd: $s"))
        jar.write(tmpArtifactPath)
        IO.move(tmpArtifactPath, artifactPath)
        Set(artifactPath)
    }

    cachedFunction((fullClasspath flatMap (a ⇒ expandClasspath(a.data)) toSet) ++ resourceDirectories.toSet ++ embeddedJars.toSet + manifest).head
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
