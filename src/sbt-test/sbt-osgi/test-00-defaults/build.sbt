lazy val test00 = (project in file("")).enablePlugins(SbtOsgi)

organization := "com.typesafe.sbt"

name := "sbt-osgi-test"

version := "1.2.3"

libraryDependencies += "org.osgi" % "org.osgi.core" % "4.3.0" % "provided"

osgiSettings

TaskKey[Unit]("verifyBundleActivator") := {
  val activator = OsgiKeys.bundleActivator.value
  if (activator.isDefined)
    sys.error("Expected bundle-activator to be None, but was %s!" format activator)
}

TaskKey[Unit]("verifyBundleSymbolicName") := {
  val name = OsgiKeys.bundleSymbolicName.value
  if (name != "com.typesafe.sbt.osgi.test")
    sys.error("Expected bundle-symbolic-name to be %s, but was %s!".format("com.typesafe.sbt.osgi.test", name))
}

TaskKey[Unit]("verifyBundleRequiredExecutionEnvironment") := {
  val re = OsgiKeys.bundleRequiredExecutionEnvironment.value
  if (re.nonEmpty)
    sys.error("Expected bundleRequiredExecutionEnvironment to be Nil, but was %s!" format re)
}

TaskKey[Unit]("verifyBundleVerion") := {
  val version = OsgiKeys.bundleVersion.value
  if (version != "1.2.3")
    sys.error("Expected bundle-version to be %s, but was %s!".format("1.2.3", version))
}

TaskKey[Unit]("verifyDynamicImportPackage") := {
  val pkg = OsgiKeys.dynamicImportPackage.value
  if (!pkg.isEmpty)
    sys.error("Expected dynamic-import-package to be empty, but was %s!" format pkg)
}

TaskKey[Unit]("verifyExportPackage") := {
  val pkg = OsgiKeys.exportPackage.value
  if (!pkg.isEmpty)
    sys.error("Expected export-package to be empty, but was %s!" format pkg)
}

TaskKey[Unit]("verifyImportPackage") := {
  val pkg = OsgiKeys.importPackage.value
  if (pkg != Seq("*"))
    sys.error("Expected import-package to be %s, but was %s!".format(Seq("*"), pkg))
}

TaskKey[Unit]("verifyFragmentHost") := {
  val host = OsgiKeys.fragmentHost.value
  if (host != None)
    sys.error("Expected fragment-host to be None, but was %s!" format host)
}

TaskKey[Unit]("verifyPrivatePackage") := {
  val pkg = OsgiKeys.privatePackage.value
  if (pkg != Seq("com.typesafe.sbt.osgi.test.*"))
    sys.error("Expected private-package to be %s, but was %s!".format(Seq("com.typesafe.sbt.osgi.test.*"), pkg))
}

TaskKey[Unit]("verifyRequireBundle") := {
  val bundle = OsgiKeys.requireBundle.value
  if (!bundle.isEmpty)
    sys.error("Expected require-bundle to be empty, but was %s!" format bundle)
}
