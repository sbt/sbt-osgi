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

import java.nio.file.{ FileVisitOption, Files, Path }

import aQute.bnd.osgi.Builder
import aQute.bnd.osgi.Constants._
import com.github.sbt.osgi.OsgiKeys.CacheStrategy
import java.util.Properties
import java.util.function.Predicate
import java.util.stream.Collectors

import sbt._
import sbt.Keys._
import sbt.Package.ManifestAttributes

import scala.collection.JavaConverters._
import scala.language.implicitConversions

private object Osgi {

  def cachedBundle(
      headers: OsgiManifestHeaders,
      additionalHeaders: Map[String, String],
      fullClasspath: Seq[File],
      artifactPath: File,
      resourceDirectories: Seq[File],
      embeddedJars: Seq[File],
      explodedJars: Seq[File],
      failOnUndecidedPackage: Boolean,
      sourceDirectories: Seq[File],
      packageOptions: scala.Seq[sbt.PackageOption],
      useJVMJar: Boolean,
      cacheStrategy: Option[CacheStrategy]
  ): Option[File] = cacheStrategy.flatMap { strategy =>
    def fileFootprint(file: File) = {
      def footprint(f: File) =
        strategy match {
          case CacheStrategy.LastModified => FileInfo.lastModified(f).lastModified.toString
          case CacheStrategy.Hash         => Hash.toHex(FileInfo.hash(f).hash.toArray)
        }

      if (!file.exists()) Seq()
      else if (file.isDirectory)
        Files.walk(file.toPath).iterator().asScala.map(f => f.toAbsolutePath.toString -> footprint(f.toFile).toSeq)
      else Seq(file.absolutePath -> footprint(file))
    }

    def serialized =
      s"""$headers
         |$additionalHeaders
         |${fullClasspath.flatMap(fileFootprint)}
         |$artifactPath
         |${resourceDirectories.flatMap(fileFootprint)}
         |${embeddedJars.flatMap(fileFootprint)}
         |${explodedJars.flatMap(fileFootprint)}
         |$failOnUndecidedPackage
         |${sourceDirectories.flatMap(fileFootprint)}
         |$packageOptions
         |$useJVMJar
         |""".stripMargin

    def footprint = Hash.apply(serialized).mkString("")

    val footprintValue = footprint
    val bundleCacheFootprint = file(artifactPath.absolutePath + "_footprint")

    if (!bundleCacheFootprint.exists() || IO.read(bundleCacheFootprint) != footprintValue) {
      IO.write(bundleCacheFootprint, footprintValue)
      None
    } else if (artifactPath.exists()) Some(artifactPath)
    else None
  }
  def withCache(
      headers: OsgiManifestHeaders,
      additionalHeaders: Map[String, String],
      fullClasspath: Seq[File],
      artifactPath: File,
      resourceDirectories: Seq[File],
      embeddedJars: Seq[File],
      explodedJars: Seq[File],
      failOnUndecidedPackage: Boolean,
      sourceDirectories: Seq[File],
      packageOptions: scala.Seq[sbt.PackageOption],
      useJVMJar: Boolean,
      cacheStrategy: Option[CacheStrategy]
  )(produce: => File): File =
    cachedBundle(
      headers,
      additionalHeaders,
      fullClasspath,
      artifactPath,
      resourceDirectories,
      embeddedJars,
      explodedJars,
      failOnUndecidedPackage,
      sourceDirectories,
      packageOptions,
      useJVMJar,
      cacheStrategy
    ).getOrElse(produce)

  def bundleTask(
      headers: OsgiManifestHeaders,
      additionalHeaders: Map[String, String],
      fullClasspath: Seq[File],
      artifactPath: File,
      resourceDirectories: Seq[File],
      embeddedJars: Seq[File],
      explodedJars: Seq[File],
      failOnUndecidedPackage: Boolean,
      sourceDirectories: Seq[File],
      packageOptions: scala.Seq[sbt.PackageOption],
      useJVMJar: Boolean,
      cacheStrategy: Option[CacheStrategy],
      streams: TaskStreams
  ): File =
    withCache(
      headers,
      additionalHeaders,
      fullClasspath,
      artifactPath,
      resourceDirectories,
      embeddedJars,
      explodedJars,
      failOnUndecidedPackage,
      sourceDirectories,
      packageOptions,
      useJVMJar,
      cacheStrategy
    ) {
      val builder = new Builder

      if (failOnUndecidedPackage) {
        streams.log.info("Validating all packages are set private or exported for OSGi explicitly...")
        val internal = headers.privatePackage
        val exported = headers.exportPackage
        validateAllPackagesDecidedAbout(internal, exported, sourceDirectories)
      }

      builder.setClasspath(fullClasspath.toArray)

      val props = headersToProperties(headers, additionalHeaders)
      addPackageOptions(props, packageOptions)
      builder.setProperties(props)

      includeResourceProperty(resourceDirectories.filter(_.exists), embeddedJars, explodedJars) foreach (dirs =>
        builder.setProperty(INCLUDERESOURCE, dirs)
      )
      bundleClasspathProperty(embeddedJars) foreach (jars => builder.setProperty(BUNDLE_CLASSPATH, jars))
      // Write to a temporary file to prevent trying to simultaneously read from and write to the
      // same jar file in exportJars mode (which causes a NullPointerException).
      val tmpArtifactPath = file(artifactPath.absolutePath + ".tmp")
      // builder.build is not thread-safe because it uses a static SimpleDateFormat.  This ensures
      // that all calls to builder.build are serialized.
      val jar = synchronized {
        builder.build
      }
      val log = streams.log
      builder.getWarnings.asScala.foreach(s => log.warn(s"bnd: $s"))
      builder.getErrors.asScala.foreach(s => log.error(s"bnd: $s"))

      if (!useJVMJar) jar.write(tmpArtifactPath)
      else {
        val tmpArtifactDirectoryPath = file(artifactPath.absolutePath + "_tmpdir")
        IO.delete(tmpArtifactDirectoryPath)
        tmpArtifactDirectoryPath.mkdirs()

        val manifest = jar.getManifest
        jar.writeFolder(tmpArtifactDirectoryPath)

        def content = {
          import _root_.java.nio.file._
          import _root_.scala.collection.JavaConverters._
          val path = tmpArtifactDirectoryPath.toPath
          Files
            .walk(path)
            .iterator
            .asScala
            .map(f => f.toFile -> path.relativize(f))
            .collect { case (f, p) if p != (file("META-INF") / "MANIFEST.MF").toPath => (f, p.toString) }
            .toTraversable
        }

        IO.jar(content, tmpArtifactPath, manifest)
        IO.delete(tmpArtifactDirectoryPath)
      }

      IO.move(tmpArtifactPath, artifactPath)
      artifactPath
    }

  private def addPackageOptions(props: Properties, packageOptions: Seq[PackageOption]) = {
    packageOptions
      .collect({ case attr: ManifestAttributes ⇒ attr.attributes })
      .flatten
      .foreach { case (name, value) ⇒ props.put(name.toString, value) }
    props
  }

  def validateAllPackagesDecidedAbout(
      internal: Seq[String],
      exported: Seq[String],
      sourceDirectories: Seq[File]
  ): Unit = {
    val allPackages = sourceDirectories.flatMap { baseFile =>
      if (!baseFile.exists()) Nil
      else {
        val packages =
          Files
            .walk(baseFile.toPath, FileVisitOption.FOLLOW_LINKS)
            .filter((p: Path) => p.toFile.isDirectory) // uses conversions defined below to not look horrible
            .filter((p: Path) =>
              p.toFile.listFiles().exists(f => f.isFile)
            ) // uses conversions defined below to not look horrible
            .map[String]((p: Path) => {
              val pack = p.toString.replace(baseFile.toString, "").replaceAll("/", ".")
              if (pack.startsWith(".")) pack.substring(1) else pack
            })
            .collect(Collectors.toSet())

        import scala.collection.JavaConverters._
        packages.asScala
      }
    }.toSet

    def validateAllPackagesDecidedAbout(
        internal: Seq[String],
        exported: Seq[String],
        realPackages: List[String]
    ): Unit =
      if (internal.isEmpty && exported.isEmpty && realPackages.nonEmpty) {
        throw new RuntimeException(
          s"Remaining packages are undecided about (private or exported) for OSGi (this is rather dangerous!): ${realPackages}"
        )
      } else
        realPackages match {
          case Nil                       => // OK!
          case pack :: remainingPackages =>
            def startsWith(it: String, prefixes: Seq[String]): Boolean =
              prefixes.exists(it.startsWith)

            if (startsWith(pack, internal) || startsWith(pack, exported)) {
              validateAllPackagesDecidedAbout(internal, exported, remainingPackages)
            } else
              throw new RuntimeException(
                s"Unable to determine if [$pack] package is meant to be private or exported! " +
                  s"Please define what to do with this package for OSGi explicitly! \n" +
                  s"  Private packages : $internal\n" +
                  s"  Exported packages: $exported\n" +
                  s"  Offending package: $pack\n"
              )
        }

    val i = internal.map(_.replaceAll(".*", ""))
    val e = exported.map(_.replaceAll(".*", ""))
    validateAllPackagesDecidedAbout(i, e, allPackages.toList)
  }

  lazy val requireCapabilityTask: String = {
    Option(System.getProperty("java.version"))
      .map(v => v.split("[.]", 3).take(2).mkString("."))
      .map(version => "osgi.ee;filter:=\"(&(osgi.ee=JavaSE)(version=%s))\"".format(version))
      .getOrElse("")
  }

  def headersToProperties(headers: OsgiManifestHeaders, additionalHeaders: Map[String, String]): Properties = {
    import headers._
    val properties = new Properties
    properties.put(BUNDLE_SYMBOLICNAME, bundleSymbolicName)
    properties.put(BUNDLE_VERSION, bundleVersion)
    bundleActivator foreach (properties.put(BUNDLE_ACTIVATOR, _))
    strToStrOpt(bundleDescription) foreach (properties.put(BUNDLE_DESCRIPTION, _))
    bundleDocURL foreach (u => properties.put(BUNDLE_DOCURL, u.toString))
    bundleLicense.headOption foreach { case (license, url) =>
      properties.put(BUNDLE_LICENSE, s"${url.toString};description=$license")
    }
    strToStrOpt(bundleName) foreach (properties.put(BUNDLE_NAME, _))
    seqToStrOpt(bundleRequiredExecutionEnvironment)(id) foreach (properties.put(BUNDLE_REQUIREDEXECUTIONENVIRONMENT, _))
    strToStrOpt(bundleVendor) foreach (properties.put(BUNDLE_VENDOR, _))
    seqToStrOpt(dynamicImportPackage)(id) foreach (properties.put(DYNAMICIMPORT_PACKAGE, _))
    seqToStrOpt(exportPackage)(id) foreach (properties.put(EXPORT_PACKAGE, _))
    seqToStrOpt(importPackage)(id) foreach (properties.put(IMPORT_PACKAGE, _))
    fragmentHost foreach (properties.put(FRAGMENT_HOST, _))
    seqToStrOpt(privatePackage)(id) foreach (properties.put(PRIVATE_PACKAGE, _))
    seqToStrOpt(requireBundle)(id) foreach (properties.put(REQUIRE_BUNDLE, _))
    strToStrOpt(requireCapability) foreach (properties.put(REQUIRE_CAPABILITY, _))
    additionalHeaders foreach { case (k, v) => properties.put(k, v) }
    properties
  }

  def seqToStrOpt[A](seq: Seq[A])(f: A => String): Option[String] =
    if (seq.isEmpty) None else Some(seq map f mkString ",")

  def strToStrOpt(s: String): Option[String] = Option(s).filter(_.trim.nonEmpty)

  def includeResourceProperty(resourceDirectories: Seq[File], embeddedJars: Seq[File], explodedJars: Seq[File]) = {
    val paths: Seq[String] =
      (resourceDirectories ++ embeddedJars).map(_.getAbsolutePath) ++ explodedJars.map(f => "@" + f.getAbsolutePath)
    seqToStrOpt(paths)(identity)
  }

  def bundleClasspathProperty(embeddedJars: Seq[File]): Option[String] =
    seqToStrOpt(embeddedJars)(_.getName) map (".," + _)

  def defaultBundleSymbolicName(organization: String, name: String): String = {
    val organizationParts = parts(organization)
    val nameParts = parts(name)
    val partsWithoutOverlap = (organizationParts.lastOption, nameParts.headOption) match {
      case (Some(last), Some(head)) if last == head => organizationParts ++ nameParts.tail
      case _                                        => organizationParts ++ nameParts
    }
    partsWithoutOverlap mkString "."
  }

  def id(s: String) = s

  def parts(s: String) = s split "[.-]" filterNot (_.isEmpty)

  // ------------ Poor Man's Java 8 make-it-look-nice inter-op ----------------
  implicit def asPredicate[T](f: T => Boolean): Predicate[T] =
    (t: T) => f(t)
  implicit def asFunction[A, B](f: A => B): java.util.function.Function[A, B] =
    (a: A) => f(a)
  // ------------ Poor Man's Java 8 make-it-look-nice inter-op ----------------
}
