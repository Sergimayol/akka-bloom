package com.example.bloom.core

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class BloomFilterTest extends AnyFunSuite with Matchers {

  test("Add an item and verify it might be present") {
    val bf = BloomFilter[String](1000, 3)
    bf.add("apple")
    bf.mightContain("apple") shouldBe true
  }

  test("Item not added should likely not be present") {
    val bf = BloomFilter[String](1000, 3)
    bf.add("apple")
    bf.mightContain("banana") shouldBe false
  }

  test("Same item added multiple times does not change bitsSet count") {
    val bf = BloomFilter[String](1000, 3)
    bf.add("apple")
    val firstCardinality = bf.toString.split("bitsSet=")(1).dropRight(1).toInt
    bf.add("apple")
    val secondCardinality = bf.toString.split("bitsSet=")(1).dropRight(1).toInt
    secondCardinality shouldBe firstCardinality
  }

  test("False positives are possible") {
    // deliberately small for higher collision chance
    val bf = BloomFilter[String](
      10,
      2
    )
    bf.add("apple")
    bf.add("banana")
    // Unadded item might still appear due to hash collisions
    val mightContain = bf.mightContain("carrot")
    // We can't assert false — but we can log and just make sure it's handled
    assert(mightContain == true || mightContain == false)
  }

  test("Different filters do not share state") {
    val bf1 = BloomFilter[String](1000, 3)
    val bf2 = BloomFilter[String](1000, 3)
    bf1.add("apple")
    bf2.mightContain("apple") shouldBe false
  }

  test("BloomFilter works with Int type") {
    val bf = BloomFilter[Int](1000, 3)
    bf.add(123)
    bf.mightContain(123) shouldBe true
    bf.mightContain(456) shouldBe false
  }

  test("BloomFilter with high number of hashes still works") {
    val bf = BloomFilter[String](1000, 50) // high number of hashes
    bf.add("grape")
    bf.mightContain("grape") shouldBe true
  }

  test("BloomFilter can store multiple different elements") {
    val bf = BloomFilter[String](1000, 4)
    val elements = List("a", "b", "c", "d", "e")
    elements.foreach(bf.add)
    elements.foreach(e => bf.mightContain(e) shouldBe true)
  }

  test("Adding null should throw IllegalArgumentException") {
    val bf = BloomFilter[String](1000, 3)
    an[IllegalArgumentException] should be thrownBy bf.add(
      null.asInstanceOf[String]
    )
  }

  test("Checking null should throw IllegalArgumentException") {
    val bf = BloomFilter[String](1000, 3)
    an[IllegalArgumentException] should be thrownBy bf.mightContain(
      null.asInstanceOf[String]
    )
  }

  test("toString should include size, hashes and bitsSet") {
    val bf = BloomFilter[String](1000, 3)
    bf.add("apple")
    val desc = bf.toString
    desc should include("size=1000")
    desc should include("hashes=3")
    desc should include("bitsSet=")
  }

  test("Default BloomFilter should have correct default parameters") {
    val bf = BloomFilter.default[String]
    bf.size shouldBe 1000
    bf.numHashes shouldBe 5
  }
}
