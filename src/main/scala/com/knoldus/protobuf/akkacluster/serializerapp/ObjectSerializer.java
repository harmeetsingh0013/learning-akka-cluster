package com.knoldus.protobuf.akkacluster.serializerapp;

import java.io.*;

public class ObjectSerializer {

    public static byte[] serializeThrowableUsingJavaSerializable(Throwable cause) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos)) {
            out.writeObject(cause);
            return baos.toByteArray();
        }catch (Exception ex) {
            ex.printStackTrace();
            return new byte[]{};
        }
    }

    public static Throwable deserializeThrowableUsingJavaSerializable(byte[] byteArray) {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            ObjectInput in = new ObjectInputStream(bais)) {
            return (Throwable) in.readObject();
        }catch (Exception ex) {
            ex.printStackTrace();
            return new Throwable("EXCEPTION_DESERIALIZATION_FAILS");
        }
    }
}
