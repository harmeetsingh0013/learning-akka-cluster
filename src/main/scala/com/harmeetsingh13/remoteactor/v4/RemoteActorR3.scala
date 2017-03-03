package com.harmeetsingh13.remoteactor.v4

import akka.actor.{ActorSystem, AddressFromURIString, Deploy, Props}
import akka.remote.RemoteScope
import com.typesafe.config.ConfigFactory

/**
  * Created by harmeet on 3/3/17.
  */

object RemoteActorR3 extends App {

  val uri = "akka.tcp://remote-r1@0.0.0.0:2551"
  val remoteR1Address = AddressFromURIString(uri)

  val props = Props[RemoteLookupProxyForwarder].withDeploy(
    Deploy(scope = RemoteScope(remoteR1Address))
  )

  val conf =
    """
      |akka {
      |  actor {
      |    provider = "akka.remote.RemoteActorRefProvider"
      |
      |    deployment {
      |      /echo {
      |       remote = "akka.tcp://remote-r1@0.0.0.0:2551"
      |      }
      |    }
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
  val ref = ActorSystem("remote-r3", config)
  val remoteR1 = ref.actorOf(props, "echo")

  Thread.sleep(30000)
  println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
  remoteR1 ! "Hello Dude1"
  remoteR1 ! "Hello Dude2"
  remoteR1 ! "Hello Dude3"


}
