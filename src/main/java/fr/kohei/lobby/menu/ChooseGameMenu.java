package fr.kohei.lobby.menu;

import fr.kohei.lobby.Main;
import fr.kohei.BukkitAPI;
import fr.kohei.manager.server.UHCServer;
import fr.kohei.menu.Button;
import fr.kohei.menu.Menu;
import fr.kohei.menu.pagination.ConfirmationMenu;
import fr.kohei.menu.pagination.PaginatedMenu;
import fr.kohei.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ChooseGameMenu extends PaginatedMenu {

    private final Menu oldMenu;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Mode de jeu";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (UHCServer.ServerType value : UHCServer.ServerType.values()) {
            buttons.put(buttons.size(), new GameButton(value));
        }

        return buttons;
    }

    @Override
    public Menu backButton() {
        return oldMenu;
    }

    @RequiredArgsConstructor
    private class GameButton extends Button {
        private final UHCServer.ServerType game;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(game.getDisplay()).setName("&c" + game.getName()).setLore(
                    "",
                    "&f&l» &cCliquez-ici pour commander"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new ConfirmationMenu(() -> {
                Main.createUHC(player, game.getName());
                player.closeInventory();
            }, getButtonItem(player), new ChooseGameMenu(oldMenu)).openMenu(player);
        }
    }
}
