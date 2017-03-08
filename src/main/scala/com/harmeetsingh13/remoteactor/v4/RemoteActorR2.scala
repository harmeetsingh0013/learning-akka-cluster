package com.harmeetsingh13.remoteactor.v4

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by Harmeet Singh(Taara) on 2/3/17.
  */

object RemoteActorR2 {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    val ref = ActorSystem("remote-r2", config)

    val remoteR1 = ref.actorOf(RemoteLookupProxyForwarder.props, RemoteLookupProxyForwarder.name)

    Thread.sleep(20000)
    println(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ")
    remoteR1 ! "Hello Server1"
    remoteR1 ! "Hello Server2"
    remoteR1 ! "Hello Server3"
  }

  val conf =
    """
      |akka {
      |  log-dead-letters = "OFF"
      |
      |  actor {
      |    provider = "akka.remote.RemoteActorRefProvider"
      |
      |    deployment {
      |     /echo {
      |       remote = "akka.tcp://remote-r1@0.0.0.0:2551"
      |      }
      |
      |     /forwarder/echo {
      |       remote = "akka.tcp://remote-r1@0.0.0.0:2551"
      |      }
      |    }
      |  }
      |
      |  remote {
      |    enabled-transports = ["akka.remote.netty.tcp"]
      |    netty.tcp {
      |      hostname = "0.0.0.0"
      |      port = 2552
      |    }
      |  }
      |}
    """.stripMargin
}
