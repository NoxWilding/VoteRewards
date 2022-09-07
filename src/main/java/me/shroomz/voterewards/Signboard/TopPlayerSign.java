package me.shroomz.voterewards.Signboard;

import lombok.Data;
import me.shroomz.voterewards.Utils.SerializableLocation;

@Data
public class TopPlayerSign {
    private final SerializableLocation sign;
    private final int position;
}
