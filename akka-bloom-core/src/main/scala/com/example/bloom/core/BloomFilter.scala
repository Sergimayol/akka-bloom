package com.example.bloom.core

import java.util.BitSet
import scala.util.hashing.MurmurHash3

class BloomFilter[T](val size: Int, val numHashes: Int)
    extends IBloomFilter[T] {

  private val bitSet = new BitSet(size)

  private def hash(item: T, seed: Int): Int =
    Math.abs(MurmurHash3.stringHash(item.toString + seed.toString) % size)

  override def add(item: T): Unit = {
    require(item != null, "Cannot add null to BloomFilter")
    for (i <- 0 until numHashes) bitSet.set(hash(item, i))
  }

  override def mightContain(item: T): Boolean = {
    require(item != null, "Cannot check null in BloomFilter")
    (0 until numHashes).forall(i => bitSet.get(hash(item, i)))
  }

  override def toString: String =
    s"BloomFilter(size=$size, hashes=$numHashes, bitsSet=${bitSet.cardinality()})"
}

object BloomFilter {
  def apply[T](size: Int, numHashes: Int): BloomFilter[T] =
    new BloomFilter[T](size, numHashes)

  def default[T]: BloomFilter[T] = new BloomFilter[T](1000, 5)
}
