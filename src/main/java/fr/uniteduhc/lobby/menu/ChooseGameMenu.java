package fr.uniteduhc.lobby.menu;

import fr.uniteduhc.common.cache.server.impl.UHCServer;
import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.menu.Button;
import fr.uniteduhc.menu.Menu;
import fr.uniteduhc.menu.pagination.ConfirmationMenu;
import fr.uniteduhc.menu.pagination.PaginatedMenu;
import fr.uniteduhc.utils.Heads;
import fr.uniteduhc.utils.ItemBuilder;
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
            ItemStack is;
            if (game == UHCServer.ServerType.MHA) is = Heads.DEKU.toItemStack();
            else is = Heads.LUFFY.toItemStack();

            return new ItemBuilder(is).setName("&6&l" + game.getName()).setLore(
                    "",
                    "&f&l» &eCliquez-ici pour commander"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new ConfirmationMenu(() -> {
                Main.getInstance().createUHC(player, game.getName());
                player.closeInventory();
            }, getButtonItem(player), new ChooseGameMenu(oldMenu)).openMenu(player);
        }
    }
}
