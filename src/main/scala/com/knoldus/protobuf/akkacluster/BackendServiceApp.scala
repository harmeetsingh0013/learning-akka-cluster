package com.knoldus.protobuf.akkacluster

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object BackendServiceApp extends App {

    val port = if(!args.isEmpty) args(0) else "2551"
    val configuration = ConfigFactory.parseString("akka.cluster.roles = [backend]")
        .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port"))
        .withFallback(ConfigFactory.load(s"node.conf"))

    val system = ActorSystem("ClusterSystem", configuration)
    val backend = system.actorOf(Props[BackendServiceActor], "backend")
}
