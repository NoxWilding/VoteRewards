package me.shroomz.voterewards.Votes.Rewards;

import lombok.Data;
import me.shroomz.voterewards.Configuration.Message.MessageContext;
import me.shroomz.voterewards.Configuration.Message.VoteMessage;
import me.shroomz.voterewards.Configuration.VoteConfiguration;
import me.shroomz.voterewards.Votes.Rewards.Matchers.RewardMatcher;
import me.shroomz.voterewards.Votes.Vote;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@Data
public class VoteReward {
    private final String serviceName;
    private final List<RewardMatcher> rewardMatchers;
    private final List<String> commands;
    private final VoteMessage playerMessage;
    private final VoteMessage broadcastMessage;
    private final boolean cascade;

    public void broadcastVote(MessageContext context, boolean playerAnnounce, boolean broadcast) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerMessage != null && Optional.of(player).equals(context.getPlayer()) && playerAnnounce) {
                playerMessage.sendAsBroadcast(player, context);
            }
            if (broadcastMessage != null && broadcast) {
                broadcastMessage.sendAsBroadcast(player, context);
            }
        }
    }

    public void runCommands(Vote vote) {
        for (String command : commands) {
            String fixed = VoteConfiguration.replaceCommandPlaceholders(command, vote);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fixed);
        }
    }
}
