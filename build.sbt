import AssemblyKeys._

name := "junto"

version := "1.6.0-SNAPSHOT"

organization := "org.scalanlp"

scalaVersion := "2.10.1"

crossPaths := false

retrieveManaged := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.10" % "2.1.0",
  "org.clapper" % "argot_2.10" % "1.0.0",
  "net.sf.trove4j" % "trove4j" % "3.0.3",
  "com.typesafe" % "scalalogging-log4j_2.10" % "1.0.1")

seq(assemblySettings: _*)

jarName in assembly := "junto-assembly.jar"

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://scalanlp.org/</url>
  <licenses>
    <license>
      <name>Apache License 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:scalanlp/junto.git</url>
    <connection>scm:git:git@github.com:scalanlp/junto.git</connection>
  </scm>
  <developers>
    <developer>
      <id>parthatalukdar</id>
      <name>Partha Talukdar</name>
      <url>http://talukdar.net</url>
    </developer>
    <developer>
      <id>jasonbaldridge</id>
      <name>Jason Baldridge</name>
      <url>http://www.jasonbaldridge.com</url>
    </developer>
  </developers>
)
