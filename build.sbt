name := "junto"

version := "2.0-SNAPSHOT"

organization := "org.scalanlp"

scalaVersion := "2.10.6"

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

crossScalaVersions := Seq("2.10.6", "2.11.7")

libraryDependencies ++= Seq(
  "com.assembla.scala-incubator" %% "graph-core" % "1.8.1",
  "org.rogach" %% "scallop" % "0.9.4",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
)

bintrayOrganization := Some("scalanlp")

bintrayReleaseOnPublish := true
