package com.harmeetsingh13.remoteactor.v1

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by harmeet on 2/3/17.
  */
class EchoServerActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case msg => log.info(s"Server Received $msg")
  }
}

object EchoServerActor {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.parseString(conf)
    val server = ActorSystem("Server", config)
    server.actorOf(Props[EchoServerActor], "Server")
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
      |      port = 2551
      |    }
      |  }
      |}
    """.stripMargin
}
