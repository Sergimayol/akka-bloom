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

object Main extends App {
  new Main().run()
}

class Main {
  def run(): Unit = {
    val filter = BloomFilter[String](size = 1000, numHashes = 5)
    filter.add("hello")
    filter.add("world")

    println("contains: hello? " + filter.mightContain("hello"))
    println("contains: world? " + filter.mightContain("world"))
    println("contains: examples? " + filter.mightContain("examples"))

    val defaultFilter = BloomFilter.default[String]
    defaultFilter.add("world")

    println("contains: hello? " + defaultFilter.mightContain("hello"))
    println("contains: world? " + defaultFilter.mightContain("world"))

    implicit val timeout: Timeout = 3.seconds

    val system: ActorSystem[BloomFilterCommand[String]] =
      ActorSystem(BloomFilterActor[String](1000, 5), "BloomFilterSystem")

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
        case Failure(ex) => println(s"Error checking '$item': ${ex.getMessage}")
      }
    }

  }
}
