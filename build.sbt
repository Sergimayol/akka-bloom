val scala2Version = "2.13.16"
val akkaBloomVersion = "0.1.0-SNAPSHOT"
val organization = "com.example"
val organizationName = "example"

val AkkaVersion = "2.10.4"

lazy val akkaResolver =
  "Akka library repository".at("https://repo.akka.io/maven")

val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)

val commonDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)

lazy val root = (project in file("."))
  .aggregate(bloomCore, bloomApi, bloomAkka, bloomCluster, examples)
  .settings(
    name := "akka-bloom",
    version := akkaBloomVersion,
    scalaVersion := scala2Version
  )

lazy val bloomCore = project
  .in(file("akka-bloom-core"))
  .settings(
    name := "akka-bloom-core",
    version := akkaBloomVersion,
    scalaVersion := scala2Version,
    libraryDependencies ++= commonDependencies
  )

lazy val bloomApi = project
  .in(file("akka-bloom-api"))
  .settings(
    name := "akka-bloom-api",
    version := akkaBloomVersion,
    scalaVersion := scala2Version,
    resolvers += akkaResolver,
    libraryDependencies ++= akkaDependencies ++ commonDependencies
  )

lazy val bloomAkka =
  project
    .in(file("akka-bloom-akka"))
    .dependsOn(bloomCore, bloomApi)
    .settings(
      name := "akka-bloom-akka",
      version := akkaBloomVersion,
      scalaVersion := scala2Version,
      resolvers += akkaResolver,
      libraryDependencies ++= akkaDependencies ++ commonDependencies
    )

lazy val bloomCluster =
  project
    .in(file("akka-bloom-cluster"))
    .dependsOn(bloomCore, bloomApi)
    .settings(
      name := "akka-bloom-cluster",
      version := akkaBloomVersion,
      scalaVersion := scala2Version,
      resolvers += akkaResolver,
      libraryDependencies ++= akkaDependencies ++ commonDependencies
    )

lazy val examples =
  project
    .in(file("akka-bloom-examples"))
    .dependsOn(bloomAkka, bloomCluster)
    .settings(
      name := "akka-bloom-examples",
      version := akkaBloomVersion,
      scalaVersion := scala2Version,
      libraryDependencies ++= akkaDependencies,
      mainClass := Some("com.example.bloom.examples.main.Main")
    )
