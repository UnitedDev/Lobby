package fr.kohei.lobby.menu;

import fr.kohei.common.cache.ProfileData;
import fr.kohei.BukkitAPI;
import fr.kohei.menu.Button;
import fr.kohei.menu.GlassMenu;
import fr.kohei.menu.Menu;
import fr.kohei.menu.buttons.BackButton;
import fr.kohei.menu.buttons.DisplayButton;
import fr.kohei.utils.Heads;
import fr.kohei.utils.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProfileMenu extends GlassMenu {

    private final Menu oldMenu;

    @Override
    public int getGlassColor() {
        return 4;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new MyProfileButton());
        
        buttons.put(21, new SettingsButton(Settings.MESSAGES));
        buttons.put(22, new SettingsButton(Settings.NOTIFICATIONS));
        buttons.put(23, new SettingsButton(Settings.FRIENDS));

        buttons.put(30, new StatsButton());
        buttons.put(31, new DivisionButton());
        buttons.put(32, new SilentButton());

        if(oldMenu != null) {
            buttons.put(40, new BackButton(oldMenu));
        } else {
            buttons.put(40, new DisplayButton(new ItemStack(Material.AIR)));
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Mon Profil";
    }

    @Getter
    public enum Settings {
        MESSAGES(Material.BOOK_AND_QUILL, "Messages Privés",
                "&fPermet d'activer ou de désactiver la",
                "&frécéption de messages privés."),

        NOTIFICATIONS(Material.EMPTY_MAP, "Notifications",
                "&fPermet d'activer ou de désactiver les",
                "&fnotifications sur le chat."),

        FRIENDS(Material.FIREWORK_CHARGE, "Demandes d'amis",
                "&fPermet d'activer ou de désactiver la",
                "&frécéption des demandes d'amis.");

        private final Material material;
        private final String display;
        private final String[] lore;

        Settings(Material material, String display, String... lore) {
            this.material = material;
            this.display = display;
            this.lore = lore;
        }
    }

    @RequiredArgsConstructor
    private static class SettingsButton extends Button {

        private final Settings settings;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = Arrays.stream(settings.getLore()).collect(Collectors.toList());

            ProfileData data = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

            lore.add(" ");
            if(settings == Settings.MESSAGES) {
                lore.add("&8┃ &7Messages: " + (data.isPrivateMessages() ? "&aActivés" : "&cDésactivés"));
            } else if(settings == Settings.FRIENDS) {
                lore.add("&8┃ &7Demandes d'amis: " + (data.isFriendRequests() ? "&aActivés" : "&cDésactivés"));
            } else if(settings == Settings.NOTIFICATIONS) {
                lore.add("&8┃ &7Notifications: " + (data.isNotifications() ? "&aActivés" : "&cDésactivés"));
            }


            lore.add(" ");
            lore.add("&f&l» &cCliquez-ici pour modifier");

            System.out.println("test3");

            return new ItemBuilder(settings.getMaterial()).setName("&c" + settings.getDisplay()).setLore(lore).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            ProfileData data = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

            if(settings == Settings.MESSAGES) {
                data.setPrivateMessages(!data.isPrivateMessages());
            } else if(settings == Settings.FRIENDS) {
                data.setFriendRequests(!data.isFriendRequests());
            } else if(settings == Settings.NOTIFICATIONS) {
                data.setNotifications(!data.isNotifications());
            }
            BukkitAPI.getCommonAPI().saveProfile(player.getUniqueId(), data);
        }

        @Override
        public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
            return true;
        }
    }

    private static class MyProfileButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            ProfileData data = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

            String display = (data.getHosts() == 0 ? "&c✗" : String.valueOf(data.getHosts()));
            if(data.getHosts() <= -1 ) {
                display = "&aIllimité";
            }
            
            return new ItemBuilder(Material.SKULL_ITEM).setDurability(SkullType.PLAYER.ordinal()).setSkullOwner(player.getName()).setName("&c" + player.getName()).setLore(
                    "&8┃ &7Grade: &r" + (data.getRank().getTabPrefix().equals("&7") ? "&7&lJOUEUR" : data.getRank().getTabPrefix()),
                    "&8┃ &7Link: &c✗",
                    "&8┃ &7Coins: &e" + data.getCoins() + " ⛁",
                    "&8┃ &7Hosts: &e" + display,
                    "&8┃ &7Boxs: &b0 ✦"
            ).toItemStack();
        }
    }

    private class DivisionButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BANNER).setName("&cMa Division").setLore(
                    "&fPermet de connaître votre avancement",
                    "&fsur les divisions.",
                    "",
                    "&f&l» &cCliquez-ici pour y accéder"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new DivisionMenu(new ProfileMenu(oldMenu)).openMenu(player);
        }
    }

    private class SilentButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Heads.YELLOW_I.toItemStack()).setName("&cJoueurs ignorés").setLore(
                    "&fPermet de gérer toute la liste des joueurs",
                    "&fignorés.",
                    "",
                    "&f&l» &cCliquez-ici pour y accéder"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new IgnoreMenu(new ProfileMenu(oldMenu)).openMenu(player);
        }
    }

    private static class StatsButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.ITEM_FRAME).setName("&cStatistiques &8(&7Bientôt&8)").setLore(
                    "&fPermet d'accéder à toutes vos statistiques",
                    "&fpour les uhc et les modes de jeux.",
                    "",
                    "&f&l» &cCliquez-ici pour y accéder"
            ).toItemStack();
        }
    }
}
