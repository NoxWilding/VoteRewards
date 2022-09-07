package me.shroomz.voterewards.Storage;

import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Votes.Vote;
import me.shroomz.voterewards.Votes.VoteStreak;

import java.util.List;
import java.util.UUID;

public interface VoteStorage {
    void addVote(Vote vote);

    default void setVotes(UUID player, int votes){
        setVotes(player, votes, System.currentTimeMillis());
    }

    void setVotes(UUID player, int votes, long ts);

    void clearVotes();

    PlayerVotes getVotes(UUID player);

    default VoteStreak getVoteStreakIfSupported(UUID player, boolean required){
        if (this instanceof ExtendedVoteStorage){
            return ((ExtendedVoteStorage) this).getVoteStreak(player, required);
        }
        return null;
    }

    List<PlayerVotes> getTopVoters(int amount, int page);

    int getPagesAvailable(int amount);
    boolean hasVotedToday(UUID player);
    List<PlayerVotes> getAllPlayersWithNoVotesToday(List<UUID> onlinePlayers);

    void save();

    void close();
}