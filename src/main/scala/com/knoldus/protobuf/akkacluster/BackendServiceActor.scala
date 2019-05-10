package com.knoldus.protobuf.akkacluster

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.client.ClusterClientReceptionist
import com.knoldus.protobuf.akkacluster.serializerapp.GameMessage

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
        case gameMessage @ GameMessage("ping", _, _, _, _, _, _, _, _, _, _, _) =>
            log.info("I am received ping message..... {}", gameMessage)

            context.actorSelection("akka.tcp://ClusterSystem@192.168.1.55:9892/user/backend") ! gameMessage.copy(msg = "pong")

        case gameMessage @ GameMessage("pong", ref, _, _, _, _, _, _, _, _, _, _) =>
            log.info("I am received pong message..... {}", gameMessage)

            ref ! gameMessage.copy(msg = "dong")

        case msg => log.info("unknown message ............ {}", msg)
    }
}
