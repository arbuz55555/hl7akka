import sbtassembly.Plugin.AssemblyKeys._

name := "hl7-parser-akka"

version := "1.0"

scalaVersion := "2.11.5"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray Repository" at "http://repo.spray.io"
)

libraryDependencies ++= {
  val akkaVersion       = "2.3.4"
  val sprayVersion      = "1.3.2"
  val hapiVersion       = "2.2"
  Seq(
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-testkit" % sprayVersion % "test",
    "io.spray" %% "spray-json" % "1.3.1",
    "io.spray" %% "spray-http" % sprayVersion,
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest"     %% "scalatest"       % "2.2.0"       % "test",
    "ca.uhn.hapi" % "hapi-base" % "2.2",
    "ca.uhn.hapi" % "hapi-structures-v22" % "2.2"
  )
}

// Assembly settings
mainClass in Global := Some("io.hl7akka.core.Main")

jarName in assembly := "hl7akka.jar"

assemblySettings