package fr.kohei.lobby.listeners;

import fr.kohei.BukkitAPI;
import fr.kohei.common.RedisProvider;
import fr.kohei.common.cache.ProfileData;
import fr.kohei.common.cache.Rank;
import fr.kohei.lobby.Main;
import fr.kohei.lobby.lobby.LobbyPlayer;
import fr.kohei.lobby.manager.JumpManager;
import fr.kohei.lobby.menu.ProfileMenu;
import fr.kohei.utils.ChatUtil;
import fr.kohei.utils.ScoreboardTeam;
import fr.kohei.utils.item.CustomItem;
import fr.kohei.utils.item.CustomItemEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

@RequiredArgsConstructor
public class PlayerListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());
            if (profile.getRank().getPermissionPower() < 30) {
                event.getPlayer().hidePlayer(player);
            }
            ProfileData target = BukkitAPI.getCommonAPI().getProfile(event.getPlayer().getUniqueId());
            if (target.getRank().getPermissionPower() < 30) {
                player.hidePlayer(event.getPlayer());
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player);

        lobbyPlayer.refreshHotbar();
        lobbyPlayer.teleportToSpawn();

        RedisProvider.redisProvider.getExecutor().execute(() -> {
            ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());
            Rank rank = profile.getRank();
            if (profile.getRank().permissionPower() > 0) {
                Bukkit.broadcastMessage(ChatUtil.translate("&8┃ " + rank.getChatPrefix() + " " + player.getName() + " &7&oa rejoint le lobby"));
            }

            for (ScoreboardTeam team : Main.getTeams()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(team.createTeam());
            }
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        CustomItem customItem = CustomItem.getCustomItem(event.getCurrentItem());
        if (customItem == null) return;
        if (customItem.getCallable() == null) return;
        customItem.getCallable().accept(new CustomItemEvent((Player) event.getWhoClicked(), event.getCurrentItem(), true));

        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Profil")) {
            new ProfileMenu(null).openMenu((Player) event.getWhoClicked());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        if (!event.getItem().hasItemMeta()) return;

        if (event.getItem().getItemMeta().getDisplayName().contains("Profil")) {
            new ProfileMenu(null).openMenu(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player);

        lobbyPlayer.setVisibility(true);
        lobbyPlayer.setParkour(null);

        event.setQuitMessage(null);
    }

    @EventHandler
    public void onUnLoad(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        LobbyPlayer lobbyPlayer = new LobbyPlayer(event.getPlayer());
        Player player = event.getPlayer();
        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

        if (event.getTo().getY() <= 0) {
            player.teleport(Main.getSpawn());
        }

        if (event.getPlayer().isOnGround()) {
            if (lobbyPlayer.getFly() != 3) {
                lobbyPlayer.setFly(3);
                if (profile.getRank().permissionPower() > 0 && !lobbyPlayer.isInParkour()) {
                    player.setAllowFlight(true);
                }
            }
        }

        JumpManager jump = Main.getJumpManager();

//        if (lobbyPlayer.isInParkour()) {
//            player.setAllowFlight(false);
//            LobbyPlayerCache lobbyPlayerCache = Main.getPlayerCache().get(player.getUniqueId());
//            if (player.getLocation().distance(jump.getJumpEnd()) <= 1) {
//                if (lobbyPlayerCache.getParkourTime() == -1L || lobbyPlayerCache.getParkourTime() > lobbyPlayer.getParkour().getCurrentTime()) {
//                    player.sendMessage(ChatUtil.prefix("&fVous avez &abattu &fvotre record qui était de &a" + TimeUtil.niceTime(lobbyPlayerCache.getParkourTime())));
//                    lobbyPlayerCache.setParkourTime(lobbyPlayer.getParkour().getCurrentTime());
//                    BukkitAPI.getCommonAPI().saveProfile(player.getUniqueId(), profile);
//                } else {
//                    player.sendMessage(ChatUtil.prefix("&fVous n'avez pas &cbattu &fvotre record qui était de &c" + TimeUtil.niceTime(lobbyPlayerCache.getParkourTime())));
//                }
//                lobbyPlayer.leaveParkour();
//                return;
//            }
//            for (Location checkpoint : jump.getCheckpoints()) {
//                if (player.getLocation().distance(checkpoint) <= 1) {
//                    if (lobbyPlayer.getParkour().getCheckpoint() == jump.getCheckpoints().indexOf(checkpoint)) break;
//                    lobbyPlayer.getParkour().setCheckpoint(jump.getCheckpoints().indexOf(checkpoint));
//                    player.sendMessage(ChatUtil.prefix("&fVous êtes arrivé au checkpoint numéro &a" + (lobbyPlayer.getParkour().getCheckpoint() + 1)));
//                }
//            }
//            return;
//        }

//        if (jump.getCheckpoints().get(0).distance(player.getLocation()) <= 1) {
//            lobbyPlayer.setParkour(new Parkour(player, 0, System.currentTimeMillis()));
//            player.sendMessage(ChatUtil.prefix("&aVous avez commencé le parkour."));
//            player.setAllowFlight(false);
//            lobbyPlayer.refreshHotbar();
//        }

    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if(event.getEntity() instanceof ArmorStand) return;
        if(event.getEntity() instanceof Player) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEdit(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        LobbyPlayer lPlayer = new LobbyPlayer(player);

        if (!event.isFlying()) return;

        if (lPlayer.isInParkour()) {
            player.setAllowFlight(false);
            return;
        }

        event.setCancelled(true);
        player.setVelocity(player.getLocation().getDirection().multiply(2.5).setY(1.5));

        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1F, 1F);
        Location location = player.getLocation();
        location.getWorld().playEffect(location, Effect.LARGE_SMOKE, 1);

        if (lPlayer.getFly() == 0) {
            player.setAllowFlight(false);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

}
