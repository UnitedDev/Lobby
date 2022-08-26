package fr.uniteduhc.lobby.manager.parkour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
@Setter
public class Parkour {

    private final Player player;
    private int checkpoint;
    private long start;

    public long getCurrentTime() {
        return System.currentTimeMillis() - start;
    }

}
