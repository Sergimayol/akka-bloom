package com.example.bloom.core

trait IBloomFilter[T] {
  def add(item: T): Unit
  def mightContain(item: T): Boolean
}
