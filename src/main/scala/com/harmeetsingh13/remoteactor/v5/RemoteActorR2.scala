package com.harmeetsingh13.remoteactor.v5

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by Harmeet Singh(Taara) on 3/3/17.
  */

object RemoteActorR2 extends App {

  val conf =
    """
      |akka {
      |  log-dead-letters = "ON"
      |
      |  actor {
      |    provider = "akka.remote.RemoteActorRefProvider"
      |  }
      |
      |  remote {
      |    enabled-transports = ["akka.remote.netty.tcp"]
      |    netty.tcp {
      |      hostname = "0.0.0.0"
      |      port = 2553
      |    }
      |  }
      |}
    """.stripMargin

  val config = ConfigFactory.parseString(conf)
  val ref = ActorSystem("remote-r2", config)

  val remoteR1 = ref.actorOf(RemoteLookupProxyForwarder.props, RemoteLookupProxyForwarder.name)

  Thread.sleep(60 * 1000)
  println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
  remoteR1 ! "Hello Dude1"
  remoteR1 ! "Hello Dude2"
  remoteR1 ! "Hello Dude3"
}
