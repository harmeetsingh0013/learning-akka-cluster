package com.knoldus.protobuf.akkacluster.serializerapp

import com.knoldus.protobuf.akkacluster.serializerapp.RegionType.RegionType
import com.knoldus.protobuf.akkacluster.serializerapp.RegionTypeProto.Unrecognized
import scalapb.TypeMapper

import scala.util.Try

object EnumsScalaPBMappers {

    implicit val regionTypeMapper = TypeMapper[RegionTypeProto, RegionType](
        regionTypeProto => Try(RegionType(regionTypeProto.index)).toOption.getOrElse(RegionType.AWS_IRELAND)
    )(regionType => RegionTypeProto.fromValue(regionType.id) match {
        case Unrecognized(_) => RegionTypeProto.AWS_IRELAND
        case region => region
    })
}
