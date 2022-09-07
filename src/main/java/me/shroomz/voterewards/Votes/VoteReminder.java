package me.shroomz.voterewards.Votes;

import me.shroomz.voterewards.Configuration.Message.MessageContext;
import me.shroomz.voterewards.Storage.ExtendedVoteStorage;
import me.shroomz.voterewards.Storage.VoteStorage;
import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Voterewards;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class VoteReminder implements Runnable{

    @Override
    public void run() {
        List<UUID> onlinePlayers = Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("voterewards.notify")).map(Player::getUniqueId).collect(Collectors.toList());

        VoteStorage voteStorage = Voterewards.getPlugin().getVoteStorage();
        if (Voterewards.getPlugin().getConfiguration().getStreaksConfiguration().isPlaceholdersEnabled() && voteStorage instanceof ExtendedVoteStorage) {
            List<Map.Entry<PlayerVotes, VoteStreak>> noVotes = ((ExtendedVoteStorage) voteStorage).getAllPlayersAndStreaksWithNoVotesToday(onlinePlayers);
            for (Map.Entry<PlayerVotes, VoteStreak> entry : noVotes){
                PlayerVotes pv = entry.getKey();
                VoteStreak voteStreak = entry.getValue();

                Player player = Bukkit.getPlayer(pv.getUuid());
                if (player != null) {
                    MessageContext context = new MessageContext(null, pv, voteStreak, player);
                    Voterewards.getPlugin().getConfiguration().getReminderMessage().sendAsReminder(player, context);
                }
            }
        } else {
            List<PlayerVotes> noVotes = voteStorage.getAllPlayersWithNoVotesToday(onlinePlayers);
            for (PlayerVotes pv : noVotes){
                Player player = Bukkit.getPlayer(pv.getUuid());
                if(player != null) {
                    MessageContext context = new MessageContext(null, pv, null, player);
                    Voterewards.getPlugin().getConfiguration().getReminderMessage().sendAsReminder(player, context);
                }
            }
        }
    }
}
