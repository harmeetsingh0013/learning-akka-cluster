package com.harmeetsingh13.remoteactor.v3

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
    val client = ActorSystem("client", config)

    val simple = client.actorOf(Props(new EchoServerActor), "echoserver")

    simple ! "Hello Server1"
    simple ! "Hello Server2"
    simple ! "Hello Server3"
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
      |
      |  deployment {
      |     /echoserver {
      |       remote = "akka.tcp://server@0.0.0.0:2551"
      |     }
      |  }
      |}
    """.stripMargin
}
