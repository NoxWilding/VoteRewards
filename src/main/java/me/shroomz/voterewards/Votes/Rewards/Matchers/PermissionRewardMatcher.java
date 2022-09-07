package me.shroomz.voterewards.Votes.Rewards.Matchers;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.shroomz.voterewards.Utils.PlayerVotes;
import me.shroomz.voterewards.Votes.Vote;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
@ToString
public class PermissionRewardMatcher implements RewardMatcher {
    static RewardMatcherFactory FACTORY = section -> {
        if (section.isString("permission")) {
            return Optional.of(new PermissionRewardMatcher(section.getString("permission")));
        }
        return Optional.empty();
    };

    private final String permission;

    @Override
    public boolean matches(Vote vote, PlayerVotes pv) {
        Player player = Bukkit.getPlayer(vote.getUuid());
        return player != null && player.hasPermission(permission);
    }
}