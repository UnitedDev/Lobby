package fr.kohei.lobby.menu;

import fr.kohei.lobby.Main;
import fr.kohei.BukkitAPI;
import fr.kohei.manager.server.UHCServer;
import fr.kohei.menu.Button;
import fr.kohei.menu.Menu;
import fr.kohei.menu.pagination.PaginatedMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MyServersMenu extends PaginatedMenu {
    private final Menu oldMenu;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Serveurs Publiques";
    }

    @Override
    public Menu backButton() {
        return oldMenu;
    }

    @Override
    public int getGlassColor() {
        return 1;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (UHCServer uhcServer : BukkitAPI.getServerCache().getUhcServers().values()) {
            if (uhcServer.getHost().equalsIgnoreCase(player.getName()))
                buttons.put(buttons.size(), new UHCButton(uhcServer));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private static class UHCButton extends Button {
        private final UHCServer uhcServer;

        @Override
        public ItemStack getButtonItem(Player player) {
            return AllServersSelectorMenu.getItem(uhcServer).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            BukkitAPI.sendToServer(player, Main.getFactory(uhcServer.getPort()).getName());
        }

        @Override
        public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
            return true;
        }
    }

}