package fr.uniteduhc.lobby.manager.cosmetics.gadgets;

import fr.uniteduhc.lobby.manager.player.LobbyProfileData;
import fr.uniteduhc.menu.Button;
import fr.uniteduhc.menu.GlassMenu;
import fr.uniteduhc.menu.Menu;
import fr.uniteduhc.menu.buttons.BackButton;
import fr.uniteduhc.menu.buttons.DisplayButton;
import fr.uniteduhc.utils.Heads;
import fr.uniteduhc.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GadgetsMenu extends GlassMenu {
    private final Menu oldMenu;

    @Override
    public int getGlassColor() {
        return 14;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(12, new DoubleJumpGadget());
//        buttons.put(13, new OneForAllGadget());
//        buttons.put(14, new GrenouilleGadget());
        if(oldMenu != null) buttons.put(22, new BackButton(oldMenu));
        buttons.put(21, new DisplayButton(new ItemStack(Material.AIR)));

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Cosmétiques";
    }

    private static class DoubleJumpGadget extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            String clickLine = "&f&l» &eCliquez-ici pour changer";
            LobbyProfileData profileData = new LobbyProfileData(player);

            return new ItemBuilder(Material.FEATHER).setName("&6&lFly ou Double Jump").setLore(
                    "&fPermet de choisir sa préférence de gadget",
                    "&ftous les lobbies.",
                    "&f",
                    "&8▎ " + (profileData.getFly().equals("fly") ? "&a" : "&7") + "Fly sur tous les lobbies",
                    "&8▎ " + (profileData.getFly().equals("dj") ? "&a" : "&7") + "Double Jump sur tous les lobbies",
                    "",
                    clickLine
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            LobbyProfileData profileData = new LobbyProfileData(player);

            if (profileData.getFly().equals("aucun") || profileData.getFly().equals("fly")) {
                profileData.setFly("dj");
            } else {
                profileData.setFly("fly");
            }
            player.setFlying(false);
        }

        @Override
        public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
            return true;
        }
    }
}
