package fr.kohei.lobby.lobby;

import fr.kohei.common.cache.ProfileData;
import fr.kohei.lobby.Main;
import fr.kohei.lobby.items.LobbyItems;
import fr.kohei.BukkitAPI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class LobbyPlayer {

    private static final HashMap<UUID, Boolean> VISIBILITY = new HashMap<>();
    private static final HashMap<UUID, Integer> FLY_HASH_MAP = new HashMap<>();
    private static final HashMap<UUID, Parkour> PARKOUR_MAP = new HashMap<>();

    private final Player player;
    private final Main main = Main.getInstance();

    public void refreshHotbar() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getActivePotionEffects().clear();
        player.setGameMode(GameMode.ADVENTURE);

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

        if(profile.getRank().permissionPower() > 0) {
            player.setAllowFlight(true);
        }

        player.getInventory().setItem(0, LobbyItems.SERVER_SELECTOR.toItemStack());
        player.getInventory().setItem(1, LobbyItems.getProfileItem(player));
        if(isInParkour()) {
            player.getInventory().setItem(3, LobbyItems.LAST_CHECKPOINT.toItemStack());
            player.getInventory().setItem(4, LobbyItems.RESTART_PARKOUR.toItemStack());
            player.getInventory().setItem(5, LobbyItems.LEAVE_PARKOUR.toItemStack());
        } else {
            player.getInventory().setItem(4, LobbyItems.SHOP.toItemStack());
        }
        player.getInventory().setItem(7, LobbyItems.COSMETICS.toItemStack());
        player.getInventory().setItem(8, LobbyItems.LOBBY_SELECTOR.toItemStack());
    }

    public void leaveParkour() {
        setParkour(null);
        player.teleport(Main.getJumpManager().getJumpStart());
        refreshHotbar();

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

        if(profile.getRank().permissionPower() > 0) {
            player.setAllowFlight(true);
        }
    }

    public void setVisibility(Boolean visibility) {
        VISIBILITY.put(player.getUniqueId(), visibility);
    }

    public boolean getVisibility() {
        return VISIBILITY.getOrDefault(player.getUniqueId(), true);
    }

    public void setFly(Integer integer) {
        FLY_HASH_MAP.put(player.getUniqueId(), integer);
    }

    public Integer getFly() {
        return FLY_HASH_MAP.getOrDefault(player.getUniqueId(), 3);
    }

    public void teleportToSpawn() {
        player.teleport(Main.getSpawn());
    }

    public void updateVisibility() {
        if(getVisibility()) {
            Bukkit.getOnlinePlayers().forEach(player::showPlayer);
        } else {
            Bukkit.getOnlinePlayers().forEach(player::hidePlayer);
        }
    }

    public Parkour getParkour() {
        return PARKOUR_MAP.get(player.getUniqueId());
    }

    public boolean isInParkour() {
        return getParkour() != null;
    }

    public void setParkour(Parkour value) {
        PARKOUR_MAP.put(player.getUniqueId(), value);
    }

    public void toggleVisibility() {
        this.setVisibility(!this.getVisibility());

        this.updateVisibility();
        player.getInventory().setItem(0, LobbyItems.getVisibilityItem(player));
    }

}
