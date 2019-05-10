package com.knoldus.protobuf.akkacluster.serializerapp;

import akka.actor.ActorRef;
import akka.actor.ExtendedActorSystem;
import akka.remote.WireFormats;
import akka.remote.serialization.ProtobufSerializer;
import com.google.protobuf.ByteString;
import com.knoldus.protobuf.akkacluster.serializerapp.exception.APIServerException;
import scala.Enumeration;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scalapb.GeneratedEnum;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class JavaTransformerUtility implements ReflectionUtility {

    static final List<String> predefineIgnoredFields = Arrays.asList(
            "serialVersionUID", "cause"
    );

    private JavaTransformerUtility() {
    }

    public static Object createInstanceOfProtoClassFromClass(String className, Class<?> clazzType, Object clazzTypeData, Throwable cause) throws Exception {
        Class<?> protoClass = Class.forName(className + PROTO_SUFFIX);
        if (protoClass.getConstructors().length != 1) {
            throw new TransformerUtilityException(protoClass.getCanonicalName() + " class doesn't contains only one constructor", null);
        } else {
            return createInstanceOfProtoClassFromClass(clazzType, protoClass, clazzTypeData, null, cause);
        }
    }

    public static Object createInstanceOfClassFromProtoClass(String className, Class<?> protobufSerializableClazz, Object protobufSerializableData, ExtendedActorSystem system) throws Exception {
        Class<?> clazz = Class.forName(className.substring(0, (className.length() - PROTO_SUFFIX.length())));
        if (clazz.getConstructors().length != 1) {
            throw new TransformerUtilityException(clazz.getCanonicalName() + " class doesn't contains only one constructor", null);
        } else {
            return createInstanceOfProtoClassFromClass(protobufSerializableClazz, clazz, protobufSerializableData, system, null);
        }
    }

    private static Object createInstanceOfProtoClassFromClass(Class<?> from, Class<?> to, Object data, ExtendedActorSystem system, Throwable cause) throws Exception {
        Constructor<?> protoClassConstructor = to.getConstructors()[0];

        List<Object> fromClassData = Arrays.stream(from.getDeclaredFields())
                .filter(field -> !predefineIgnoredFields.contains(field.getName()))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .map(field -> {
                    try {
                        final Class<?> toFieldType = to.getDeclaredField(field.getName()).getType();

                        if (toFieldType == Option.class && field.getType() != Option.class) {
                            return Option.apply(extractValueFromField(field, data, toFieldType, system, cause));
                        } else if (field.getType() == Option.class && toFieldType != Option.class) {
                            Option option = (Option) extractValueFromField(field, data, toFieldType, system, cause);
                            return option.get();
                        } else {
                            return extractValueFromField(field, data, toFieldType, system, cause);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        String errorMessage = "Failed to convert " + from.getCanonicalName() + " class " + field.getName() + " filed to "
                                + to.getCanonicalName() + " class " + field.getName() + " field";
                        throw new TransformerUtilityException(errorMessage, ex);
                    }
                }).collect(Collectors.toList());

        if (from.getName().endsWith(PROTO_SUFFIX)) {
            try {
                Field field = from.getDeclaredField("cause");
                field.setAccessible(true);
                Option scalaOptionType = evaluateScalaOptionType(field, data, system, cause);
                if (scalaOptionType.isDefined()) {
                    Throwable throwable = (Throwable) scalaOptionType.get();
                    ThrowableHolder holder = (ThrowableHolder) protoClassConstructor.newInstance(fromClassData.toArray());
                    holder.initCause(throwable);
                    return holder;
                }
            } catch (Exception ex) {
//                ex.printStackTrace();
                System.out.println("cause field no exists " + from.getName());
            }
        }

        if (!from.getName().endsWith(PROTO_SUFFIX)) {
            try {
                to.getDeclaredField("cause");
                if(cause != null){
                    byte[] causeBytes = ObjectSerializer.serializeThrowableUsingJavaSerializable(cause);
                    fromClassData.add(Option.apply(new ThrowableProto(ByteString.copyFrom(causeBytes))));
                }else {
                    fromClassData.add(Option.apply(null));
                }
            } catch (Exception ex) {
//                ex.printStackTrace();
                System.out.println("cause field no exists " + from.getName());
            }
        }

        return protoClassConstructor.newInstance(fromClassData.toArray());
    }

    private static Object extractValueFromField(Field field, Object data, Class<?> toFieldType, ExtendedActorSystem system, Throwable cause) {
        field.setAccessible(true);
        Object value = extractValueFromPrimitiveField(field, data);
        if (value == null) {
            return extractValueFromObjectField(field, data, toFieldType, system, cause);
        }
        return value;
    }

    private static Object extractValueFromPrimitiveField(Field field, Object data) {
        if (field.getType() == boolean.class) {
            System.out.println("I am in boolean type : " + field.getType());
            return extractValueFromField(obj -> field.getBoolean(obj), field, data);
        } else if (field.getType() == byte.class) {
            System.out.println("I am in byte type : " + field.getType());
            return extractValueFromField(obj -> field.getByte(obj), field, data);
        } else if (field.getType() == char.class) {
            System.out.println("I am in char type : " + field.getType());
            return extractValueFromField(obj -> field.getChar(obj), field, data);
        } else if (field.getType() == short.class) {
            System.out.println("I am in short type : " + field.getType());
            return extractValueFromField(obj -> field.getShort(obj), field, data);
        } else if (field.getType() == int.class) {
            System.out.println("I am in int type : " + field.getType());
            return extractValueFromField(obj -> field.getInt(obj), field, data);
        } else if (field.getType() == long.class) {
            System.out.println("I am in long type : " + field.getType());
            return extractValueFromField(obj -> field.getLong(obj), field, data);
        } else if (field.getType() == float.class) {
            System.out.println("I am in float type : " + field.getType());
            return extractValueFromField(obj -> field.getFloat(obj), field, data);
        } else if (field.getType() == double.class) {
            System.out.println("I am in double type : " + field.getType());
            return extractValueFromField(obj -> field.getDouble(obj), field, data);
        } else if (field.getType() == String.class) {
            System.out.println("I am in String type : " + field.getType());
            return extractValueFromField(obj -> field.get(obj), field, data);
        } else {
            System.out.println("No matched value in primitive : " + field.getType());
            return null;
        }
    }

    private static Object extractValueFromObjectField(Field field, Object data, Class<?> toFieldType, ExtendedActorSystem system, Throwable cause) {
        if (field.getType() == ActorRef.class) {
            System.out.println("I am in ActorRef type : " + field.getType());
            return extractValueFromField(obj -> actorRefToByteString((ActorRef) field.get(obj)), field, data);
        } else if (field.getType() == ByteString.class) {
            System.out.println("I am in ByteString type : " + field.getType());
            return extractValueFromField(obj -> byteStringToActorRef((ByteString) field.get(obj), system), field, data);
        } else if (field.getType() == Option.class) {
            System.out.println("I am in Option type : " + field.getType());
            return evaluateScalaOptionType(field, data, system, cause);
        } else if (field.getType() == Enumeration.Value.class) {
            System.out.println("I am in Enumeration.Value type : " + field.getType());
            return extractValueFromField(obj -> field.get(obj), field, data);
        } else if (GeneratedEnum.class.isAssignableFrom(field.getType())) {
            System.out.println("I am in GeneratedEnum type : " + field.getType());
            String enumerationClassName = findEnumerationClassName(field);
            GeneratedEnum value = (GeneratedEnum) extractValueFromField(obj -> field.get(obj), field, data);
            return ScalaTransformerUtility.convertGeneratedEnumValueToEnumerationValue(enumerationClassName, value.index());
        } else if (Seq.class.isAssignableFrom(field.getType())) {
            System.out.println("I am in scala.collection.Seq type : " + field.getType());
            Seq<?> value = (Seq<?>) extractValueFromField(obj -> field.get(obj), field, data);
            Iterator<?> iterator = evaluateScalaSeqType(field, value, system, cause);
            return wrapCollectionTypeToAppropriateType(iterator, toFieldType);
        } else {
            Object value = extractValueFromField(obj -> field.get(obj), field, data);
            return resolveNestedObjects(value, system, cause);
        }
    }

    private static Object resolveNestedObjects(Object value, ExtendedActorSystem system, Throwable cause) {
        String valueClassName = value.getClass().getCanonicalName();
        try {
            if (valueClassName.endsWith(PROTO_SUFFIX)) {
                return createInstanceOfClassFromProtoClass(valueClassName, value.getClass(), value, system);
            } else {
                return createInstanceOfProtoClassFromClass(valueClassName, value.getClass(), value, cause);
            }
        } catch (Exception ex) {
            String errorMessage = "Unable to resolve nested objects of class" + valueClassName + "class.";
            throw new TransformerUtilityException(errorMessage, ex);
        }
    }

    private static Object resolveScalaEnumeration(Enumeration.Value value) {

        Class<?> classType = ScalaTransformerUtility.findEnumerationOuterType(value.getClass(), value);
        return ScalaTransformerUtility.convertEnumerationValueToGeneratedEnumValue(classType, value.id());
    }

    private static ByteString actorRefToByteString(ActorRef actorRef) {
        WireFormats.ActorRefData refData = ProtobufSerializer.serializeActorRef(actorRef);
        return ByteString.copyFrom(refData.toByteString().toByteArray());
    }

    private static Object byteStringToActorRef(ByteString byteString, ExtendedActorSystem system) {
        try {
            WireFormats.ActorRefData refData = WireFormats.ActorRefData
                    .parseFrom(akka.protobuf.ByteString.copyFrom(byteString.toByteArray()));
            return ProtobufSerializer.deserializeActorRef(system, refData);
        } catch (Exception ex) {
            String errorMessage = "Unable to parse ByteString " + byteString + " to ActorRef";
            throw new TransformerUtilityException(errorMessage, ex);
        }
    }

    private static boolean isPrimitive(String typeName) {
        return Arrays.asList(
                Boolean.class.getName(), Byte.class.getName(), Character.class.getName(),
                Short.class.getName(), Integer.class.getName(), Long.class.getName(), Float.class.getName(),
                Double.class.getName(), String.class.getName()
        ).contains(typeName);
    }

    private static Object wrapCollectionTypeToAppropriateType(Iterator<?> transformedCollection, Class<?> toFieldType) {
        if (toFieldType == scala.collection.immutable.Set.class) {
            return JavaConverters.asScalaIterator(transformedCollection).toSet();
        } else if (toFieldType == scala.collection.immutable.Vector.class) {
            return JavaConverters.asScalaIterator(transformedCollection).toVector();
        } else {
            //TODO HS Needs to decide, what will be the else part?
            return JavaConverters.asScalaIterator(transformedCollection).toList();
        }
    }

    private static Iterator<?> evaluateScalaSeqType(Field field, Seq<?> seqOfData, ExtendedActorSystem system, Throwable cause) {

        Collection<?> collection = JavaConverters.asJavaCollection(seqOfData);
        return collection.stream()
                .map(value -> resolveContainerTypeData(field, value, system, cause))
                .iterator();
    }

    private static Option evaluateScalaOptionType(Field field, Object data, ExtendedActorSystem system, Throwable cause) {

        Option fieldValue = (Option) extractValueFromField(obj -> field.get(obj), field, data);
        return fieldValue.map(optionValue -> resolveContainerTypeData(field, optionValue, system, cause));
    }

    private static Object resolveContainerTypeData(Field field, Object containerData, ExtendedActorSystem system, Throwable cause) {
        if (isPrimitive(containerData.getClass().getTypeName())) {
            return containerData;
        } else if (containerData instanceof ActorRef) {
            ActorRef actorRef = (ActorRef) containerData;
            return actorRefToByteString(actorRef);
        } else if (containerData instanceof ThrowableProto) {
            ThrowableProto throwableProto = (ThrowableProto) containerData;
            ByteString bytString = throwableProto.exception();
            return ObjectSerializer.deserializeThrowableUsingJavaSerializable(bytString.toByteArray());
        } else if (containerData instanceof ByteString) {
            ByteString bytString = (ByteString) containerData;
            return byteStringToActorRef(bytString, system);
        } else if (containerData instanceof Enumeration.Value) {
            return containerData;
        } else if (containerData instanceof GeneratedEnum) {
            String enumerationClassName = findEnumerationClassName(field);
            GeneratedEnum value = (GeneratedEnum) containerData;
            return ScalaTransformerUtility.convertGeneratedEnumValueToEnumerationValue(enumerationClassName, value.index());
        } else if (containerData instanceof APIServerException) {
            return null;
        } else {
            return resolveNestedObjects(containerData, system, cause);
        }
    }

    private static <R> R extractValueFromField(CheckedFunction<R, Object> function, Field field, Object data) {
        try {
            return function.apply(data);
        } catch (Exception ex) {
            String errorMessage = "Class " + data.getClass().getName() + " field " + field.getName() + "contains Invalid data";
            throw new InvalidFieldDataException(errorMessage, ex);
        }
    }

    private static String findEnumerationClassName(Field field) {
        String fieldTypeName = field.getType().getCanonicalName();
        return fieldTypeName.substring(0, fieldTypeName.length() - PROTO_SUFFIX.length()) + "$";
    }
}
