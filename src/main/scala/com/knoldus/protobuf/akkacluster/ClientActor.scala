package com.knoldus.protobuf.akkacluster

import akka.actor.{Actor, ActorLogging, ActorPath}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import com.knoldus.protobuf.akkacluster.serializerapp._

class ClientActor extends Actor with ActorLogging {

    val initialContacts = Set(
        ActorPath.fromString("akka.tcp://ClusterSystem@192.168.1.55:2551/system/receptionist")
    )

    val settings = ClusterClientSettings(context.system).withInitialContacts(initialContacts)

    val cluster = context.system.actorOf(ClusterClient.props(settings), "client")

    implicit val ec = context.dispatcher

    override def receive : Receive = {
        case _ : String =>
            log.info("I am in client actor ................. ")
            val gameMessage = GameMessage(
                msg = "ping",
                ref = self,
                status = Some(false),
                optionRef = Some(self),
                stage = Stage(Level(5)),
                currentLevel = 5,
                optionCurrentLevel = Some(Level(5)),
                regionType = RegionType.AWS_MUMBAI,
                levels = List(11, 22, 33, 44, 55),
                levelsV = Vector(58, 65, 15, 32, 65),
                stages = Seq(Stage(Level(258)), Stage(Level(369)), Stage(Level(147))),
                RewardsPoint(self)
            )

            log.info("***************Start*****************")
            log.info("\n\n\n")
            log.info("{}", System.currentTimeMillis())
            log.info("\n\n\n")
            log.info("***********************************")

            cluster ! ClusterClient.Send("/user/backend", gameMessage, true)

        case gameMessage @ GameMessage("dong", _, _, _, _, _, _, _, _, _, _, _) =>

            log.info("***************End*****************")
            log.info("\n\n\n")
            log.info("{}", System.currentTimeMillis())
            log.info("\n\n\n")
            log.info("***********************************")

        case msg => log.error("unknow message ......... {}", msg)
    }
}
