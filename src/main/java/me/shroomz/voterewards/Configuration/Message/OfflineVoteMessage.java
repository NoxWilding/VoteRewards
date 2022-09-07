package me.shroomz.voterewards.Configuration.Message;

import org.bukkit.command.CommandSender;

public interface OfflineVoteMessage {
    String getBaseMessage();

    String getWithOfflinePlayer(CommandSender to, MessageContext context);
}
