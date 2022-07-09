package fr.kohei.lobby.menu;

import fr.kohei.BukkitAPI;
import fr.kohei.common.cache.server.impl.UHCServer;
import fr.kohei.lobby.Main;
import fr.kohei.menu.Button;
import fr.kohei.menu.Menu;
import fr.kohei.menu.pagination.ConfirmationMenu;
import fr.kohei.menu.pagination.PaginatedMenu;
import fr.kohei.utils.Heads;
import fr.kohei.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class UHCGamemodeMenu extends PaginatedMenu {

    private final Menu oldMenu;
    private final UHCServer.ServerType game;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return game.getName();
    }

    @Override
    public Menu backButton() {
        return oldMenu;
    }

    @Override
    public int getGlassColor() {
        return 4;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(39, new CreateServerGameButton());

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (UHCServer uhcServer : BukkitAPI.getCommonAPI().getServerCache().getUhcServers().values()) {
            if (uhcServer.getType() != game) continue;
            buttons.put(buttons.size(), new UHCButton(uhcServer));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private class UHCButton extends Button {
        private final UHCServer uhcServer;

        @Override
        public ItemStack getButtonItem(Player player) {
            return AllServersSelectorMenu.getItem(uhcServer).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.chat("/joinqueue uhc-" + uhcServer.getPort());
        }

        @Override
        public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
            return true;
        }
    }

    private class CreateServerGameButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return getCreateServerGameItem(game);
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new ConfirmationMenu(() -> {
                Main.getInstance().createUHC(player, game.getName());
                player.closeInventory();
            }, getCreateServerGameItem(game), new  UHCGamemodeMenu(oldMenu, game)).openMenu(player);
        }
    }

    public static ItemStack getCreateServerGameItem(UHCServer.ServerType game) {
        return new ItemBuilder(Heads.COMMAND_BLOCK.toItemStack()).setName("&a&lCréer un serveur").setLore(
                "&fPermet de créer un serveur uhc avec le",
                "&fmode &c" + game.getName(),
                "",
                "&f&l» &eCliquez-ici pour confirmer"
        ).toItemStack();
    }

}
