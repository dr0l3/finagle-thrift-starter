lazy val commonResolvers = Seq(
  resolvers += "twitter-repo" at "https://maven.twttr.com",
  resolvers += Resolver.bintrayRepo("twittercsl", "sbt-plugins/scrooge-sbt-plugin"),

  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val commonScalaVersion = Seq(
  scalaOrganization := "org.typelevel",
  scalaVersion := "2.12.4-bin-typelevel-4"
)

val twitterVersion = "17.12.0"

lazy val commonDeps = Seq(
  libraryDependencies ++= Seq(
    "com.twitter" %% "finagle-http" % twitterVersion,
    "org.apache.thrift" % "libthrift" % "0.9.2",
    "com.twitter" %% "scrooge-core" % twitterVersion exclude("com.twitter", "libthrift"),
    "com.twitter" %% "finagle-thrift" % twitterVersion exclude("com.twitter", "libthrift"),
    "com.twitter" %% "finagle-mux" % twitterVersion,
    "com.twitter" %% "finagle-thriftmux" % twitterVersion,
    "com.twitter" %% "finagle-zipkin" % twitterVersion,
    "org.slf4j" % "slf4j-api" % "1.7.5",
    "org.slf4j" % "slf4j-log4j12" % "1.7.5"

  )
)

lazy val commonMergeStrategy = assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

lazy val commonDocker = dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("anapsix/alpine-java")
    add(artifact, artifactTargetPath)
    expose(8000)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}

def dockerImageNames(name: String) = {
  imageNames in docker := Seq(
    // Sets the latest tag
    ImageName(s"dr0l3/fts-$name:latest")
  )
}

lazy val commonScalaFlags = Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xfuture", // Turn on future language features.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match", // Pattern match may not be typesafe.
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  //  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.

  //typelevel
  "-Yinduction-heuristics", // speeds up the compilation of inductive implicit resolution
  "-Ykind-polymorphism", // type and method definitions with type parameters of arbitrary kinds
  "-Yliteral-types", // literals can appear in type position
  "-Xstrict-patmat-analysis", // more accurate reporting of failures of match exhaustivity
  "-Xlint:strict-unsealed-patmat" // warn on inexhaustive matches against unsealed traits
)

def baseproject(loc: String): Project =
  Project(loc, file(loc))
    .settings(
      name := loc,
      commonResolvers,
      commonScalaVersion,
      commonDeps,
      scalacOptions ++= commonScalaFlags,
      commonDocker,
      commonMergeStrategy,
      dockerImageNames(loc)
    )


lazy val thrift = project.in(file("thrift"))
  .settings(
    name := "thrift",
    commonResolvers,
    commonScalaVersion,
    commonDeps)

lazy val server = baseproject("server")
  .dependsOn(thrift)
  .enablePlugins(DockerPlugin)

lazy val server2 = baseproject("server2")
  .dependsOn(thrift)
  .enablePlugins(DockerPlugin)

lazy val server3 = baseproject("server3")
  .dependsOn(thrift)
  .enablePlugins(DockerPlugin)


lazy val servers = (project in file("."))
  .aggregate(server, server2, server3)