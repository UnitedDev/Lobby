package fr.kohei.lobby.manager.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketNotification;
import fr.kohei.BukkitAPI;
import fr.kohei.common.cache.data.ProfileData;
import fr.kohei.common.cache.rank.Rank;
import fr.kohei.lobby.Main;
import fr.kohei.lobby.manager.player.LobbyPlayer;
import fr.kohei.lobby.manager.player.LobbyProfileData;
import fr.kohei.lobby.menu.*;
import fr.kohei.lobby.manager.packets.PlayerChatPacket;
import fr.kohei.lobby.utils.other.MathUtil;
import fr.kohei.menu.Menu;
import fr.kohei.staff.events.StaffDisableEvent;
import fr.kohei.utils.ChatUtil;
import fr.kohei.utils.ScoreboardTeam;
import fr.kohei.utils.item.CustomItem;
import fr.kohei.utils.item.CustomItemEvent;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;

@RequiredArgsConstructor
public class PlayerListeners implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player);
        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

        if (!profile.getServersData().containsKey("skin"))
            profile.getServersData().put("skin", "aucun");
        else
            Main.getInstance().getCosmeticManager().getSkinManager().onJoin(player);

        if (!profile.getServersData().containsKey("pet"))
            profile.getServersData().put("pet", "aucun");

        if (!profile.getServersData().containsKey("fly"))
            profile.getServersData().put("fly", "aucun");

        if (!profile.getServersData().containsKey("bought_skins"))
            profile.getServersData().put("bought_skins", new ArrayList<>());

        if (!profile.getServersData().containsKey("bought_pets"))
            profile.getServersData().put("bought_pets", new ArrayList<>());

        lobbyPlayer.teleportToSpawn();

        Rank rank = profile.getRank();

        for (ScoreboardTeam team : Main.getInstance().getTeams()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(team.createTeam());
        }

        if (profile.isStaff()) return;

        lobbyPlayer.refreshHotbar();
        if (profile.getRank().permissionPower() > 0) {
            Bukkit.broadcastMessage(ChatUtil.translate("&8┃ " + rank.getChatPrefix() + " " + player.getName() + " &7&oa rejoint le lobby"));
        }

        if (profile.getLink().equals("") || profile.getLink() == null) {
            player.sendMessage(ChatUtil.prefix("&cIl semblerait que vous ne soyez pas link. Pour lier votre compte Minecraft à Discord," +
                    " utilisez la commande &l/link <pseudo>&c sur notre Discord &8(&fhttps://discord.gg/kohei&8)."));
            player.sendMessage(ChatUtil.prefix("&cSi vous rencontrez un problème lors de la liaison de votre compte, contactez un administrateur ou un responsale."));
        }

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            player.sendMessage(" ");
            player.sendMessage(ChatUtil.translate("&7▎ &fBienvenue &a" + player.getName() + "&f sur &6&lKOHEI&f."));
            if (LunarClientAPI.getInstance().isRunningLunarClient(player)) {
                player.sendMessage(ChatUtil.translate("&7▎ &fVous avez été &aauthentifié avec&f le &bLunarClient&f."));
            } else {
                player.sendMessage(ChatUtil.translate("&7▎ &fNous vous recommandons l'&cutilisation&f de &bLunar Client &fpour une meilleur expérience de jeu."));
            }
            player.sendMessage(ChatUtil.translate("&7▎ &fBon jeu sur le serveur, si vous avez une question contactez un staff qui apparait dans le &d/stafflist&f."));
            player.sendMessage(" ");
        }, 5);


        BukkitAPI.getCommonAPI().saveProfile(player.getUniqueId(), profile);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(event.getPlayer().getUniqueId());

        if (event.getInventory().getName().contains("Confirmation")) return;

        if (profile.getLink().equals("") || profile.getLink() == null) {
            event.setCancelled(true);
            event.getPlayer().closeInventory();
        }
    }

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("BLOCK")) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block.getType() != Material.ENDER_PORTAL_FRAME) return;

        if (Main.getInstance().getBoxTask().getBox().distance(player.getLocation()) > 10) return;

        new BoxMenu().openMenu(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Profil")) {
            InventoryView openedInventory = player.getOpenInventory();
            Menu oldMenu = null;

            if (openedInventory.getTopInventory() != null) {
                oldMenu = new ServerSelectorMenu();
            }

            new ProfileMenu(oldMenu).openMenu((Player) event.getWhoClicked());
        }

        CustomItem customItem = CustomItem.getCustomItem(event.getCurrentItem());
        if (customItem == null) return;
        if (customItem.getCallable() == null) return;
        customItem.getCallable().accept(new CustomItemEvent((Player) event.getWhoClicked(), event.getCurrentItem(), true));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        if (!event.getItem().hasItemMeta()) return;

        if (event.getItem().getItemMeta().getDisplayName().contains("Profil")) {
            new ProfileMenu(null).openMenu(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(event.getPlayer().getUniqueId());

        if (profile.getLink().equals("") || profile.getLink() == null) return;

        BukkitAPI.getCommonAPI().getMessaging().sendPacket(new PlayerChatPacket(
                event.getPlayer().getUniqueId(),
                event.getMessage(),
                BukkitAPI.getCommonAPI().getServerCache().getLobbyServers().get(Bukkit.getPort()).isRestricted()
        ));
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

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(event.getPlayer().getUniqueId());

        if (event.getMessage().contains("acceptlink")) return;

        if (profile.getLink().equals("") || profile.getLink() == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatUtil.prefix("&cVous ne pouvez pas utiliser de commandes si vous n'êtes pas link"));
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        LobbyPlayer lobbyPlayer = new LobbyPlayer(event.getPlayer());
        Player player = event.getPlayer();
        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

        if (event.getTo().getY() <= 0) {
            player.teleport(Main.getInstance().getSpawn());
        }

        if (event.getPlayer().isOnGround()) {
            lobbyPlayer.setFly(3);
            if (profile.getRank().permissionPower() > 0 && !lobbyPlayer.isInParkour()) {
                player.setAllowFlight(true);
            }
        }

        if (profile.getLink().equals("") || profile.getLink() == null) {
            player.teleport(event.getFrom());
            Main.getInstance().sendLinkTitle(player);
        }

//        JumpManager jump = Main.getInstance().getJumpManager();
//
//        if (lobbyPlayer.isInParkour()) {
//            player.setAllowFlight(false);
//            LobbyPlayerCache lobbyPlayerCache = Main.getInstance().getPlayerCache().get(player.getUniqueId());
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
        if (event.getEntity() instanceof ArmorStand) return;
        if (event.getEntity() instanceof Player) return;

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
        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

        if (profile.isStaff()) return;

        if (!event.isFlying()) return;

        if (lPlayer.isInParkour()) {
            player.setAllowFlight(false);
            return;
        }

        LobbyProfileData lobbyProfile = new LobbyProfileData(player);
        if (lobbyProfile.getFly().equals("fly")) return;

        event.setCancelled(true);
        player.setVelocity(player.getLocation().getDirection().multiply(2).setY(1));

        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1F, 1F);
        Location location = player.getLocation();
        MathUtil.sendCircleParticle(EnumParticle.SMOKE_LARGE, location.clone().add(0, -1, 0), 0.7, 10);

        lPlayer.setFly(lPlayer.getFly() - 1);

        if (lPlayer.getFly() == 0) {
            player.setAllowFlight(false);
        }
    }

    @EventHandler
    public void onStaffDisable(StaffDisableEvent event) {
        new LobbyPlayer(event.getPlayer()).updateVisibility();
        new LobbyPlayer(event.getPlayer()).refreshHotbar();
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