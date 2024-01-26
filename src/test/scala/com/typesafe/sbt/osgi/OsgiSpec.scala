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

import aQute.bnd.osgi.Constants._
import java.io.File
import org.specs2.mutable.Specification
import scala.collection.JavaConverters._

class OsgiSpec extends Specification {

  import Osgi._

  "Calling seqToStrOpt" should {
    "return None for an empty Seq" in {
      seqToStrOpt(Nil)(id) must be(None)
    }
    "return Some wrapping a properly constructed String for a non-empty Seq" in {
      seqToStrOpt(Seq("foo", "bar", "baz"))(id) must beSome("foo,bar,baz")
      seqToStrOpt(Seq(1, 2, 3))(_.toString) must beSome("1,2,3")
    }
  }

  "Calling headersToProperties" should {
    "return the proper properties" in {
      val headers = OsgiManifestHeaders(
        Some("bundleActivator"),
        "bundleDescription",
        Some(sbt.url("http://example.example")),
        Seq(("License", sbt.url("http://license.license"))),
        "bundleName",
        Seq("req1", "req2"),
        "bundleVendor",
        "bundleSymbolicName",
        "bundleVersion",
        Seq("dynamicImportPackage"),
        Seq("exportPackage1", "exportPackage2", "exportPackage3"),
        Seq("importPackage"),
        None,
        Seq("privatePackage"),
        Nil,
        "requireCapability"
      )
      val properties = headersToProperties(headers, Map.empty)
      properties.asScala must havePairs(
        BUNDLE_ACTIVATOR -> "bundleActivator",
        BUNDLE_SYMBOLICNAME -> "bundleSymbolicName",
        BUNDLE_DESCRIPTION -> "bundleDescription",
        BUNDLE_DOCURL -> sbt.url("http://example.example").toString,
        BUNDLE_LICENSE -> "http://license.license;description=License",
        BUNDLE_NAME -> "bundleName",
        BUNDLE_REQUIREDEXECUTIONENVIRONMENT -> "req1,req2",
        BUNDLE_VENDOR -> "bundleVendor",
        BUNDLE_VERSION -> "bundleVersion",
        DYNAMICIMPORT_PACKAGE -> "dynamicImportPackage",
        EXPORT_PACKAGE -> "exportPackage1,exportPackage2,exportPackage3",
        IMPORT_PACKAGE -> "importPackage",
        PRIVATE_PACKAGE -> "privatePackage",
        REQUIRE_CAPABILITY -> "requireCapability"
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
    "add resources and embedded jars and exploded jars to INCLUDERESOURCE" in {
      val resourceDir = new File("/resource")
      val jar = new File("/aJar.jar")
      val anotherJar = new File("/anotherJar.jar")
      val actual = includeResourceProperty(Seq(resourceDir), Seq(jar), Seq(anotherJar))
      actual must beSome(resourceDir.getAbsolutePath + "," + jar.getAbsolutePath + ",@" + anotherJar.getAbsolutePath)
    }
  }

  "Calling bundleClasspathProperty" should {
    "add bundle classes and embedded jars to classpath" in {
      val embeddedJars = Seq(new File("/aJar.jar"), new File("/bJar.jar"))
      val actual = bundleClasspathProperty(embeddedJars)
      actual must beSome(".,aJar.jar,bJar.jar")
    }
    "remain default if no embedded jars are specified" in {
      val embeddedJars = Seq()
      val actual = bundleClasspathProperty(embeddedJars)
      actual must beNone
    }
  }
}
