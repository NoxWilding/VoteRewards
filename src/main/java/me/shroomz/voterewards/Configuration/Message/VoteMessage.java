package me.shroomz.voterewards.Configuration.Message;

import org.bukkit.entity.Player;

public interface VoteMessage {
    void sendAsBroadcast(Player player, MessageContext context);

    void sendAsReminder(Player player, MessageContext context);
}

