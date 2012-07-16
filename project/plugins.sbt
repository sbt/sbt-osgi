
resolvers ++= Seq(
)

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.5")

addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.5.0")

libraryDependencies <+= (sbtVersion)("org.scala-sbt" % "scripted-plugin" % _)
