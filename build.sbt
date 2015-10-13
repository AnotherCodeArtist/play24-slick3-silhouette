import com.tuplejump.sbt.yeoman.Yeoman

name := "play23-slick3-silhouette"

version := "1.0"

lazy val `play23-slick3-silhouette` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq( cache , ws , evolutions, filters )

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "3.0.4",
  "com.typesafe.play" %% "play-slick" % "1.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.0",
  "com.h2database" % "h2" % "1.4.187",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "net.ceedubs" % "ficus_2.11" % "1.1.2",
  "com.mohiva" % "play-silhouette-testkit_2.11" % "3.0.0" % "test"
)

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test"
)

libraryDependencies += specs2 % Test

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override" // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  //"-Ywarn-numeric-widen" // Warn when numerics are widened.
)

//********************************************************
// Yeoman settings
//********************************************************
Yeoman.yeomanSettings

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  