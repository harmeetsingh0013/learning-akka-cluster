package com.harmeetsingh13.remoteactor.v3

import akka.actor.{ActorSystem, AddressFromURIString, Deploy, Props}
import akka.remote.RemoteScope

/**
  * Created by harmeet on 3/3/17.
  */

object RemoteActorR3 extends App {

  val uri = "akka.tcp://remote-r1@0.0.0.0:2551"
  val remoteR1Address = AddressFromURIString(uri)

  val props = Props[RemoteActorR1].withDeploy(
    Deploy(scope = RemoteScope(remoteR1Address))
  )

  val ref = ActorSystem("remote-r3")
  val remoteR1 = ref.actorOf(props, "echo")

  remoteR1 ! "Hello Dude1"
  remoteR1 ! "Hello Dude2"
  remoteR1 ! "Hello Dude3"
}
