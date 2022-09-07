package me.shroomz.voterewards.Votes.Rewards.Matchers;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Votes.Vote;

import java.util.Optional;

@RequiredArgsConstructor
public class CumulativeVotesEveryRewardMatcher implements RewardMatcher {
    static RewardMatcherFactory FACTORY = section -> {
        if (section.isInt("every-cumulative-votes")) {
            int votes = section.getInt("every-cumulative-votes");
            Preconditions.checkArgument(votes >= 1, "every-cumulative-votes number must be greater than or equal to 1.");
            return Optional.of(new CumulativeVotesEveryRewardMatcher(votes));
        }
        return Optional.empty();
    };

    private final int everyVotes;

    @Override
    public boolean matches(Vote vote, PlayerVotes pv) {
        int cur = pv.getType() == PlayerVotes.Type.FUTURE ? pv.getVotes() : pv.getVotes() + 1;
        return cur % everyVotes == 0;
    }
}