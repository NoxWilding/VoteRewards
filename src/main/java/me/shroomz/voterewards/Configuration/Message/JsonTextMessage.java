package me.shroomz.voterewards.Configuration.Message;

import me.shroomz.voterewards.Voterewards;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JsonTextMessage extends MessageBase implements VoteMessage {
    private final String message;

    public JsonTextMessage(String message) {
        this.message = message;
    }

    @Override
    public void sendAsBroadcast(Player player, MessageContext context) {
        String jsonString = replace(message, context);
        if (Bukkit.isPrimaryThread()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + jsonString);
        } else {
            Bukkit.getScheduler().runTask(Voterewards.getPlugin(), () -> sendAsBroadcast(player, context));
        }
    }

    @Override
    public void sendAsReminder(Player player, MessageContext context) {
        String jsonString = replace(message, context);
        if (Bukkit.isPrimaryThread()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + jsonString);
        } else {
            Bukkit.getScheduler().runTask(Voterewards.getPlugin(), () -> sendAsReminder(player, context));
        }
    }
}
