package com.business.intelligence.util;


//用于持久化cookie工具类

import org.apache.http.client.CookieStore;

import java.io.*;

public class CookieStoreUtils {

    private static String storePath = "cookieStore";

    public static void storeCookie(CookieStore cookieStore, String fileName) {
        File file = new File(storePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(file, fileName)))) {
            objectOutputStream.writeObject(cookieStore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CookieStore readStore(String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(storePath, fileName)))) {
            CookieStore cookieStore = (CookieStore) inputStream.readObject();
            return cookieStore;
        } catch (Exception e) {
        }

        return null;
    }

    public static void storeObject(Object objct, String fileName) {
        File file = new File(storePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(file, fileName)))) {
            objectOutputStream.writeObject(objct);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object readObject(String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(storePath, fileName)))) {
            return inputStream.readObject();
        } catch (Exception e) {
        }
        return null;
    }
}
