package me.shroomz.voterewards;

import lombok.Getter;
import me.shroomz.voterewards.Configuration.VoteConfiguration;
import me.shroomz.voterewards.Storage.QueuedVotesStorage;
import me.shroomz.voterewards.Storage.RecentVoteStorage;
import me.shroomz.voterewards.Storage.VoteStorage;
import me.shroomz.voterewards.Utils.BrokenNag;
import me.shroomz.voterewards.Utils.Cooldowns.VoteServiceCooldown;
import me.shroomz.voterewards.Votes.VoteReminder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;

public final class Voterewards extends JavaPlugin {

    @Getter
    private static Voterewards plugin;
    @Getter
    private VoteStorage voteStorage;
    @Getter
    private QueuedVotesStorage queuedVotes;
    @Getter
    private RecentVoteStorage recentVoteStorage;
    @Getter
    private VoteServiceCooldown voteServiceCooldown;
    @Getter
    private VoteConfiguration configuration;
    private BukkitTask voteReminderTask;


    @Override
    public void onEnable() {
        // Plugin startup logic

        dependencyCheck();

        plugin = this;
        saveDefaultConfig();
        configuration = new VoteConfiguration(getConfig());

        if (configuration.isConfigurationError()) {
            BrokenNag.nag(getServer().getConsoleSender());
        }

        try {
            voteStorage = configuration.initializeVoteStorage();
        } catch (Exception e) {
            throw new RuntimeException("Exception whilst initializing vote storage", e);
        }

        try {
            queuedVotes = new QueuedVotesStorage(new File(getDataFolder(), "queued_votes.json"));
        } catch (IOException e) {
            throw new RuntimeException("Exception whilst initializing queued vote storage", e);
        }

        recentVoteStorage = new RecentVoteStorage();
        voteServiceCooldown = new VoteServiceCooldown(getConfig().getInt("votes.cooldown-per-service", 3600));

        int r = getConfig().getInt("vote-reminder.repeat");
        String text = getConfig().getString("vote-reminder.message");
        if (text != null && !text.isEmpty()) {
            if (r > 0) {
                voteReminderTask = getServer().getScheduler().runTaskTimerAsynchronously(this, new VoteReminder(), 20 * r, 20 * r);
            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        if (voteReminderTask != null) {
            voteReminderTask.cancel();
            voteReminderTask = null;
        }
        voteStorage.save();
        queuedVotes.save();
        voteStorage.close();
    }

    public void dependencyCheck(){
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            getLogger().info("PlaceholderAPI detected, providing extra placeholders.");
        }
        if(getServer().getPluginManager().getPlugin("Vault") != null){
            getLogger().info("Vault detected.");
        }
    }

    public void reloadPlugin() {
        reloadConfig();
        configuration = new VoteConfiguration(getConfig());
        voteServiceCooldown = new VoteServiceCooldown(getConfig().getInt("votes.cooldown-per-service", 3600));
        getCommand("vote").setExecutor(configuration.getVoteCommand());
        getCommand("votestreak").setExecutor(configuration.getVoteStreakCommand());

        if (voteReminderTask != null) {
            voteReminderTask.cancel();
            voteReminderTask = null;
        }
        int r = getConfig().getInt("vote-reminder.repeat");
        String text = getConfig().getString("vote-reminder.message");
        if (text != null && !text.isEmpty() && r > 0) {
            voteReminderTask = getServer().getScheduler().runTaskTimerAsynchronously(this, new VoteReminder(), 20 * r, 20 * r);
        }
    }

    public ClassLoader _exposeClassLoader() {
        return getClassLoader();
    }
}
