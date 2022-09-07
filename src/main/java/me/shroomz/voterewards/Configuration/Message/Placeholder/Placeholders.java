package me.shroomz.voterewards.Configuration.Message.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.shroomz.voterewards.Voterewards;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "voterewards";
    }

    @Override
    public @NotNull String getAuthor() {
        return "LeShroomz";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player == null) return null;

        if(params.equalsIgnoreCase("votes")){
            return Integer.toString(Voterewards.getPlugin().getVoteStorage().getVotes(player.getUniqueId()).getVotes());
        }

        return null;
    }
}
