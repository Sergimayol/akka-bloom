package com.example.bloom.akka.cluster

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.sharding.typed.scaladsl.EntityTypeKey
import com.example.bloom.akka.api.{BloomFilterCommand, Add, MightContain}
import com.example.bloom.core.BloomFilter

object BloomFilterClusterActor {

  def TypeKey[T]: EntityTypeKey[BloomFilterCommand[T]] =
    EntityTypeKey[BloomFilterCommand[T]]("BloomFilterEntity")

  def apply[T](
      entityId: String,
      size: Int,
      numHashes: Int
  ): Behavior[BloomFilterCommand[T]] = {
    val bloomFilter = BloomFilter[T](size, numHashes)
    behavior(bloomFilter)
  }

  def default[T]: Behavior[BloomFilterCommand[T]] = {
    val bloomFilter = BloomFilter.default[T]
    behavior(bloomFilter)
  }

  private def behavior[T](
      bloomFilter: BloomFilter[T]
  ): Behavior[BloomFilterCommand[T]] = {
    Behaviors.receiveMessage {
      case Add(item) =>
        bloomFilter.add(item)
        Behaviors.same

      case MightContain(item, replyTo) =>
        replyTo ! bloomFilter.mightContain(item)
        Behaviors.same
    }
  }
}
