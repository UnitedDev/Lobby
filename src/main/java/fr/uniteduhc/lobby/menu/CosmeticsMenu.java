package fr.uniteduhc.lobby.menu;

import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.common.cache.rank.Rank;
import fr.uniteduhc.lobby.manager.cosmetics.gadgets.GadgetsMenu;
import fr.uniteduhc.lobby.manager.cosmetics.skin.SkinSelectorMenu;
import fr.uniteduhc.lobby.manager.cosmetics.skin.SkinType;
import fr.uniteduhc.lobby.manager.player.LobbyProfileData;
import fr.uniteduhc.menu.Button;
import fr.uniteduhc.menu.GlassMenu;
import fr.uniteduhc.menu.buttons.DisplayButton;
import fr.uniteduhc.utils.Heads;
import fr.uniteduhc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CosmeticsMenu extends GlassMenu {
    @Override
    public int getGlassColor() {
        return 4;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(12, new SkinDisplayButton());
        buttons.put(13, new PetDisplayButton());
        buttons.put(14, new GadgetDisplayButton());
        buttons.put(21, new DisplayButton(new ItemStack(Material.AIR)));

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Cosmétiques";
    }

    private static class SkinDisplayButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            ItemStack itemStack;
            itemStack = null;
            LobbyProfileData profileData = new LobbyProfileData(player);
            if (profileData.getSkin().equals("aucun") || profileData.getSkin() == null) {
                // get a random skintype
                String base = SkinType.values()[(int) (Math.random() * SkinType.values().length)].getHead();
                itemStack = new ItemBuilder(Material.SKULL_ITEM).setTexture(base).setDurability(SkullType.PLAYER.ordinal()).toItemStack();
            } else {
                itemStack = new ItemBuilder(Material.SKULL_ITEM).setTexture(profileData.getSkinCosmetic().getSkin().getHead())
                        .setDurability(SkullType.PLAYER.ordinal()).toItemStack();
            }

            return new ItemBuilder(itemStack).setName("&6&lSkin").setLore(
                    "&fPermet de modifier son skin sur les",
                    "&flobbies.",
                    "",
                    "&8▎ &7Skin: &a" + (profileData.getSkin().equals("aucun") || profileData.getSkin() == null ? "&cAucun" : profileData.getSkinCosmetic().getName()),
                    "",
                    "&f&l» &eCliquez-ici pour y accéder"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new SkinSelectorMenu(new CosmeticsMenu()).openMenu(player);
        }
    }

    private static class PetDisplayButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Heads.SASUKE.toItemStack()).setName("&6&lPets").setLore(
                    "&fPermet de modifier son pet sur les",
                    "&flobbies.",
                    "",
                    "&8▎ &7Skin: &cAucun",
                    "",
                    "&f&l» &eCliquez-ici pour y accéder"
            ).toItemStack();
        }
    }

    private static class GadgetDisplayButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            Rank rank = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId()).getRank();

            return new ItemBuilder(Material.FEATHER).setName("&6&lGadget").setLore(
                    "&fPermet de modifier son gadget sur les",
                    "&flobbies.",
                    "",
                    "&8▎ &7Skin: &aDouble Saut",
                    "",
                    (rank.getPermissionPower() > 0 ? "&f&l» &eCliquez-ici pour y accéder" : "&f&l» &cRéservé aux gradés")
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            Rank rank = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId()).getRank();
            if(rank.getPermissionPower() <= 0) return;
            new GadgetsMenu(new CosmeticsMenu()).openMenu(player);
        }
    }
}
