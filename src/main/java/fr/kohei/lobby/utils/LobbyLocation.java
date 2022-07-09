package fr.kohei.lobby.utils;

import fr.kohei.lobby.Main;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Getter
public class LobbyLocation {
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public LobbyLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LobbyLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public Location getLocation() {
        return new Location(Main.getInstance().getSpawn().getWorld(), x, y, z, yaw, pitch);
    }
}
