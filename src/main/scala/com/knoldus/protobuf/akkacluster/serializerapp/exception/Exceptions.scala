package com.knoldus.protobuf.akkacluster.serializerapp.exception

import akka.AkkaException

class APIServerException(error : ErrorCodes.ErrorCodes, info : Option[String], cause : Option[Throwable]) extends AkkaException(error.toString, cause.getOrElse(null))
{
    //NOTE:: AD - log is done at exception constructor so that Takipi could capture the parameters at the moment of error
    if(error == ErrorCodes.INNER_API_SERVER_ERROR)
    {
        println(s"APIServerException Generated - $toString")
    }

    def getError = error

    override def clone = new APIServerException(error, info, cause)

    override def toString = s"${getClass.getName}($error,$info,$cause)"
}

case class ModelManagerException(error : ErrorCodes.ErrorCodes = ErrorCodes.INNER_API_SERVER_ERROR, info : String = null, cause : Throwable = null) extends APIServerException(error, Option(info), Option(cause))