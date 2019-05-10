package com.knoldus.protobuf.akkacluster.serializerapp;

@FunctionalInterface
public interface CheckedFunction<R, T> {
    R apply(T t) throws Exception;
}
