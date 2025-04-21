package com.example.bloom.examples.main

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef

import com.example.bloom.akka.api.{BloomFilterCommand, Add, MightContain}
import com.example.bloom.core.BloomFilter
import akka.actor.typed.ActorSystem
import com.example.bloom.akka.BloomFilterActor
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Success, Failure}
import akka.actor.typed.scaladsl.AskPattern._
import akka.cluster.typed.{Cluster, Join}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}
import com.example.bloom.akka.cluster.BloomFilterClusterActor
import com.example.bloom.akka.ScalableBloomFilterActor

object MainActors extends App {
  new MainActors().run()
}

class MainActors {
  def run(): Unit = {
    implicit val timeout: Timeout = 3.seconds

    val system: ActorSystem[BloomFilterCommand[String]] =
      ActorSystem(BloomFilterActor[String](1000, 5), "BloomFilterSystem")

    // val system: ActorSystem[BloomFilterCommand[String]] =
    //   ActorSystem(
    //     ScalableBloomFilterActor[String](1000, 5),
    //     "ScalableBloomFilterActorSystem"
    //   )

    import system.executionContext
    implicit val scheduler = system.scheduler

    system ! Add("apple")
    system ! Add("banana")

    val queries = List("apple", "grape", "banana")

    queries.foreach { item =>
      val response: Future[Boolean] =
        system.ask(replyTo => MightContain(item, replyTo))

      response.onComplete {
        case Success(result) =>
          println(s"'$item' might be in the BloomFilter? $result")
          system.terminate()
        case Failure(ex) =>
          println(s"Error checking '$item': ${ex.getMessage}")
          system.terminate()
      }
    }
  }

}
