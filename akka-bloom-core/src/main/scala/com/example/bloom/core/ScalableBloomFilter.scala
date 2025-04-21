package com.example.bloom.core

class ScalableBloomFilter[T](val initialSize: Int, val numHashes: Int)
    extends IBloomFilter[T] {
  private var filters = List(BloomFilter[T](initialSize, numHashes))

  override def add(element: T): Unit = {
    if (filters.head.mightContain(element)) {
      val newFilter = BloomFilter[T](filters.head.size * 2, numHashes)
      filters = newFilter :: filters
    }
    filters.head.add(element)
  }

  override def mightContain(element: T): Boolean = {
    filters.exists(_.mightContain(element))
  }

  // For Testing Only
  def getFilterSize: Int = filters.size
}

object ScalableBloomFilter {
  def apply[T](size: Int, numHashes: Int): ScalableBloomFilter[T] =
    new ScalableBloomFilter[T](size, numHashes)

  def default[T]: ScalableBloomFilter[T] = new ScalableBloomFilter[T](1000, 5)
}
