package fr.kohei.lobby.items;

import fr.kohei.lobby.Main;
import fr.kohei.lobby.lobby.LobbyPlayer;
import fr.kohei.lobby.menu.LobbySelectorMenu;
import fr.kohei.lobby.menu.ServerSelectorMenu;
import fr.kohei.utils.Heads;
import fr.kohei.utils.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class LobbyItems {

    public static final CustomItem LOBBY_SELECTOR = new CustomItem(Material.ENDER_PORTAL_FRAME, "Changer de Lobby", click -> new LobbySelectorMenu(null, false).openMenu(click.getPlayer()));
    public static final CustomItem VISIBILITY = new CustomItem(new ItemStack(Material.INK_SACK, 1, (short) 10), "Visibilité", click -> new LobbyPlayer(click.getPlayer()).toggleVisibility());
    public static final CustomItem SERVER_SELECTOR = new CustomItem(Material.COMPASS, "Menu Principal", click -> new ServerSelectorMenu().openMenu(click.getPlayer()));
    public static final CustomItem PROFILE = new CustomItem(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()), "Mon Profil", c -> {});
    public static final CustomItem COSMETICS = new CustomItem(Heads.COAL_CHEST.toItemStack(), "Cosmétiques", click -> {
    });
    public static final CustomItem LAST_CHECKPOINT = new CustomItem(Material.FEATHER, "Dernier Checkpoint", click -> click.getPlayer().teleport(Main.getJumpManager().getCheckpoints().get(new LobbyPlayer(click.getPlayer()).getParkour().getCheckpoint())));
    public static final CustomItem RESTART_PARKOUR = new CustomItem(Material.SLIME_BALL, "Recommencer", click -> new LobbyPlayer(click.getPlayer()).leaveParkour());
    public static final CustomItem LEAVE_PARKOUR = new CustomItem(new ItemStack(Material.INK_SACK, 1, (byte) 1), "Quitter", click -> new LobbyPlayer(click.getPlayer()).leaveParkour());

    public static ItemStack getVisibilityItem(Player player) {
        ItemStack toReturn = VISIBILITY.toItemStack();

        LobbyPlayer lobbyPlayer = new LobbyPlayer(player);

        if (lobbyPlayer.getVisibility()) {
            toReturn.setDurability((short) 10);
        } else {
            toReturn.setDurability((short) 1);
        }

        return toReturn;
    }
    public static ItemStack getProfileItem(Player player) {
        ItemStack toReturn = PROFILE.toItemStack();

        SkullMeta meta = (SkullMeta) toReturn.getItemMeta();
        meta.setOwner(player.getName());
        toReturn.setItemMeta(meta);

        return toReturn;
    }

}
