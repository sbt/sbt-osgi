sbtosgi
=======

Plugin for `sbt`_ to create `OSGi`_ bundles.


Installing sbtosgi
------------------

Just add the following line to your plugin definition (*plugins.sbt* file in the *project/* folder of your project or *plugins.sbt* file in the *~/.sbt/plugins/* directory, for details about plugins see the `sbt documentation`_), paying attention to the blank line between settings::

  addSbtPlugin("com.typesafe.sbtosgi" % "sbtosgi" % "0.2.0")

If you want to use the latest and greates features, you can also give the latest snapshot release a try::

  resolvers += Classpaths.typesafeSnapshots

  addSbtPlugin("com.typesafe.sbtosgi" % "sbtosgi" % "0.3.0-SNAPSHOT")


Adding sbtosgi settings
-----------------------

Add the below line to your build definition, which will add the task *osgi-bundle* and also publish an OSGi bundle instead of a raw JAR archive. Again, pay attention to the blank line between settings::

  osgiSettings

If you don't want to publish an OSGi bundle instead of a raw JAR archive::

  defaultOsgiSettings

Notice that automatically publishing an OSGi bundle and *defaultOsgiSettings* are only available from version 0.3.0-SNAPSHOT on. If you are using version 0.2.0, you have to add the following settings to your build definition to enable publishing an OSGi bundle::

  packagedArtifact in (Compile, packageBin) <<= (artifact in (Compile, packageBin), OsgiKeys.bundle).identityMap

  artifact in (Compile, packageBin) ~= (_.copy(`type` = "bundle"))



Configuring sbtosgi
-------------------

This plugin comes with the following configuration options, available as sbt settings:

- *OsgiKeys.bundleActivator*: Optional value for *Bundle-Activator* header, default is *None*
- *OsgiKeys.bundleSymbolicName*: Value for *Bundle-SymbolicName* header, default is *organization* plus *name*
- *OsgiKeys.bundleVersion*: Value for *Bundle-Version* header, default is *version*
- *OsgiKeys.dynamicImportPackage*: Values for *Dynamic-ImportPackage* header, default is the empty sequence
- *OsgiKeys.export-package*: Values for *Export-Package* header, default is the empty sequence
- *OsgiKeys.importPackage*: Values for *Import-Package* header, default is "*"
- *OsgiKeys.fragmentHost*: Optional value for *Fragment-Host* header, default is *None*
- *OsgiKeys.privatePackage*: Values for *Private-Package* header, default is *OsgiKeys.bundleSymbolicName* plus ".*"
- *OsgiKeys.requireBundle*: Values for *Require-Bundle* header, default is the empty sequence
- *OsgiKeys.additionalHeaders*: Map of additional headers to be passed to BND, default is the empty sequence


Mailing list
------------

Please use the `sbt mailing list`_ and prefix the subject with "[sbtosgi]".


Contribution policy
-------------------

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license.


License
-------

This code is open source software licensed under the `Apache 2.0 License`_. Feel free to use it accordingly.

.. _`sbt`: https://github.com/harrah/xsbt/
.. _`OSGi`: http://www.osgi.org/
.. _`sbt documentation`: https://github.com/harrah/xsbt/wiki/Plugins
.. _`sbt mailing list`: mailto:simple-build-tool@googlegroups.com
.. _`Apache 2.0 License`: http://www.apache.org/licenses/LICENSE-2.0.html
