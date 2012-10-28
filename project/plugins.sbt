
addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.0.0")

libraryDependencies <+= (sbtVersion)("org.scala-sbt" % "scripted-plugin" % _)

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.0")
