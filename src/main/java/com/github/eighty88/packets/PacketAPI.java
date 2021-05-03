package com.github.eighty88.packets;

import org.bukkit.entity.Player;

public interface PacketAPI {
    Object receiveFrom(Player player, Object packet);

    Object sendTo(Player player, Object packet);
}
