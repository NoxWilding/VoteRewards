package me.shroomz.voterewards.Migrate;

public interface Migration {
    String getName();

    void execute(ProgressListener listener);
}

