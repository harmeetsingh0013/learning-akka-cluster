package com.knoldus.protobuf.akkacluster

import akka.actor.{Actor, ActorLogging, ActorPath}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import com.knoldus.protobuf.akkacluster.BackendServiceActor.Message

class ClientActor extends Actor with ActorLogging {

    val initialContacts = Set(
        ActorPath.fromString("akka.tcp://ClusterSystem@192.168.1.55:2551/system/receptionist")
    )

    val settings = ClusterClientSettings(context.system).withInitialContacts(initialContacts)

    val cluster = context.system.actorOf(ClusterClient.props(settings), "client")

    implicit val ec = context.dispatcher

    override def receive : Receive = {
        case _ =>
            log.info("I am in client actor ................. ")
            cluster ! ClusterClient.Send("/user/backend", Message("ping"), true)
    }
}
