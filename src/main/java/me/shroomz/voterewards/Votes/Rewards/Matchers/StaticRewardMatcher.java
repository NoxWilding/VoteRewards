package me.shroomz.voterewards.Votes.Rewards.Matchers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Votes.Vote;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StaticRewardMatcher implements RewardMatcher {
    public static final StaticRewardMatcher ALWAYS_MATCH = new StaticRewardMatcher(true);
    public static final StaticRewardMatcher ERROR = new StaticRewardMatcher(false);

    public static final RewardMatcherFactory DEFAULT_FACTORY = (section) -> section.getBoolean("default") ? Optional.of(ALWAYS_MATCH) : Optional.empty();

    private final boolean val;

    @Override
    public boolean matches(Vote vote, PlayerVotes pv) {
        return val;
    }
}

