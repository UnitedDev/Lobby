package fr.kohei.lobby.menu;

import fr.kohei.lobby.Main;
import fr.kohei.BukkitAPI;
import fr.kohei.manager.server.UHCServer;
import fr.kohei.menu.Button;
import fr.kohei.menu.Menu;
import fr.kohei.menu.pagination.PaginatedMenu;
import fr.kohei.utils.Heads;
import fr.kohei.utils.ItemBuilder;
import fr.kohei.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AllServersSelectorMenu extends PaginatedMenu {
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
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(39, new CreateServerButton());

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        BukkitAPI.getServerCache().getUhcServers().values().forEach(uhcServer -> {

            buttons.put(buttons.size(), new UHCButton(uhcServer));
        });

        return buttons;
    }

    @RequiredArgsConstructor
    private static class UHCButton extends Button {
        private final UHCServer uhcServer;

        @Override
        public ItemStack getButtonItem(Player player) {
            return getItem(uhcServer).toItemStack();
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

    public static ItemBuilder getItem(UHCServer server) {
        UHCServer.ServerStatus status = server.getStatus();
        ItemStack itemStack;
        if (status == UHCServer.ServerStatus.CLOSED) {
            itemStack = Heads.BALLOON_ORANGE.toItemStack();
        } else if (status == UHCServer.ServerStatus.FULL || status == UHCServer.ServerStatus.PLAYING) {
            itemStack = Heads.BALLOON_RED.toItemStack();
        } else {
            itemStack = Heads.BALLOON_GREEN.toItemStack();
        }

        List<String> lore = new ArrayList<>();
        lore.add("&7" + Main.getFactory(server.getPort()).getName());
        lore.add(" ");
        lore.add("&f➥ &c&lHost");
        lore.add(" &fHost: &a" + server.getHost());
        lore.add(" &fJoueurs: &e" + server.getPlayers() + "&8/&e" + server.getSlots());
        lore.add(" &fPhase: " + server.getStatus().getName());
        lore.add(" ");
        lore.add("&f➥ &c&lInfos");
        lore.add(" &fTeams: &a" + server.getTeamSize() + "vs" + server.getTeamSize());
        lore.add(" &fTaille: &e± " + server.getBorderStartSize());
        lore.add(" &fBordure: &c" + TimeUtil.niceTime(server.getBorderTimer()));
        lore.add(" &fPvP: &a" + TimeUtil.niceTime(server.getPvpTimer()));
        lore.add(" ");
        lore.add("&f➥ &c&lScenarios");
        if (server.getEnabledScenarios().isEmpty()) {
            lore.add(" &cAucun");
        } else {
            if (server.getEnabledScenarios().size() >= 5) {
                lore.add(" &8» &f" + server.getEnabledScenarios().get(0));
                lore.add(" &8» &f" + server.getEnabledScenarios().get(1));
                lore.add(" &8» &f" + server.getEnabledScenarios().get(2));
                lore.add(" &8» &f" + server.getEnabledScenarios().get(3));
                lore.add(" &8» &f" + server.getEnabledScenarios().get(4));
                if (server.getEnabledScenarios().size() > 5)
                    lore.add(" &7&oet " + (server.getEnabledScenarios().size() - 5) + " de plus");
            } else {
                server.getEnabledScenarios().forEach(s -> lore.add(" &f" + s));
            }
        }
        lore.add(" ");
        lore.add("&f&l» &cCliquez-ici pour vous connecter");
        return new ItemBuilder(itemStack).setName("&c&l" + server.getCustomName()).setLore(lore);
    }

    private class CreateServerButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Heads.COMMAND_BLOCK.toItemStack()).setName("&cCréer un serveur").setLore(
                    "&fPermet de créer un serveur uhc dans",
                    "&flequel vous serez l'hôte principal",
                    "",
                    "&f&l» &cCliquez-ici pour confirmer"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new ChooseGameMenu(new AllServersSelectorMenu(oldMenu)).openMenu(player);
        }
    }


}
