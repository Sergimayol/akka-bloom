package com.example.bloom.examples.main

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AskPattern, Behaviors}
import akka.cluster.typed.{Cluster, Join}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}
import com.example.bloom.akka.api.{Add, MightContain, BloomFilterCommand}
import com.example.bloom.akka.cluster.BloomFilterClusterActor

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContextExecutor
import akka.util.Timeout

object MainClusterActors extends App {
  new MainClusterActors().run()
}

class MainClusterActors {
  def run(): Unit = {
    val system = ActorSystem[Nothing](Guardian(), "BloomCluster")

    // Join cluster (for local single-node demo)
    Cluster(system).manager ! Join(Cluster(system).selfMember.address)

    // For asking pattern
    implicit val timeout: Timeout = 3.seconds
    implicit val ec: ExecutionContextExecutor = system.executionContext

    // Wait for the cluster to be ready
    // Simple sleep to allow the cluster to stabilize (not ideal for production, better with clustering callbacks)
    Thread.sleep(5000)

    val sharding = ClusterSharding(system)

    val entityRef =
      sharding.entityRefFor(BloomFilterClusterActor.TypeKey[String], "filter-1")

    entityRef ! Add("grape")

    val resultFuture =
      entityRef.ask[Boolean](replyTo => MightContain("grape", replyTo))

    resultFuture.onComplete {
      case Success(true) =>
        println("'grape' might be in the set")
        system.terminate()
      case Success(false) =>
        println("'grape' is definitely not in the set")
        system.terminate()
      case Failure(ex) =>
        println(s"Error occurred: ${ex.getMessage}")
        system.terminate()
    }
  }

  object Guardian {
    def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
      val sharding = ClusterSharding(context.system)

      sharding.init(
        Entity(BloomFilterClusterActor.TypeKey[String])(ctx =>
          BloomFilterClusterActor.default[String]
        )
      )

      Behaviors.empty
    }
  }
}
