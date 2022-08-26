package fr.uniteduhc.lobby.utils.other;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class LobbyLocation {
    private final World world;

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public LobbyLocation(double x, double y, double z, float yaw, float pitch) {
        this.world = Bukkit.getWorld("world");

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LobbyLocation(double x, double y, double z) {
        this.world = Bukkit.getWorld("world");

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public Location getLocation() {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
