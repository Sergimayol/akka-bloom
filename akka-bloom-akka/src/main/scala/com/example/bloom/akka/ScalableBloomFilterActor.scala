package com.example.bloom.akka

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef

import com.example.bloom.akka.api.{BloomFilterCommand, Add, MightContain}
import com.example.bloom.core.ScalableBloomFilter

object ScalableBloomFilterActor {
  def apply[T](size: Int, numHashes: Int): Behavior[BloomFilterCommand[T]] = {
    val bloomFilter = ScalableBloomFilter[T](size, numHashes)
    behavior(bloomFilter)
  }

  def default[T]: Behavior[BloomFilterCommand[T]] = {
    val bloomFilter = ScalableBloomFilter.default[T]
    behavior(bloomFilter)
  }

  private def behavior[T](
      bloomFilter: ScalableBloomFilter[T]
  ): Behavior[BloomFilterCommand[T]] = {
    Behaviors.receiveMessage {
      case Add(item) =>
        bloomFilter.add(item)
        Behaviors.same

      case MightContain(item, replyTo) =>
        val result = bloomFilter.mightContain(item)
        replyTo ! result
        Behaviors.same
    }
  }
}
