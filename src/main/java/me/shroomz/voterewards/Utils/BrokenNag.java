package me.shroomz.voterewards.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BrokenNag {
    public static void nag(CommandSender player) {
        if (player.hasPermission("voterewards.admin")) {
            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "ERROR ERROR ERROR ERROR ERROR ERROR ERROR");
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + "You have a configuration error.");
            player.sendMessage(ChatColor.RED + "will NOT work until the error is resolved.");
            player.sendMessage(ChatColor.RED + "Please check your server logs for more info.");
            player.sendMessage(ChatColor.RED + "Use /vr reload to reload your config.");
            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "ERROR ERROR ERROR ERROR ERROR ERROR ERROR");
            player.sendMessage("");
        } else {
            player.sendMessage(ChatColor.RED + "An internal error has occurred. Ask the server administrator for more information.");
        }
    }
}
