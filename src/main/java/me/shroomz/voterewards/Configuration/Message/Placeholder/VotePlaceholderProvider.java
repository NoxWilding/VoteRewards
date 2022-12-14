package me.shroomz.voterewards.Configuration.Message.Placeholder;

import me.shroomz.voterewards.Configuration.Message.MessageContext;
import me.shroomz.voterewards.Votes.Vote;
import me.shroomz.voterewards.Votes.VoteStreak;

public class VotePlaceholderProvider implements PlaceholderProvider {
    @Override
    public String apply(String message, MessageContext context) {
        String base = message.replace("%player%", context.getVoteRecord().getAssociatedUsername())
                .replace("%votes%", Integer.toString(context.getVoteRecord().getVotes()))
                .replace("%uuid%", context.getVoteRecord().getUuid().toString());
        if (context.getVote().isPresent()) {
            Vote vote = context.getVote().get();
            base = base.replace("%service%", vote.getServiceName());
        }
        if (context.getStreakRecord().isPresent()) {
            VoteStreak voteStreak = context.getStreakRecord().get();
            base = base.replace("%streak%", Integer.toString(voteStreak.getCount()))
                    .replace("%streak_days%", Integer.toString(voteStreak.getDays()));
            if (base.contains("%streak_today_services%")) {
                int votedToday = (int) voteStreak.getServices().values().stream()
                        .filter(timeSince -> timeSince <= 86400)
                        .count();
                base = base.replace("%streak_today_services%", Integer.toString(votedToday));
            }
        }
        return base;
    }

    @Override
    public boolean canUse() {
        return true; // Only depends on SuperbVote components.
    }
}