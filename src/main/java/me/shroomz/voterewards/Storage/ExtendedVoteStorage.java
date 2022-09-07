package me.shroomz.voterewards.Storage;

import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Votes.VoteStreak;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExtendedVoteStorage extends VoteStorage{
    VoteStreak getVoteStreak(UUID player, boolean required);

    List<Map.Entry<PlayerVotes, VoteStreak>> getAllPlayersAndStreaksWithNoVotesToday(List<UUID> onlinePlayers);
}
