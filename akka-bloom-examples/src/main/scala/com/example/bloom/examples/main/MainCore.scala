package com.example.bloom.examples.main

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef

import com.example.bloom.akka.api.{BloomFilterCommand, Add, MightContain}
import com.example.bloom.core.{BloomFilter, ScalableBloomFilter}
import akka.actor.typed.ActorSystem
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Success, Failure}
import akka.actor.typed.scaladsl.AskPattern._
import akka.cluster.typed.{Cluster, Join}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}
import com.example.bloom.akka.cluster.BloomFilterClusterActor

object MainCore extends App {
  new MainCore().run()
}

class MainCore {
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

    val scalableBloomFilter = new ScalableBloomFilter[String](100, 3)
    scalableBloomFilter.add("hello")
    println("contains: hello? " + scalableBloomFilter.mightContain("hello"))
    println("contains: world? " + scalableBloomFilter.mightContain("world"))
  }
}
