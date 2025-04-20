package com.example.bloom.akka.api

import akka.actor.typed.ActorRef

sealed trait BloomFilterCommand[T]

case class Add[T](item: T) extends BloomFilterCommand[T]

case class MightContain[T](item: T, replyTo: ActorRef[Boolean])
    extends BloomFilterCommand[T]
