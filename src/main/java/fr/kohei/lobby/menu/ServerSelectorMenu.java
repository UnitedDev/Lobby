package fr.kohei.lobby.menu;

import fr.kohei.lobby.Main;
import fr.kohei.BukkitAPI;
import fr.kohei.manager.server.CTFServer;
import fr.kohei.manager.server.UHCServer;
import fr.kohei.menu.Button;
import fr.kohei.menu.Menu;
import fr.kohei.menu.buttons.DisplayButton;
import fr.kohei.utils.Heads;
import fr.kohei.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSelectorMenu extends Menu {

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i : new int[]{3, 5, 9, 17, 36, 44}) {
            buttons.put(i, new DisplayButton(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(1).setName(" ").toItemStack()));
        }

        for (int i : new int[]{0, 8, 45, 53}) {
            buttons.put(i, new DisplayButton(new ItemBuilder(Heads.LUCKY.toItemStack()).setName(" ").toItemStack()));
        }

        for (int i : new int[]{1, 2, 6, 7, 10, 16, 18, 26, 27, 37, 35, 52, 51, 47, 46, 43}) {
            buttons.put(i, new DisplayButton(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(14).setName(" ").toItemStack()));
        }

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.SKULL_ITEM).setDurability(SkullType.PLAYER.ordinal()).setSkullOwner(player.getName())
                        .setName("&cMon Profil").setLore(
                                "&fPermet d'accéder à mon profil pour modifier",
                                "&fvos options.",
                                "",
                                "&f&l» &cCliquez-ici pour y accéder"
                        ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new ProfileMenu(new ServerSelectorMenu()).openMenu(player);
            }
        });

        buttons.put(49, new TeleportToSpawnButton());
        buttons.put(50, new AllGamemodeButton());
        buttons.put(48, new ParkourButton());


        buttons.put(20, new UHCGamemodeButton(UHCServer.ServerType.MHA));
        buttons.put(21, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&cSoon...").toItemStack()));
        buttons.put(29, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&cSoon...").toItemStack()));
        buttons.put(30, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&cSoon...").toItemStack()));

        buttons.put(23, new CTFButton());
        buttons.put(24, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&cSoon...").toItemStack()));
        buttons.put(32, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&cSoon...").toItemStack()));
        buttons.put(33, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&cSoon...").toItemStack()));

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Menu Principal";
    }

    private static class TeleportToSpawnButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Heads.LANTERN.toItemStack()).setName("&cRetourner au Centre").setLore(
                            "&fCliquez-ici pour vous téléporter au centre",
                            "&fdu lobby",
                            "",
                            "&f&l» &cCliquez-ici pour vous téléporter")
                    .toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.teleport(Main.getSpawn());
            player.closeInventory();
        }
    }

    private static class ParkourButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.FEATHER).setName("&cCommencer le Jump").setLore(
                            "&fCliquez-ici pour vous téléporter au point",
                            "&fde commencement du jump",
                            "",
                            "&f&l» &cCliquez-ici pour vous téléporter")
                    .toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.teleport(Main.getJumpManager().getJumpStart());
            player.closeInventory();
        }
    }

    @RequiredArgsConstructor
    private static class AllGamemodeButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            int i = 0;
            for (UHCServer uhcServer : BukkitAPI.getServerCache().getUhcServers().values()) {
                i += uhcServer.getPlayers();
            }
            return new ItemBuilder(Heads.CHEST_MINECART.toItemStack()).setName("&cServeurs Publiques").setLore(
                    "&fPermet d'afficher tous les serveurs à",
                    "&fla demande",
                    "",
                    "&8┃ &7Connectés: &f" + i,
                    "",
                    "&f&l» &cCliquez-ici pour y accéder"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new AllServersSelectorMenu(new ServerSelectorMenu()).openMenu(player);
        }
    }

    @RequiredArgsConstructor
    private static class UHCGamemodeButton extends Button {
        private final UHCServer.ServerType game;

        @Override
        public ItemStack getButtonItem(Player player) {
            int i = 0;
            for (UHCServer uhcServer : BukkitAPI.getServerCache().getUhcServers().values()) {
                if (uhcServer.getType() == game) i += uhcServer.getPlayers();
            }

            List<String> lore = new ArrayList<>();
            lore.add("&fPermet d'accéder à la liste de serveurs");
            lore.add("&fdu mode de jeu &c" + game.getName());
            if(game == UHCServer.ServerType.MHA) {
                lore.add("");
                lore.add("&8┃ &7Créateur: &fAlexQLF");
                lore.add("&8┃ &7Développeur: &fRhodless");
            }
            lore.add("");
            lore.add("&8┃ &7Connectés: &f" + i);
            lore.add("");
            lore.add("&f&l» &cCliquez-ici pour y accéder");

            return new ItemBuilder(game.getDisplay()).setName("&c" + game.getName()).setLore(lore).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new UHCGamemodeMenu(new ServerSelectorMenu(), game).openMenu(player);
        }
    }

    @RequiredArgsConstructor
    private static class CTFButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            int i = 0;
            for (CTFServer ctfServer : BukkitAPI.getServerCache().getCtfServers().values()) {
                i += ctfServer.getPlayers();
            }

            i += BukkitAPI.getTotalPlayers() - BukkitAPI.getNormalPlayers();

            return new ItemBuilder(Heads.SASUKE.toItemStack()).setName("&cNaruto-CTF").setLore(
                    "&fPermet d'accéder à la liste de serveurs",
                    "&fdu mode de jeu &cNaruto-CTF",
                    "",
                    "&8┃ &7Connectés: &f" + i,
                    "",
                    "&f&l» &cCliquez-ici pour y accéder"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new CTFMenu(new ServerSelectorMenu(), false).openMenu(player);
        }
    }
}
