addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.1.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.7.1")

libraryDependencies += ("org.scala-sbt" % "scripted-plugin" % sbtVersion.value)
