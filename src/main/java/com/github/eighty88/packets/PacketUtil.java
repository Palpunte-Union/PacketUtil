package com.github.eighty88.packets;

import com.github.eighty88.packets.util.NettyInjector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class PacketUtil extends JavaPlugin {

    private static PacketUtil instance;

    public List<PacketAPI> apiList = new ArrayList<>();

    @Override
    public void onEnable() {
        new NettyInjector(this);
        instance = this;
    }

    @Override
    public void onDisable() {
    }

    public static void registerAPI(PacketAPI api) {
        getInstance().apiList.add(api);
    }

    public static void unregisterAPI(PacketAPI api) {
        getInstance().apiList.remove(api);
    }

    public static PacketUtil getInstance() {
        return instance;
    }
}
