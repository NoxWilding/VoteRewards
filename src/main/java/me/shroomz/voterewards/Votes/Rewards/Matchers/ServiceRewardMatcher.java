package me.shroomz.voterewards.Votes.Rewards.Matchers;

import com.google.common.collect.ImmutableList;
import lombok.ToString;
import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Votes.Vote;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString
public class ServiceRewardMatcher implements RewardMatcher {
    static RewardMatcherFactory FACTORY = section -> {
        if (section.isString("service")) {
            return Optional.of(new ServiceRewardMatcher(ImmutableList.of(section.getString("service"))));
        } else if (section.isList("services")) {
            return Optional.of(new ServiceRewardMatcher(section.getStringList("services")));
        }
        return Optional.empty();
    };

    private final List<String> names;

    public ServiceRewardMatcher(List<String> names) {
        this.names = new ArrayList<>(names);
        this.names.replaceAll(String::toLowerCase);
    }

    @Override
    public boolean matches(Vote vote, PlayerVotes pv) {
        return names.contains(vote.getServiceName().toLowerCase());
    }
}