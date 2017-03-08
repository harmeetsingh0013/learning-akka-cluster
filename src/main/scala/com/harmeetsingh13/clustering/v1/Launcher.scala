package com.harmeetsingh13.clustering.v1

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by Harmeet Singh(Taara) on 4/3/17.
  */
object Launcher extends App {

  val seedConfig = ConfigFactory.load("seed")
  val seedSystem = ActorSystem("words", seedConfig)
}
