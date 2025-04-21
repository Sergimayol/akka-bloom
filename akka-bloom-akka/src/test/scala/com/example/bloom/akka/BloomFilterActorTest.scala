package com.example.bloom.akka

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

import com.example.bloom.akka.api.{Add, MightContain, BloomFilterCommand}

class BloomFilterActorTest
    extends ScalaTestWithActorTestKit
    with AnyWordSpecLike
    with Matchers {

  "BloomFilterActor" should {

    "add an item and confirm it might be present" in {
      val probe = createTestProbe[Boolean]()
      val actor = spawn(BloomFilterActor[String](1000, 3))

      actor ! Add("apple")
      actor ! MightContain("apple", probe.ref)

      probe.expectMessage(true)
    }

    "return false for item not added (most likely)" in {
      val probe = createTestProbe[Boolean]()
      val actor = spawn(BloomFilterActor[String](1000, 3))

      actor ! Add("apple")
      actor ! MightContain("banana", probe.ref)

      probe.expectMessage(false)
    }

    "handle multiple add and mightContain messages" in {
      val probe = createTestProbe[Boolean]()
      val actor = spawn(BloomFilterActor[String](1000, 3))

      actor ! Add("apple")
      actor ! Add("banana")
      actor ! MightContain("banana", probe.ref)
      actor ! MightContain("carrot", probe.ref)

      probe.expectMessage(true)
      probe.expectMessage(false)
    }

    "work with default constructor" in {
      val probe = createTestProbe[Boolean]()
      val actor = spawn(BloomFilterActor.default[String])

      actor ! Add("default")
      actor ! MightContain("default", probe.ref)

      probe.expectMessage(true)
    }

    "handle different types like Int" in {
      val probe = createTestProbe[Boolean]()
      val actor = spawn(BloomFilterActor[Int](1000, 4))

      actor ! Add(42)
      actor ! MightContain(42, probe.ref)
      actor ! MightContain(99, probe.ref)

      probe.expectMessage(true)
      probe.expectMessage(false)
    }
  }
}
