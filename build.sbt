
//version := "0.1"
//
//mainClass := Some("server/com.example.Example")

enablePlugins(JavaAppPackaging)


lazy val commonResolvers = Seq(
  resolvers += "twitter-repo" at "https://maven.twttr.com",
//  resolvers += Resolver.bintrayRepo("twittercsl", "sbt-plugins/scrooge-sbt-plugin"),
  resolvers += "Twitter Repository" at "http://maven.twttr.com/"
)

lazy val commonScalaVersion = Seq(
//  scalaOrganization := "org.typelevel",
  scalaVersion := "2.11.8"
)

lazy val twitterVersion = "6.38.0"

lazy val commonDeps = Seq(
  libraryDependencies ++= Seq(
    "org.apache.thrift" % "libthrift"     % "0.9.2",
//    "com.twitter" %% "scrooge-core"       % "6.38.0" exclude("com.twitter", "libthrift"),
    "com.twitter" %% "finagle-thrift"     % "6.38.0" exclude("com.twitter", "libthrift"),
    "com.twitter" %% "finagle-zipkin"     % "6.38.0",
    "org.slf4j"   %  "slf4j-api"          % "1.7.5",
    "org.slf4j"   %  "slf4j-log4j12"      % "1.7.5"

  )
)


lazy val thrift = project.in(file("thrift"))
  .settings(
    name := "thrift",
    commonResolvers,
    commonScalaVersion,
    commonDeps,
    scalacOptions := Seq(
      "-feature",
      "-unchecked",
      "-deprecation",
      "-encoding", "utf8",
      "-language:postfixOps",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-target:jvm-1.7",
      "-Yclosure-elim",
      "-Yinline"
    ),
    Seq(
      (scroogeThriftOutputFolder in Compile) <<= (sourceManaged in Compile) (_ / "scrooge"),
      (scroogeThriftOutputFolder in Test) <<= (sourceManaged in Test) (_ / "scrooge"),
      (scroogeIsDirty in Compile) <<= (scroogeIsDirty in Compile) map { (_) => true }
    )
  )



lazy val server = project.in(file("server"))
  .settings(
    name := "server",
    commonResolvers,
    commonScalaVersion,
    commonDeps
  ).dependsOn(thrift)

lazy val server2 = project.in(file("server2"))
  .settings(
    name := "server",
    commonResolvers,
    commonScalaVersion,
    commonDeps
  ).dependsOn(thrift)

lazy val server3 = project.in(file("server3"))
  .settings(
    name := "server",
    commonResolvers,
    commonScalaVersion,
    commonDeps
  ).dependsOn(thrift)


lazy val root = (project in file("."))
    .settings(
      scalaVersion := "2.11.8"
    )
  .aggregate(server, thrift, server2, server3)
