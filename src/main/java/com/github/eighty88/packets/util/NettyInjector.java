package com.github.eighty88.packets.util;

import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class NettyInjector implements Listener {
    private final Plugin plugin;

    public static HashMap<PlayerChannelHandler, Player> connection = new HashMap<>();

    public static HashMap<Channel, Player> players = new HashMap<>();

    private Field EntityPlayer_playerConnection;

    private Field PlayerConnection_networkManager;

    private Field NetworkManager_K;
    private Field NetworkManager_M;

    public NettyInjector(Plugin plugin){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        try {
            Class<?> entityPlayer = Reflection.getCraftClass("EntityPlayer");
            EntityPlayer_playerConnection = Reflection.getField(entityPlayer, "playerConnection");

            Class<?> playerConnection = Reflection.getCraftClass("PlayerConnection");
            PlayerConnection_networkManager = Reflection.getField(playerConnection, "networkManager");

            Class<?> networkManager = Reflection.getCraftClass("NetworkManager");
            NetworkManager_K = Reflection.getField(networkManager, "channel");
            NetworkManager_M = Reflection.getField(networkManager, "packetListener");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inject(Player player) throws Exception {
        Object entityPlayer = Reflection.getEntityPlayer(player);
        Object networkManager = getNetworkManager(entityPlayer);
        Channel channel = getChannel(networkManager);
        PlayerChannelHandler pch = new PlayerChannelHandler(player);
        if (channel.pipeline().get(PlayerChannelHandler.class) == null) {
            players.put(channel, player);
            channel.pipeline().addBefore("packet_handler", "JinroRPG", pch);
            connection.put(pch, player);
        }
    }

    public void remove(final Player player) throws Exception {
        Object entityPlayer = Reflection.getEntityPlayer(player);
        Object networkManager = getNetworkManager(entityPlayer);
        final Channel channel = getChannel(networkManager);
        players.remove(channel);
        if (channel.pipeline().get(PlayerChannelHandler.class) != null) {
            channel.pipeline().remove(PlayerChannelHandler.class);
            for (Map.Entry<PlayerChannelHandler, Player> es : connection.entrySet()) {
                if (es.getValue().equals(player)) {
                    connection.remove(es.getKey());
                }
            }
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) throws Exception {
        if (e.getPlugin().equals(plugin)) {
            for (Player player: Bukkit.getOnlinePlayers()) {
                remove(player);
            }
        }
    }

    private Object getNetworkManager(Object entityPlayer) {
        Object pc = Reflection.getFieldValue(EntityPlayer_playerConnection, entityPlayer);
        return Reflection.getFieldValue(PlayerConnection_networkManager, pc);
    }

    private Channel getChannel(Object networkManager) {
        Channel ch;
        try {
            ch = Reflection.getFieldValue(NetworkManager_K, networkManager);
        } catch (Exception e) {
            ch = Reflection.getFieldValue(NetworkManager_M, networkManager);
        }
        return ch;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws Exception {
        inject(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) throws Exception {
        remove(e.getPlayer());
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent e) throws Exception {
        if (e.getPlugin().equals(plugin)) {
            for (Player player: Bukkit.getOnlinePlayers()) {
                inject(player);
            }
        }
    }
}
