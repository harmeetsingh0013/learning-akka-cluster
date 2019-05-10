package com.knoldus.protobuf.akkacluster.serializerapp

import akka.actor.ActorRef
import com.knoldus.protobuf.akkacluster.serializerapp.RegionType.RegionType

import scala.runtime.ScalaRunTime

trait ProtobufSerializable

abstract class Command
{
    //NOTE:: AD - support for http transport
    var httpActor : Option[ActorRef] = None
}

abstract class ThrowableHolder extends Throwable(null, null, true, false)
{
    var cause : Option[Throwable] = None
    var causingCommand : Option[Command] = None

    override def initCause(throwable : Throwable) : ThrowableHolder =
    {
        cause = Some(throwable)
        return this
    }

    override def getCause = cause.getOrElse(new Throwable("CAUSE_NOT_SET"))

    //NOTE:: AD - this is done since case class doesn't implement toString if it's already
    //            implemented. in our case it is implemented by Throwable. so this solves the issue
    //            but adds an assumption that all inheriting classes will be case class
    override def toString = ScalaRunTime._toString(this.asInstanceOf[Product])
}

trait Message extends ThrowableHolder with ProtobufSerializable
{
    def optionRef : Option[ActorRef]

    def stage : Stage

    def regionType : RegionType
}

object RegionType extends Enumeration
{
    type RegionType = Value

    val AWS_NORTH_VIRGINIA = Value(0, "us-east-1")
    val AWS_OHIO = Value(1, "us-east-2")
    val AWS_NORTH_CALIFORNIA = Value(2, "us-west-1")
    val AWS_OREGON = Value(3, "us-west-2")
    val AWS_CANADA = Value(4, "ca-central-1")
    val AWS_IRELAND = Value(5, "eu-west-1")
    val AWS_FRANFURKT = Value(6, "eu-central-1")
    val AWS_LONDON = Value(7, "eu-west-2")
    val AWS_SINGAPORE = Value(8, "ap-southeast-1")
    val AWS_SYDNEY = Value(9, "ap-southeast-2")
    val AWS_SEOUL = Value(10, "ap-northeast-2")
    val AWS_TOKYO = Value(11, "ap-northeast-1")
    val AWS_MUMBAI = Value(12, "ap-south-1")
    val AWS_SAO_PAOLO = Value(13, "sa-east-1")
    val LOCAL_IL_1 = Value(14, "il-1")
}

case class RewardsPoint(ref : ActorRef) extends ProtobufSerializable

case class Level(number : Int) extends ProtobufSerializable

case class Stage(level : Level) extends ProtobufSerializable

case class GameMessage(
    msg : String,
    ref : ActorRef,
    status : Option[Boolean],
    override val optionRef : Option[ActorRef],
    override val stage : Stage,
    currentLevel : Int,
    optionCurrentLevel : Option[Level],
    override val regionType : RegionType,
    levels : List[Int],
    levelsV : Vector[Int],
    stages : Seq[Stage],
    rewards : RewardsPoint
) extends Message

case class GameReply(
    msg : String,
    status : Option[Boolean],
    override val optionRef : Option[ActorRef],
    override val stage : Stage,
    currentLevel : Int,
    optionCurrentLevel : Option[Level],
    override val regionType : RegionType,
    levels : List[Int],
    levelsV : Vector[Int],
    stages : Seq[Stage],
    rewards : RewardsPoint
) extends Message

object GameProtocol
{}
