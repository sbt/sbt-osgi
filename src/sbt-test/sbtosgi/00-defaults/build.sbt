
organization := "com.typesafe.sbtosgi"

name := "sbtosgi-test"

version := "1.2.3"

libraryDependencies += "org.osgi" % "org.osgi.core" % "4.3.0" % "provided"

osgiSettings

TaskKey[Unit]("verify-bundle-activator") <<= OsgiKeys.bundleActivator map (activator =>
  if (activator.isDefined) error("Expected bundle-activator to be None, but was %s!" format activator)
)

TaskKey[Unit]("verify-bundle-symbolic-name") <<= OsgiKeys.bundleSymbolicName map (name =>
  if (name != "com.typesafe.sbtosgi.test")
    error("Expected bundle-symbolic-name to be %s, but was %s!".format("com.typesafe.sbtosgi.test", name))
)

TaskKey[Unit]("verify-bundle-verion") <<= OsgiKeys.bundleVersion map (version =>
  if (version != "1.2.3")
    error("Expected bundle-version to be %s, but was %s!".format("1.2.3", version))
)

TaskKey[Unit]("verify-dynamic-import-package") <<= OsgiKeys.dynamicImportPackage map (pkg =>
  if (!pkg.isEmpty)
    error("Expected dynamic-import-package to be empty, but was %s!" format pkg)
)

TaskKey[Unit]("verify-export-package") <<= OsgiKeys.exportPackage map (pkg =>
  if (!pkg.isEmpty)
    error("Expected export-package to be empty, but was %s!" format pkg)
)

TaskKey[Unit]("verify-import-package") <<= OsgiKeys.importPackage map (pkg =>
  if (pkg != Seq("*"))
    error("Expected import-package to be %s, but was %s!".format(Seq("*"), pkg))
)

TaskKey[Unit]("verify-fragment-host") <<= OsgiKeys.fragmentHost map (host =>
  if (host != None)
    error("Expected fragment-host to be None, but was %s!" format host)
)

TaskKey[Unit]("verify-private-package") <<= OsgiKeys.privatePackage map (pkg =>
  if (pkg != Seq("com.typesafe.sbtosgi.test.*"))
    error("Expected private-package to be %s, but was %s!".format(Seq("com.typesafe.sbtosgi.test.*"), pkg))
)

TaskKey[Unit]("verify-require-bundle") <<= OsgiKeys.requireBundle map (bundle =>
  if (!bundle.isEmpty)
    error("Expected require-bundle to be empty, but was %s!" format bundle)
)
