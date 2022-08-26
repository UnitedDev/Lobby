package fr.uniteduhc.lobby.menu;

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
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ShopMenu extends GlassMenu {
    private final Menu oldMenu;

    @Override
    public String getTitle(Player player) {
        return "Boutique";
    }

    @Override
    public int getGlassColor() {
        return 0;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

//        buttons.put(12, new RanksButton());
        // RANKS

        if (oldMenu != null)
            buttons.put(40, new BackButton(oldMenu));
        else buttons.put(40, new DisplayButton(new ItemStack(Material.AIR)));

        return buttons;
    }

//    private class RanksButton extends Button {
//        @Override
//        public ItemStack getButtonItem(Player player) {
//            return new ItemBuilder(Heads.SPECIAL_CHEST.toItemStack()).setName("&6&lBoutique").setLore(
//                    "&fPermet d'accéder "
//            );
//        }
//    }
}