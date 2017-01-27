name := "junto"

version := "2.0-SNAPSHOT"

organization := "org.scalanlp"

scalaVersion := "2.11.8"

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

//crossScalaVersions := Seq("2.10.6", "2.11.7")

libraryDependencies ++= Seq(
  "org.scala-graph" %% "graph-core" % "1.11.4",
  "org.rogach" %% "scallop" % "2.0.6",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
)

enablePlugins(JavaAppPackaging)

mainClass in Compile := Some("junto.Junto")
