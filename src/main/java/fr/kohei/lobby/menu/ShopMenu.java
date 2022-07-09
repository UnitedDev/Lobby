package fr.kohei.lobby.menu;

import fr.kohei.menu.Button;
import fr.kohei.menu.GlassMenu;
import fr.kohei.menu.Menu;
import fr.kohei.menu.buttons.BackButton;
import fr.kohei.menu.buttons.DisplayButton;
import fr.kohei.utils.Heads;
import fr.kohei.utils.ItemBuilder;
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