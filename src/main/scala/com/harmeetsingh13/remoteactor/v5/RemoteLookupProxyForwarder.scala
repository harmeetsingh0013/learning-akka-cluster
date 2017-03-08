package com.harmeetsingh13.remoteactor.v5

import akka.actor.{Actor, ActorLogging, ActorRef, AddressFromURIString, Deploy, Props, ReceiveTimeout, Terminated}
import akka.remote.RemoteScope

import scala.concurrent.duration.{Duration, DurationLong}

/**
  * Created by Harmeet Singh(Taara) on 2/3/17.
  */
class RemoteLookupProxyForwarder extends Actor with ActorLogging {

  context.setReceiveTimeout(3 seconds)
  deployAndWatch

  def deployAndWatch: Unit = {
    val uri = "akka.tcp://remote-r1@0.0.0.0:2551"
    val remoteR1Address = AddressFromURIString(uri)

    val props = Props[RemoteActorR1].withDeploy(
      Deploy(scope = RemoteScope(remoteR1Address))
    )
    val actor = context.actorOf(props, "echo")
    context.watch(actor)
    log.info("switching to may be active state")
    context.become(maybeActive(actor))
    context.setReceiveTimeout(Duration.Undefined)
  }


  def deploying: Receive = {
    case ReceiveTimeout =>
      deployAndWatch

    case msg => log.error(s"Ignoring message $msg, remote actor is not ready yet")
  }

  def maybeActive(actor: ActorRef): Receive = {
    case Terminated(actor) =>
      log.info(s"Actor $actor terminated.")
      log.info("switching to deploying state")
      context.become(deploying)
      context.setReceiveTimeout(3 seconds)
      deployAndWatch

    case msg => actor forward msg
  }

  override def receive = deploying
}

object RemoteLookupProxyForwarder {
  def props = Props(new RemoteLookupProxyForwarder)

  def name = "forwarder"
}