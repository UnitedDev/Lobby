package fr.uniteduhc.lobby.menu;

import fr.uniteduhc.menu.Button;
import fr.uniteduhc.menu.GlassMenu;
import fr.uniteduhc.menu.Menu;
import fr.uniteduhc.menu.buttons.BackButton;
import fr.uniteduhc.menu.buttons.DisplayButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SettingsMenu extends GlassMenu {
    private final Menu oldMenu;

    @Override
    public int getGlassColor() {
        return 14;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(12, new ProfileMenu.SettingsButton(ProfileMenu.Settings.MESSAGES));
        buttons.put(13, new ProfileMenu.SettingsButton(ProfileMenu.Settings.NOTIFICATIONS));
        buttons.put(14, new ProfileMenu.SettingsButton(ProfileMenu.Settings.FRIENDS));

        if (oldMenu == null) {
            buttons.put(24, new DisplayButton(new ItemStack(Material.AIR)));
        } else {
            buttons.put(22, new BackButton(oldMenu));
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Paramètres";
    }
}
