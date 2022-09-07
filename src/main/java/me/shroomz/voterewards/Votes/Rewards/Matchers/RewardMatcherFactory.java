package me.shroomz.voterewards.Votes.Rewards.Matchers;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public interface RewardMatcherFactory {
    Optional<RewardMatcher> create(ConfigurationSection section);
}
