package com.example.bloom.core

package com.example.bloom.core

import org.scalatest.funsuite.AnyFunSuite

class ScalableBloomFilterTest extends AnyFunSuite {

  test("ScalableBloomFilter should add and detect an element") {
    val filter = ScalableBloomFilter[String](10, 3)

    filter.add("hello")

    assert(filter.mightContain("hello") == true)
    assert(filter.mightContain("world") == false)
  }

  test("ScalableBloomFilter should expand when an element already exists") {
    val filter = ScalableBloomFilter[String](2, 3)

    filter.add("hello")
    filter.add("world")

    assert(filter.mightContain("hello") == true)
    assert(filter.mightContain("world") == true)
  }

  test("ScalableBloomFilter should not contain an element that was not added") {
    val filter = ScalableBloomFilter[String](10, 3)

    filter.add("hello")

    assert(filter.mightContain("world") == false)
  }

  test(
    "ScalableBloomFilter should double the size of the filter when the first filter is full"
  ) {
    val filter = ScalableBloomFilter[String](2, 3)

    filter.add("hello")
    filter.add("world")

    assert(filter.getFilterSize > 1)
  }

  test("ScalableBloomFilter should work with the default constructor") {
    val filter = ScalableBloomFilter.default[String]

    filter.add("test")

    assert(filter.mightContain("test") == true)
  }
}
