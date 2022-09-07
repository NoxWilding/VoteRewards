package me.shroomz.voterewards.Configuration;

import lombok.Data;
import me.shroomz.voterewards.Configuration.Message.OfflineVoteMessage;

@Data
public class TextLeaderboardConfiguration {
    private final int perPage;
    private final OfflineVoteMessage header;
    private final OfflineVoteMessage entryText;
    private final OfflineVoteMessage pageNumberText;
}
