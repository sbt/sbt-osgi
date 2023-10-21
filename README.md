sbt-osgi
========

[![Build Status](https://github.com/sbt/sbt-osgi/actions/workflows/ci.yml/badge.svg)](https://github.com/sbt/sbt-osgi/actions/workflows/ci.yml)

Plugin for [sbt](http://www.scala-sbt.org) to to create [OSGi](http://www.osgi.org/) bundles.

Installing sbt-osgi
-------------------

sbt-osgi is a plugin for sbt. In order to install sbt, please refer to the sbt documentation([0.13](https://www.scala-sbt.org/0.13/docs/Setup.html), [1.x](https://www.scala-sbt.org/1.x/docs/Setup.html)). Please make sure that you are using a suitable version of sbt:

- sbt-osgi 0.5 → sbt 0.12
- sbt-osgi 0.7 → sbt 0.13
- sbt-osgi 0.9.{0-3} → sbt 0.13 / sbt 1.x
- sbt-osgi 0.9.{4-5} -> sbt 1.x

As sbt-osgi is a plugin for sbt, it is installed like any other sbt plugin, that is by mere configuration: just add sbt-osgi to your global or local plugin definition. Global plugins are defined in `~/.sbt/<SBT_VERSION>/plugins/plugins.sbt` and local plugins are defined in `project/plugins.sbt` in your project.

In order to add sbt-osgi as a plugin, just add the below setting to the relevant plugin definition, paying attention to blank lines between settings:

```
// Other stuff

addSbtPlugin("com.typesafe.sbt" % "sbt-osgi" % "0.9.6")
```

If you want to use the latest and greatest features, you can instead have sbt depend on and locally build the current source snapshot by adding the following to your plugin definition file.
Example `<PROJECT_ROOT>/project/plugins.sbt`:
```scala
lazy val plugins = (project in file("."))
  .dependsOn(sbtOsgi)

// Other stuff

def sbtOsgi = uri("git://github.com/sbt/sbt-osgi.git")
```

Using sbt-osgi
---------------

#### Version 0.8.0 and above
As, since version `0.8.0`, sbt-osgi uses the sbt 0.13.5 *Autoplugin* feature, it can be enabled for individual projects like any other sbt Autoplugin. For more information on enabling and disabling plugins, refer to the [sbt plugins tutorial](http://www.scala-sbt.org/release/tutorial/Using-Plugins.html#Enabling+and+disabling+auto+plugins).

To enable sbt-osgi for a specific Project, use the project instance `enablePlugins(Plugins*)` method providing it with `SbtOsgi` as a parameter value. If using only '.sbt' definition files with only the implicitly declared root project with sbt 0.13.5 you will be required to obtain a reference to the project by explicitly declaring it in your build file. This may easily be done using the `project` macro, as shown in the example below. If using sbt 0.13.6 or greater, `enablePlugins(Plugins*)` is directly available in `.sbt` files.

Example `<PROJECT_ROOT>/build.sbt`:

```scala
// sbt 0.13.5
lazy val fooProject = (project in file(".")) // Obtain the root project reference
  .enablePlugins(SbtOsgi)  // Enables sbt-osgi for this project. This will automatically append
                           // the plugin's default settings to this project thus providing the
                           // `osgiBundle` task.

// sbt 0.13.6+
enablePlugins(SbtOsgi) // No need to obtain root project reference on single project builds for sbt 0.13.6+
```

Example `<PROJECT_ROOT>/project/Build.scala`:
```scala
import sbt._
import com.typesafe.sbt.SbtOsgi.autoImport._  // The autoImport object contains everything which would normally be
                                              // imported automatically in '*.sbt' project definition files.

object Build extends sbt.Build {

  lazy val fooProject = Project("foo-project", file("."))
    .enablePlugins(SbtOsgi)  // Enables sbt-osgi for this project. This will automatically append
                             // the plugin's default settings to this project thus providing the
                             // `osgiBundle` task.
}

```

To also override the default publish behaviour, also add the `osgiSettings` settings to your project via your preferred method.

Example `<PROJECT_ROOT>/build.sbt`:

```scala
// Other settings

osgiSettings
```

#### Version 0.7.0 and below
Add the below line to your sbt build definition, which adds the task `osgiBundle` which creates an OSGi bundle for your project and also changes the `publish` task to publish an OSGi bundle instead of a raw JAR archive. Again, pay attention to the blank line between settings:

```
// Other stuff

osgiSettings
```

If you just want `osgiBundle`, i.e. don't want to change the behavior of `publish`:


```
// Other stuff

defaultOsgiSettings
```

Settings
--------

sbt-osgi can be configured with the following settings:

- `bundleActivator`: value for `Bundle-Activator` header, default is `None`
- `bundleRequiredExecutionEnvironment`: value for `Bundle-RequiredExecutionEnvironment` header, default is an empty string.
- `bundleSymbolicName`: value for `Bundle-SymbolicName` header, default is `organization` plus `name`
- `bundleVersion`: value for `Bundle-Version` header, default is `version`
- `dynamicImportPackage`: values for `Dynamic-ImportPackage` header, default is the empty sequence
- `exportPackage`: values for `Export-Package` header, default is the empty sequence
- `importPackage`: values for `Import-Package` header, default is `*`
- `fragmentHost`: value for `Fragment-Host` header, default is `None`
- `privatePackage`: values for `Private-Package` header, default is `OsgiKeys.bundleSymbolicName` plus `.*`
- `requireBundle`: values for `Require-Bundle` header, default is the empty sequence
- `additionalHeaders`: map of additional headers to be passed to BND, default is the empty sequence
- `embeddedJars`: list of dependencies to embed inside the bundle which are automatically added to `Bundle-Classpath`
- `explodedJars`: list of jarfiles to explode into the bundle
- `requireCapability`: value for `Require-Capability` header, defaults to `osgi.ee;filter:="(&(osgi.ee=JavaSE)(version=*PROJECT JAVAC VERSION*))"`
- `failOnUndecidedPackage`: allows failing the build when a package is neither exported nor private (instead of silently dropping it), `false` by default to be compatible with previous behaviour 

Example `build.sbt`:

```
organization := "com.typesafe.sbt"

name := "osgi.demo"

version := "1.0.0"

enablePlugins(SbtOsgi)

libraryDependencies += "org.osgi" % "org.osgi.core" % "4.3.0" % "provided"

osgiSettings

OsgiKeys.exportPackage := Seq("com.typesafe.sbt.osgidemo")

OsgiKeys.bundleActivator := Option("com.typesafe.sbt.osgidemo.internal.Activator")
```

Contribution policy
-------------------

Contributions via GitHub pull requests are gladly accepted from their original author. 
Before we can accept pull requests, you will need to agree to the [Typesafe Contributor License Agreement](http://www.typesafe.com/contribute/cla) online, using your GitHub account - it takes 30 seconds.

License
-------

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
