package fr.kohei.lobby.menu;

import fr.kohei.lobby.manager.cosmetics.gadgets.GadgetsMenu;
import fr.kohei.lobby.manager.cosmetics.skin.SkinSelectorMenu;
import fr.kohei.lobby.manager.cosmetics.skin.SkinType;
import fr.kohei.lobby.manager.player.LobbyProfileData;
import fr.kohei.menu.Button;
import fr.kohei.menu.GlassMenu;
import fr.kohei.menu.buttons.DisplayButton;
import fr.kohei.utils.Heads;
import fr.kohei.utils.ItemBuilder;
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
            return new ItemBuilder(Material.FEATHER).setName("&6&lGadget").setLore(
                    "&fPermet de modifier son gadget sur les",
                    "&flobbies.",
                    "",
                    "&8▎ &7Skin: &aDouble Saut",
                    "",
                    "&f&l» &eCliquez-ici pour y accéder"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new GadgetsMenu(new CosmeticsMenu()).openMenu(player);
        }
    }
}
