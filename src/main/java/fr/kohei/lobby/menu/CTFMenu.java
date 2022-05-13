package fr.kohei.lobby.menu;

import fr.kohei.BukkitAPI;
import fr.kohei.manager.server.CTFServer;
import fr.kohei.menu.Button;
import fr.kohei.menu.Menu;
import fr.kohei.menu.buttons.DisplayButton;
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
public class CTFMenu extends PaginatedMenu {

    private final Menu backMenu;
    private final boolean playing;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Naruto CTF";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(3, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Heads.CHEST_MINECART.toItemStack()).setName("&cEn Attente").setLore(
                        "",
                        "&f&l» &cCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new CTFMenu(new ServerSelectorMenu(), false).openMenu(player);
            }
        });


        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Heads.COMMAND_BLOCK.toItemStack()).setName("&cEn Jeu &8(&cSoon&8)").setLore(
                        "",
                        "&f&l» &cCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
//                new CTFMenu(new ServerSelectorMenu(), true).openMenu(player);
            }
        });

        buttons.put(5, new DisplayButton(new ItemBuilder(Heads.SOON.toItemStack()).setName("&cSoon").toItemStack()));

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;

        for (CTFServer ctfServer : BukkitAPI.getServerCache().getCtfServers().values()) {
            if (playing) {
                if (ctfServer.getStatus() == CTFServer.ServerStatus.PLAYING) {
                    buttons.put(i++, new CTFButton(ctfServer));
                }
            } else {
                if (ctfServer.getStatus() == CTFServer.ServerStatus.WAITING) {
                    buttons.put(i++, new CTFButton(ctfServer));
                }
            }
        }

        return buttons;
    }

    @Override
    public Menu backButton() {
        return backMenu;
    }

    @RequiredArgsConstructor
    public static class CTFButton extends Button {

        private final CTFServer server;

        @Override
        public ItemStack getButtonItem(Player player) {

            if (server.getStatus() == CTFServer.ServerStatus.WAITING) {
                return new ItemBuilder(Heads.BALLOON_GREEN.toItemStack()).setName("&cCTF-" + server.getPort()).setLore(
                        "",
                        "&f➥ &c&lServeur",
                        " &fJoueurs: &a" + server.getPlayers() + "&8/&a16",
                        " &fStatus: &eAttente",
                        " &fStart: &c" + server.getStart(),
                        " ",
                        "&f&l» &cCliquez-ici pour rejoindre"
                ).toItemStack();
            } else {
                return new ItemBuilder(Heads.BALLOON_RED.toItemStack()).setName("&cCTF-" + server.getPort()).setLore(
                        "",
                        "&f➥ &c&lServeur",
                        " &fJoueurs: &a" + server.getPlayers() + "&8/&a16",
                        " &fStatus: &cEn cours",
                        " ",
                        "&f&l» &cCliquez-ici pour rejoindre"
                ).toItemStack();
            }
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            BukkitAPI.sendToServer(player, "ctf-" + server.getPort());
        }
    }
}
