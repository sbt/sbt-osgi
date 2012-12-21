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

import java.util.Properties
import java.io.File
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class packageSpec extends Specification {

  "Calling seqToStrOpt" should {
    "return None for an empty Seq" in {
      seqToStrOpt(Nil)(id) must be(None)
    }
    "return Some wrapping a properly consturcted String for a non-empty Seq" in {
      seqToStrOpt(Seq("foo", "bar", "baz"))(id) must beEqualTo(Some("foo,bar,baz"))
      seqToStrOpt(Seq(1, 2, 3))(_.toString) must beEqualTo(Some("1,2,3"))
    }
  }

  "Calling headersToProperties" should {
    "return the proper properties" in {
      val headers = OsgiManifestHeaders(
        Some("bundleActivator"),
        "bundleSymbolicName",
        "bundleVersion",
        Seq("dynamicImportPackage"),
        Seq("exportPackage1", "exportPackage2", "exportPackage3"),
        Seq("importPackage"),
        None,
        Seq("privatePackage"),
        Nil
      )
      val properties = headersToProperties(headers, Map.empty)
      import aQute.lib.osgi.Constants._
      properties.asScala must havePairs(
        BUNDLE_ACTIVATOR -> "bundleActivator",
        BUNDLE_SYMBOLICNAME -> "bundleSymbolicName",
        BUNDLE_VERSION -> "bundleVersion",
        DYNAMICIMPORT_PACKAGE -> "dynamicImportPackage",
        EXPORT_PACKAGE -> "exportPackage1,exportPackage2,exportPackage3",
        IMPORT_PACKAGE -> "importPackage",
        PRIVATE_PACKAGE -> "privatePackage"
      )
      properties.asScala must not(haveKey(FRAGMENT_HOST))
      properties.asScala must not(haveKey(REQUIRE_BUNDLE))
    }
  }

  "Calling defaultBundleSymbolicName" should {
    "concatenate organization and name properly" in {
      defaultBundleSymbolicName("a.b.c", "d.c") must beEqualTo("a.b.c.d.c")
      defaultBundleSymbolicName("a.b.c", "d-c") must beEqualTo("a.b.c.d.c")
      defaultBundleSymbolicName("a.b.c", "c.d") must beEqualTo("a.b.c.d")
      defaultBundleSymbolicName("a.b.c", "c-d") must beEqualTo("a.b.c.d")
      defaultBundleSymbolicName("", "a") must beEqualTo("a")
      defaultBundleSymbolicName("a", "") must beEqualTo("a")
      defaultBundleSymbolicName("", "") must beEqualTo("")
    }
  }

  "Calling includeResourceProperty" should {
    "add resources and embedded jars to INCLUDE_RESOURCE" in {
      val resources = Seq(new File("/resource"))
      val embeddedJars = Seq(new File("/aJar.jar"))
      val actual = includeResourceProperty(resources, embeddedJars)
      actual must beEqualTo(Some("/resource,/aJar.jar"))
    }
  }

  "Calling bundleClasspathProperty" should {
    "add bundle classes and embedded jars to classpath" in {
      val embeddedJars = Seq(new File("/aJar.jar"), new File("/bJar.jar"))
      val actual = bundleClasspathProperty(embeddedJars)
      actual must beEqualTo(Some(".,aJar.jar,bJar.jar"))
    }
    "remain default if no embedded jars are specified" in {
      val embeddedJars = Seq()
      val actual = bundleClasspathProperty(embeddedJars)
      actual must beEqualTo(None)
    }
  }
}
