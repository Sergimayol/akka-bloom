package com.example.bloom.examples.main

import com.example.bloom.core.BloomFilter

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
  }
}
