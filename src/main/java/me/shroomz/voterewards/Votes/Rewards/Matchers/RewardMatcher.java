package me.shroomz.voterewards.Votes.Rewards.Matchers;

import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Votes.Vote;

public interface RewardMatcher {
    boolean matches(Vote vote, PlayerVotes pv);
}

