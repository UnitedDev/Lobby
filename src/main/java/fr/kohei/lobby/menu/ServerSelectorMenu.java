package fr.kohei.lobby.menu;

import fr.kohei.BukkitAPI;
import fr.kohei.common.cache.server.impl.CTFServer;
import fr.kohei.common.cache.server.impl.UHCServer;
import fr.kohei.lobby.Main;
import fr.kohei.menu.Button;
import fr.kohei.menu.GlassMenu;
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

public class ServerSelectorMenu extends GlassMenu {

    @Override
    public int getGlassColor() {
        return 5;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.SKULL_ITEM).setDurability(SkullType.PLAYER.ordinal()).setSkullOwner(player.getName())
                        .setName("&6&lMon Profil").setLore(
                                "&fPermet d'accéder à mon profil pour modifier",
                                "&fvos options.",
                                "",
                                "&f&l» &eCliquez-ici pour y accéder"
                        ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new ProfileMenu(new ServerSelectorMenu()).openMenu(player);
            }
        });
        buttons.put(37, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName("&6&lChanger de Lobby").setLore(
                        "&fPermet d'accéder à la liste des lobbies.",
                        "",
                        "&f&l» &eCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new ProfileMenu(new ServerSelectorMenu()).openMenu(player);
            }
        });
        buttons.put(16, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Heads.SHOP.toItemStack()).setName("&6&lBoutique").setLore(
                        "&fPermet d'accéder à tout la boutique",
                        "&fdu serveur.",
                        "",
                        "&f&l» &eCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new ShopMenu(new ServerSelectorMenu()).openMenu(player);
            }
        });
        buttons.put(43, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Heads.LUCKY.toItemStack()).setName("&6&lBesoin d'Aide").setLore(
                        "&fSi vous avez besoin d'aide, vous pouvez",
                        "&futiliser la commande /stafflist et contacter",
                        "&fun membre du staff",
                        "",
                        "&f&l» &eCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
                player.chat("/stafflist");
            }
        });
        buttons.put(10, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("&6&lParamètres").setLore(
                        "&fPermet d'accéder à vos paramètres.",
                        "",
                        "&f&l» &eCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new SettingsMenu(new ServerSelectorMenu()).openMenu(player);
            }
        });

        buttons.put(49, new TeleportToSpawnButton());
        buttons.put(50, new AllGamemodeButton());
        buttons.put(48, new ParkourButton());


        buttons.put(21, new UHCGamemodeButton(UHCServer.ServerType.MHA));
        buttons.put(22, new UHCGamemodeButton(UHCServer.ServerType.MUGIWARA));
        buttons.put(32, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&6&lSoon...").toItemStack()));
        buttons.put(30, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&6&lSoon...").toItemStack()));

        buttons.put(23, new CTFButton());
        buttons.put(31, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&6&lSoon...").toItemStack()));

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Menu Principal";
    }

    private static class TeleportToSpawnButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Heads.LANTERN.toItemStack()).setName("&6&lRetourner au Centre").setLore(
                            "&fCliquez-ici pour vous téléporter au centre",
                            "&fdu lobby",
                            "",
                            "&f&l» &eCliquez-ici pour vous téléporter")
                    .toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.teleport(Main.getInstance().getSpawn());
            player.closeInventory();
        }
    }

    private static class ParkourButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.FEATHER).setName("&6&lCommencer le Jump").setLore(
                            "&fCliquez-ici pour vous téléporter au point",
                            "&fde commencement du jump",
                            "",
                            "&f&l» &c&mCliquez-ici pour vous téléporter")
                    .toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
//            player.teleport(Main.getInstance().getJumpManager().getJumpStart());
//            player.closeInventory();
        }
    }

    @RequiredArgsConstructor
    private static class AllGamemodeButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            int i = 0;
            for (UHCServer uhcServer : BukkitAPI.getCommonAPI().getServerCache().getUhcServers().values()) {
                i += uhcServer.getPlayers();
            }
            return new ItemBuilder(Heads.CHEST_MINECART.toItemStack()).setName("&6&lServeurs Publiques").setLore(
                    "&fPermet d'afficher tous les serveurs à",
                    "&fla demande",
                    "",
                    "&a&l" + i + " &8joueurs en jeu",
                    "",
                    "&f&l» &eCliquez-ici pour y accéder"
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
            for (UHCServer uhcServer : BukkitAPI.getCommonAPI().getServerCache().getUhcServers().values()) {
                if (uhcServer.getType() == game) i += uhcServer.getPlayers();
            }

            List<String> lore = new ArrayList<>();
            lore.add("&fPermet d'accéder à la liste de serveurs");
            lore.add("&fdu mode de jeu &c" + game.getName());
            if (game == UHCServer.ServerType.MHA) {
                lore.add("");
                lore.add("&8┃ &7Créateur: &cAlexQLF");
            }
            if (game == UHCServer.ServerType.MUGIWARA) {
                lore.add("");
                lore.add("&8┃ &7Créateur: &cShot0w");
                lore.add("&8┃ &7Développeur: &cRhodless");
            }
            lore.add("");
            lore.add("&a&l" + i + " &8joueurs en jeu");
            lore.add("");
            lore.add("&f&l» &eCliquez-ici pour y accéder");

            ItemStack is;
            if (game == UHCServer.ServerType.MHA) is = Heads.DEKU.toItemStack();
            else is = Heads.LUFFY.toItemStack();

            return new ItemBuilder(is).setName("&6&l" + game.getName()).setLore(lore).toItemStack();
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
            for (CTFServer ctfServer : BukkitAPI.getCommonAPI().getServerCache().getCtfServers().values()) {
                i += ctfServer.getPlayers();
            }

            i += BukkitAPI.getTotalPlayers() - BukkitAPI.getNormalPlayers();

            return new ItemBuilder(Heads.SASUKE.toItemStack()).setName("&6&lNaruto-CTF").setLore(
                    "&fPermet d'accéder à la liste de serveurs",
                    "&fdu mode de jeu &cNaruto-CTF",
                    "",
                    "&a&l" + i + " &8joueurs en jeu",
                    "",
                    "&f&l» &eCliquez-ici pour y accéder"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new CTFMenu(new ServerSelectorMenu(), false).openMenu(player);
        }
    }
}
