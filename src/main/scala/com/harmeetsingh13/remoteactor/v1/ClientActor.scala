package com.harmeetsingh13.remoteactor.v1

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by harmeet on 2/3/17.
  */
class ClientActor extends Actor with ActorLogging {

  override def receive: Receive = ???
}

object ClientActor {

  def main (args: Array[String] ): Unit = {
    val config = ConfigFactory.parseString(conf)
    val client = ActorSystem("Client", config)
    client.actorOf(Props[EchoServerActor], "Client")

    val path = "akka.tcp://Server@0.0.0.0:2551/user/Server"
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
