package fr.uniteduhc.lobby.manager.player;

import fr.uniteduhc.common.cache.data.ProfileData;
import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.lobby.manager.items.LobbyItems;
import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.lobby.manager.parkour.Parkour;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        player.setGameMode(GameMode.SURVIVAL);

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

        if(profile.getRank().permissionPower() > 0) {
            player.setAllowFlight(true);
            LobbyProfileData lobbyProfileData = new LobbyProfileData(player);
            if(lobbyProfileData.getFly().equals("aucun")) {
                lobbyProfileData.setFly("dj");
            }
        } else {
            player.setAllowFlight(false);
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

        if (profile.getBox() <= 0) return;

        ItemStack box = LobbyItems.BOX.toItemStack();
        box.setAmount(profile.getBox());
        player.getInventory().setItem(2, box);
    }

    public void leaveParkour() {
        setParkour(null);
        player.teleport(Main.getInstance().getJumpManager().getJumpStart());
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
        player.teleport(Main.getInstance().getSpawn());
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
