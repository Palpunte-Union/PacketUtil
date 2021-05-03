package com.github.eighty88.packets.util;

import com.github.eighty88.packets.PacketAPI;
import com.github.eighty88.packets.PacketUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

public class PlayerChannelHandler extends ChannelDuplexHandler {

    Player player;

    public PlayerChannelHandler(Player player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        for (PacketAPI api : PacketUtil.getInstance().apiList) {
            msg = api.receiveFrom(player, msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        for (PacketAPI api : PacketUtil.getInstance().apiList) {
            msg = api.sendTo(player, msg);
        }
        super.write(ctx, msg, promise);
    }
}
