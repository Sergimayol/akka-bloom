val AkkaVersion = "2.10.4"
val ScalaTestVersion = "3.2.17"
val ScalaVersion = "2.13.16"
val BloomFilterVersion = "0.1.0-SNAPSHOT"

lazy val commonSettings = Seq(
  scalaVersion := ScalaVersion,
  organization := "com.example",
  version := BloomFilterVersion
)

lazy val akkaResolver =
  "Akka library repository" at "https://repo.akka.io/maven"

lazy val commonDependencies = Seq(
  "org.scalatest" %% "scalatest" % ScalaTestVersion % Test
)

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)

lazy val akkaClusterDependencies = Seq(
  "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion
)

lazy val root = (project in file("."))
  .aggregate(bloomCore, bloomApi, bloomAkka, bloomCluster, examples)
  .settings(commonSettings: _*)

lazy val bloomCore = project
  .in(file("akka-bloom-core"))
  .settings(commonSettings: _*)
  .settings(
    name := "akka-bloom-core",
    libraryDependencies ++= commonDependencies
  )

lazy val bloomApi = project
  .in(file("akka-bloom-api"))
  .settings(commonSettings: _*)
  .settings(
    name := "akka-bloom-api",
    resolvers += akkaResolver,
    libraryDependencies ++= akkaDependencies ++ commonDependencies
  )

lazy val bloomAkka = project
  .in(file("akka-bloom-akka"))
  .dependsOn(bloomCore, bloomApi)
  .settings(commonSettings: _*)
  .settings(
    name := "akka-bloom-akka",
    resolvers += akkaResolver,
    libraryDependencies ++= akkaDependencies ++ commonDependencies
  )

lazy val bloomCluster = project
  .in(file("akka-bloom-cluster"))
  .dependsOn(bloomCore, bloomApi)
  .settings(commonSettings: _*)
  .settings(
    name := "akka-bloom-cluster",
    resolvers += akkaResolver,
    libraryDependencies ++= akkaDependencies ++ commonDependencies ++ akkaClusterDependencies
  )

lazy val examples = project
  .in(file("akka-bloom-examples"))
  .dependsOn(bloomAkka, bloomCluster)
  .settings(commonSettings: _*)
  .settings(
    name := "akka-bloom-examples",
    libraryDependencies ++= akkaDependencies ++ akkaClusterDependencies
  )

lazy val examplesCore = project
  .in(file("akka-bloom-examples-core"))
  .dependsOn(bloomAkka, bloomCluster)
  .settings(commonSettings: _*)
  .settings(
    name := "akka-bloom-examples-core",
    mainClass := Some("com.example.bloom.examples.main.MainCore")
  )

lazy val examplesActors = project
  .in(file("akka-bloom-examples-actors"))
  .dependsOn(bloomAkka, bloomCluster)
  .settings(commonSettings: _*)
  .settings(
    name := "akka-bloom-examples-actors",
    mainClass := Some("com.example.bloom.examples.main.MainActors")
  )

lazy val examplesClusterActors = project
  .in(file("akka-bloom-examples-cluster-actors"))
  .dependsOn(bloomAkka, bloomCluster)
  .settings(commonSettings: _*)
  .settings(
    name := "akka-bloom-examples-cluster-actors",
    mainClass := Some("com.example.bloom.examples.main.MainClusterActors")
  )
