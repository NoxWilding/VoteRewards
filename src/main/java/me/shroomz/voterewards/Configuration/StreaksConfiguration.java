package me.shroomz.voterewards.Configuration;

import lombok.Data;

@Data
public class StreaksConfiguration {
    private final boolean enabled, placeholdersEnabled, sharedCooldownPerService;
}
