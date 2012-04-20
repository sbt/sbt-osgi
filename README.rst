sbtosgi
=======

Plugin for `sbt`_ to create `OSGi`_ bundles. More details to come ...


Installing sbtosgi
------------------

Just add the following lines to your plugin definition (*plugins.sbt* file in the *project/* folder of your project or *plugins.sbt* file in the *~/.sbt/plugins/* directory, for details about plugins see the `sbt documentation`_), paying attention to the blank line between settings:

::

  resolvers += Classpaths.typesafeResolver

  addSbtPlugin("com.typesafe.sbtosgi" % "sbtosgi" % "0.1.0")


Adding sbtosgi settings
-----------------------

Add the below line to your build definition, which will add the task *osgi-bundle*, paying attention to the blank line between settings:

::

    seq(osgiSettings: _*)


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
