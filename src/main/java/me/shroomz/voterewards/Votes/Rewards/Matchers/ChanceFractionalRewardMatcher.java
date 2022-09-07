package me.shroomz.voterewards.Votes.Rewards.Matchers;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Votes.Vote;

import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
public class ChanceFractionalRewardMatcher implements RewardMatcher {
    static RewardMatcherFactory FACTORY = section -> {
        if (section.isInt("chance-fractional")) {
            int denominator = section.getInt("chance-fractional");
            Preconditions.checkArgument(denominator > 1, "Chance denominator is less than or equal to one.");
            return Optional.of(new ChanceFractionalRewardMatcher(denominator));
        }
        return Optional.empty();
    };

    private static final Random random = new Random();
    private final int chance;

    @Override
    public boolean matches(Vote vote, PlayerVotes pv) {
        return random.nextInt(chance) == 0;
    }
}