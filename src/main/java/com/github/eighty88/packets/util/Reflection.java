package com.github.eighty88.packets.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {
    public static String getPackageName() {
        return "net.minecraft.server." + getCraftBukkitVersion();
    }

    public static String getCraftBukkitVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static Class<?> getCraftClass(String s) throws Exception {
        return Class.forName(getPackageName() + "." + s);
    }

    public static Object getEntityPlayer(Player p) throws Exception {
        Method getHandle = p.getClass().getMethod("getHandle");
        return getHandle.invoke(p);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object obj) {
        try {
            return (T) field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}

