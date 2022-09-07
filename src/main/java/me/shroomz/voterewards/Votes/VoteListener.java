package me.shroomz.voterewards.Votes;

import com.vexsoftware.votifier.model.VotifierEvent;
import me.shroomz.voterewards.Commands.VoteCommand;
import me.shroomz.voterewards.Configuration.Message.MessageContext;
import me.shroomz.voterewards.Signboard.TopPlayerSignFetcher;
import me.shroomz.voterewards.Storage.MysqlVoteStorage;
import me.shroomz.voterewards.Storage.VoteStorage;
import me.shroomz.voterewards.Utils.BrokenNag;
import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Voterewards;
import me.shroomz.voterewards.Votes.Rewards.VoteReward;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class VoteListener implements Listener {
    @EventHandler
    public void onVote(final VotifierEvent event) {
        if (Voterewards.getPlugin().getConfiguration().isConfigurationError()) {
            Voterewards.getPlugin().getLogger().severe("Refusing to process vote because your configuration is invalid. Please check your logs.");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Voterewards.getPlugin(), () -> {
            OfflinePlayer op = Bukkit.getOfflinePlayer(event.getVote().getUsername());
            String worldName = null;
            if (op.isOnline()) {
                worldName = op.getPlayer().getWorld().getName();
            }

            VoteStorage voteStorage = Voterewards.getPlugin().getVoteStorage();
            VoteStreak voteStreak = voteStorage.getVoteStreakIfSupported(op.getUniqueId(), false);
            PlayerVotes pvCurrent = voteStorage.getVotes(op.getUniqueId());
            PlayerVotes pv = new PlayerVotes(op.getUniqueId(), op.getName(), pvCurrent.getVotes() + 1, PlayerVotes.Type.FUTURE);
            Vote vote = new Vote(op.getName(), op.getUniqueId(), event.getVote().getServiceName(),
                    event.getVote().getAddress().equals(VoteCommand.FAKE_HOST_NAME_FOR_VOTE), worldName, new Date());

            if (!vote.isFakeVote()) {
                if (Voterewards.getPlugin().getConfiguration().getStreaksConfiguration().isSharedCooldownPerService()) {
                    if (voteStreak == null) {
                        // becomes a required value
                        voteStreak = voteStorage.getVoteStreakIfSupported(op.getUniqueId(), true);
                    }
                    if (voteStreak != null && voteStreak.getServices().containsKey(vote.getServiceName())) {
                        long difference = Voterewards.getPlugin().getVoteServiceCooldown().getMax() - voteStreak.getServices().get(vote.getServiceName());
                        if (difference > 0) {
                            Voterewards.getPlugin().getLogger().log(Level.WARNING, "Ignoring vote from " + vote.getName() + " (service: " +
                                    vote.getServiceName() + ") due to [shared] service cooldown.");
                            return;
                        }
                    }
                }

                if (Voterewards.getPlugin().getVoteServiceCooldown().triggerCooldown(vote)) {
                    Voterewards.getPlugin().getLogger().log(Level.WARNING, "Ignoring vote from " + vote.getName() + " (service: " +
                            vote.getServiceName() + ") due to service cooldown.");
                    return;
                }
            }

            processVote(pv, voteStreak, vote, Voterewards.getPlugin().getConfig().getBoolean("broadcast.enabled"),
                    !op.isOnline() && Voterewards.getPlugin().getConfiguration().requirePlayersOnline(),
                    false);
        });
    }

    private void processVote(PlayerVotes pv, VoteStreak voteStreak, Vote vote, boolean broadcast, boolean queue, boolean wasQueued) {
        List<VoteReward> bestRewards = Voterewards.getPlugin().getConfiguration().getBestRewards(vote, pv);
        MessageContext context = new MessageContext(vote, pv, voteStreak, Bukkit.getOfflinePlayer(vote.getUuid()));
        boolean canBroadcast = Voterewards.getPlugin().getRecentVoteStorage().canBroadcast(vote.getUuid());
        Voterewards.getPlugin().getRecentVoteStorage().updateLastVote(vote.getUuid());

        Optional<Player> player = context.getPlayer().map(OfflinePlayer::getPlayer);
        boolean hideBroadcast = player.isPresent() && player.get().hasPermission("voterewards.bypassbroadcast");

        if (bestRewards.isEmpty()) {
            throw new RuntimeException("No vote rewards found for '" + vote + "'");
        }

        if (queue) {
            if (!Voterewards.getPlugin().getConfiguration().shouldQueueVotes()) {
                Voterewards.getPlugin().getLogger().log(Level.WARNING, "Ignoring vote from " + vote.getName() + " (service: " +
                        vote.getServiceName() + ") because they aren't online.");
                return;
            }

            Voterewards.getPlugin().getLogger().log(Level.INFO, "Queuing vote from " + vote.getName() + " to be run later");
            for (VoteReward reward : bestRewards) {
                reward.broadcastVote(context, false, broadcast && Voterewards.getPlugin().getConfig().getBoolean("broadcast.queued") && canBroadcast && !hideBroadcast);
            }
            Voterewards.getPlugin().getQueuedVotes().addVote(vote);
        } else {
            if (!vote.isFakeVote() || Voterewards.getPlugin().getConfig().getBoolean("votes.process-fake-votes")) {
                Voterewards.getPlugin().getVoteStorage().addVote(vote);
            }

            if (!wasQueued) {
                for (VoteReward reward : bestRewards) {
                    reward.broadcastVote(context, Voterewards.getPlugin().getConfig().getBoolean("broadcast.message-player"), broadcast && canBroadcast && !hideBroadcast);
                }
                Bukkit.getScheduler().runTaskAsynchronously(Voterewards.getPlugin(), this::afterVoteProcessing);
            }

            Bukkit.getScheduler().runTask(Voterewards.getPlugin(), () -> bestRewards.forEach(reward -> reward.runCommands(vote)));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Voterewards.getPlugin().getConfiguration().isConfigurationError()) {
            if (event.getPlayer().hasPermission("voterewards.admin")) {
                Player player = event.getPlayer();
                Bukkit.getScheduler().runTaskLater(Voterewards.getPlugin(), () -> BrokenNag.nag(player), 40);
            }
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Voterewards.getPlugin(), () -> {
            // Update names in MySQL, if it is being used.
            if (Voterewards.getPlugin().getVoteStorage() instanceof MysqlVoteStorage) {
                ((MysqlVoteStorage) Voterewards.getPlugin().getVoteStorage()).updateName(event.getPlayer());
            }

            // Process queued votes.
            VoteStorage voteStorage = Voterewards.getPlugin().getVoteStorage();
            UUID playerUUID = event.getPlayer().getUniqueId();
            PlayerVotes pv = voteStorage.getVotes(playerUUID);
            VoteStreak voteStreak = voteStorage.getVoteStreakIfSupported(playerUUID, false);
            List<Vote> votes = Voterewards.getPlugin().getQueuedVotes().getAndRemoveVotes(playerUUID);
            if (!votes.isEmpty()) {
                for (Vote vote : votes) {
                    processVote(pv, voteStreak, vote, false, false, true);
                    pv = new PlayerVotes(pv.getUuid(), event.getPlayer().getName(),pv.getVotes() + 1, PlayerVotes.Type.CURRENT);
                }
                afterVoteProcessing();
            }

            // Remind players to vote.
            if (Voterewards.getPlugin().getConfig().getBoolean("vote-reminder.on-join") &&
                    event.getPlayer().hasPermission("voterewards.notify") &&
                    !Voterewards.getPlugin().getVoteStorage().hasVotedToday(event.getPlayer().getUniqueId())) {
                MessageContext context = new MessageContext(null, pv, voteStreak, event.getPlayer());
                Voterewards.getPlugin().getConfiguration().getReminderMessage().sendAsReminder(event.getPlayer(), context);
            }
        });
    }

    private void afterVoteProcessing() {
        Voterewards.getPlugin().getScoreboardHandler().doPopulate();
        new TopPlayerSignFetcher(Voterewards.getPlugin().getTopPlayerSignStorage().getSignList()).run();
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("PlaceholderAPI")) {
            Voterewards.getPlugin().getLogger().info("Using clip's PlaceholderAPI to provide extra placeholders.");
        }
    }
}
