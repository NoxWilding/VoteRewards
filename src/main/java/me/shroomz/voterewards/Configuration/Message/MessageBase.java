package me.shroomz.voterewards.Configuration.Message;

import com.google.common.collect.ImmutableList;
import me.shroomz.voterewards.Configuration.Message.Placeholder.ClipsPlaceholderProvider;
import me.shroomz.voterewards.Configuration.Message.Placeholder.PlaceholderProvider;
import me.shroomz.voterewards.Configuration.Message.Placeholder.VotePlaceholderProvider;

import java.util.List;

class MessageBase {
    private static final List<PlaceholderProvider> PROVIDER_LIST = ImmutableList.of(new VotePlaceholderProvider(),
            new ClipsPlaceholderProvider());

    String replace(String message, MessageContext context) {
        String replaced = message;
        for (PlaceholderProvider provider : PROVIDER_LIST) {
            if (provider.canUse()) {
                replaced = provider.apply(replaced, context);
            }
        }
        return replaced;
    }
}
