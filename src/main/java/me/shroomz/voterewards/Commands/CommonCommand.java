package me.shroomz.voterewards.Commands;

import lombok.RequiredArgsConstructor;
import me.shroomz.voterewards.Configuration.Message.MessageContext;
import me.shroomz.voterewards.Configuration.Message.VoteMessage;
import me.shroomz.voterewards.Storage.VoteStorage;
import me.shroomz.voterewards.Utils.BrokenNag;
import me.shroomz.voterewards.Voterewards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CommonCommand implements CommandExecutor {
    private final VoteMessage message;
    private final boolean streakRelated;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (Voterewards.getPlugin().getConfiguration().isConfigurationError()) {
            BrokenNag.nag(player);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Voterewards.getPlugin(), () -> {
            VoteStorage voteStorage = Voterewards.getPlugin().getVoteStorage();
            MessageContext ctx = new MessageContext(null, voteStorage.getVotes(player.getUniqueId()), voteStorage.getVoteStreakIfSupported(player.getUniqueId(), streakRelated), player);
            message.sendAsReminder(player, ctx);
        });
        return true;
    }
}

