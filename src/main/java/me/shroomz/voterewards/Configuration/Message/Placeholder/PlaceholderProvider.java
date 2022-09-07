package me.shroomz.voterewards.Configuration.Message.Placeholder;

import me.shroomz.voterewards.Configuration.Message.MessageContext;

public interface PlaceholderProvider {
    String apply(String message, MessageContext context);

    boolean canUse();
}