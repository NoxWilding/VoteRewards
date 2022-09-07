package me.shroomz.voterewards.Configuration;

import lombok.Value;
import me.shroomz.voterewards.Configuration.Message.PlainStringMessage;

import java.util.List;

@Value
public class TopPlayerSignsConfiguration {
    private final List<PlainStringMessage> signText;
}
