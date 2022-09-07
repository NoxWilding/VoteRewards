package me.shroomz.voterewards.Storage;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.shroomz.voterewards.Voterewards;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RecentVoteStorage {

    private final LoadingCache<UUID, UUID> lastVotes = CacheBuilder.newBuilder()
            .expireAfterWrite(Voterewards.getPlugin().getConfig().getInt("broadcast.antispam.time", 120), TimeUnit.SECONDS)
            .build(new CacheLoader<UUID, UUID>() {
                @Override
                public UUID load(UUID uuid) {
                    return uuid;
                }
            });

    public boolean canBroadcast(UUID uuid){
        if(!Voterewards.getPlugin().getConfig().getBoolean("broadcast.antistapm.enabled")) return true;
        return lastVotes.getIfPresent(uuid) != null;
    }

    public void updateLastVote(UUID uuid){
        lastVotes.put(uuid, uuid);
    }
}
