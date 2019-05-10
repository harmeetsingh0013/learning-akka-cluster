package com.knoldus.protobuf.akkacluster

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Client extends App {

    val system = ActorSystem("Akka-Cluster", ConfigFactory.load("client.conf"))

    val clientRef = system.actorOf(Props[ClientActor], "client-actor")

    clientRef ! "run"
}
