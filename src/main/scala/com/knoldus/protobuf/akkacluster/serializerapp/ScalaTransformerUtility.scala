package com.knoldus.protobuf.akkacluster.serializerapp

import scalapb.{GeneratedEnumCompanion, GeneratedMessageCompanion}

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

object ScalaTransformerUtility
{
    private val runtimeMirror: Mirror = universe.runtimeMirror(getClass.getClassLoader)

    def invokeToByteArrayMethod(clazz : Class[_], obj: AnyRef) : Array[Byte] = {
        println(" >>>>>>>>>>>>>>>>> In invokeToByteArrayMethod Method <<<<<<<<<<<<<<<<<<<<")

        val instanceMirror : universe.InstanceMirror = runtimeMirror.reflect(obj)
        val methodSymbol : universe.MethodSymbol = instanceMirror.symbol.typeSignature.member(TermName("toByteArray")).asMethod
        instanceMirror.reflectMethod(methodSymbol).apply().asInstanceOf[Array[Byte]]
    }

    def invokeParseFromMethod(clazz : Class[_], bytes : Array[Byte]) : AnyRef = {
        println(" >>>>>>>>>>>>>>>>> In invokeParseFromMethod Method <<<<<<<<<<<<<<<<<<<<")

        val protoClass = findProtoClassCanonicalName(clazz)
        val obj = loadCompanionObject(protoClass)
        val generatedMessageCompanion = obj.instance.asInstanceOf[GeneratedMessageCompanion[_]]
        generatedMessageCompanion.parseFrom(bytes).asInstanceOf[AnyRef]
    }

    def convertEnumerationValueToGeneratedEnumValue(clazz : Class[_], id : Int) : AnyRef = {
        println(" >>>>>>>>>>>>>>>>> In convertEnumerationValueToGeneratedEnumValue Method <<<<<<<<<<<<<<<<<<<<")

        val protoClass = findProtoClassCanonicalName(clazz)
        val obj : universe.ModuleMirror = loadCompanionObject(protoClass)
        val generatedEnumCompanion = obj.instance.asInstanceOf[GeneratedEnumCompanion[_]]
        generatedEnumCompanion.fromValue(id).asInstanceOf[AnyRef]
    }

    def convertGeneratedEnumValueToEnumerationValue(enumerationClassName : String, index: Int): Enumeration#Value = {
        println(" >>>>>>>>>>>>>>>>> In convertGeneratedEnumValueToEnumerationValue Method <<<<<<<<<<<<<<<<<<<<")

        val moduleMirror : universe.ModuleMirror = loadCompanionObject(enumerationClassName)
        val enumeration = moduleMirror.instance.asInstanceOf[Enumeration]
        enumeration.apply(index)
    }

    def findEnumerationOuterType(clazz : Class[_], enumeration: Enumeration#Value) : Class[_ <: AnyRef] = {
        println(" >>>>>>>>>>>>>>>>> In findEnumerationOuterType Method <<<<<<<<<<<<<<<<<<<<")

        clazz.getMethod("scala$Enumeration$Val$$$outer").invoke(enumeration).getClass
    }

    private def findProtoClassCanonicalName(clazz : Class[_]) : String = {
        val clazzName = clazz.getCanonicalName
        val tempClazzName = if(clazzName.endsWith("$")) clazzName.substring(0, clazzName.length - 1) else clazzName
        tempClazzName + ReflectionUtility.PROTO_SUFFIX
    }

    private def loadCompanionObject(companionName : String) : universe.ModuleMirror = {
        val module = runtimeMirror.staticModule(companionName)
        runtimeMirror.reflectModule(module)
    }
}
