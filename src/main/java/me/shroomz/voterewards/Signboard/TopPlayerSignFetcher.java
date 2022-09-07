package me.shroomz.voterewards.Signboard;

import lombok.RequiredArgsConstructor;
import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Voterewards;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.OptionalInt;

@RequiredArgsConstructor
public class TopPlayerSignFetcher implements Runnable {
    private final List<TopPlayerSign> toUpdate;

    @Override
    public void run() {
        // Determine how many players to fetch from the leaderboard.
        OptionalInt toFetch = toUpdate.stream().mapToInt(TopPlayerSign::getPosition).max();
        if (!toFetch.isPresent()) {
            return;
        }

        // Fetch the players required.
        List<PlayerVotes> topPlayers = Voterewards.getPlugin().getVoteStorage().getTopVoters(toFetch.getAsInt(), 0);

        // We've done everything we can do asynchronously. Hand off to the synchronous update task.
        Bukkit.getScheduler().runTask(Voterewards.getPlugin(), new TopPlayerSignUpdater(toUpdate, topPlayers));
    }
}
