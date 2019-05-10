name := "akkacluster-protobuf-versioning"

version := "0.1"

val currentScalaVersion = "2.12.2"
scalaVersion := currentScalaVersion

val akkaVersion = "2.5.21"
libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "org.scala-lang" % "scala-reflect" % currentScalaVersion
)

PB.protocVersion := "-v351"


PB.targets in Compile := Seq(
    scalapb.gen() -> (sourceManaged in Compile).value
)

fork in run := true