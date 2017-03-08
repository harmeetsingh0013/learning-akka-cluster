package com.harmeetsingh13.remoteactor.v5

import akka.actor.{Actor, ActorLogging, ActorSystem}
import com.typesafe.config.ConfigFactory

/**
  * Created by Harmeet Singh(Taara) on 2/3/17.
  */
class RemoteActorR1 extends Actor with ActorLogging {

  override def receive: Receive = {
    case msg => log.info(s"Server Received $msg")
  }
}

object RemoteActorR1 {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    ActorSystem("remote-r1", config)
  }

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
      |      port = 2551
      |    }
      |  }
      |}
    """.stripMargin
}
