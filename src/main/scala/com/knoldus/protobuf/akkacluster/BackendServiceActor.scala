package com.knoldus.protobuf.akkacluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.client.ClusterClientReceptionist
import com.knoldus.protobuf.akkacluster.BackendServiceActor.Message

class BackendServiceActor extends Actor with ActorLogging {

    val cluster = Cluster(context.system)

    override def preStart(): Unit = {
        ClusterClientReceptionist(context.system).registerService(self)
        cluster.subscribe(self, classOf[MemberUp])
    }
    override def postStop(): Unit = {
        cluster.unsubscribe(self)
        ClusterClientReceptionist(context.system).unregisterService(self)
    }

    override def receive : Receive = {
        case Message("ping") =>
            log.info("I am received ping message.....")
            context.actorSelection("akka.tcp://ClusterSystem@192.168.1.55:9892/user/backend") ! Message("pong")
        case Message("pong") =>
            log.info("I am received pong message.....")
        case msg => log.info("unknown message ............ {}", msg)
    }
}

object BackendServiceActor {
    case class Message(message : String)
}
