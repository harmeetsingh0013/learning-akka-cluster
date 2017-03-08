package com.harmeetsingh13.remoteactor.v1

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by Harmeet Singh(Taara) on 2/3/17.
  */
class RemoteActorR2 extends Actor with ActorLogging {

  override def receive: Receive = ???
}

object RemoteActorR2 {

  def main (args: Array[String] ): Unit = {
    val config = ConfigFactory.parseString(conf)
    val client = ActorSystem("remote-r2", config)
    client.actorOf(Props[RemoteActorR1], "echo")

    val path = "akka.tcp://remote-r1@0.0.0.0:2551/user/echo"
    val simple = client.actorSelection(path)

    simple ! "Hello Server"

  }

  val conf =
    """
      |akka {
      |  actor {
      |    provider = "akka.remote.RemoteActorRefProvider"
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
