package fr.uniteduhc.lobby.menu;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.provider.service.CloudServiceFactory;
import de.dytanic.cloudnet.driver.service.ServiceConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.common.cache.data.ProfileData;
import fr.uniteduhc.common.cache.rank.Rank;
import fr.uniteduhc.common.cache.server.impl.LobbyServer;
import fr.uniteduhc.menu.Button;
import fr.uniteduhc.menu.Menu;
import fr.uniteduhc.menu.pagination.ConfirmationMenu;
import fr.uniteduhc.menu.pagination.PaginatedMenu;
import fr.uniteduhc.utils.ChatUtil;
import fr.uniteduhc.utils.Heads;
import fr.uniteduhc.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LobbySelectorMenu extends PaginatedMenu {

    private final Menu oldMenu;
    private final boolean restricted;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Selecteur de Lobby";
    }

    @Override
    public int getGlassColor() {
        return 9;
    }

    @Override
    public Menu backButton() {
        return oldMenu;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {

        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                if(restricted) {
                    return new ItemBuilder(Heads.SETTINGS.toItemStack()).setName("&6&lRestricted Hubs &8(&aSelectionné&8)").toItemStack();
                }
                return new ItemBuilder(Heads.SETTINGS.toItemStack()).setName("&6&lRestricted Hubs").setLore(
                        "&fPermet de voir la liste des joueurs réservés",
                        "&faux gradés.",
                        "",
                        "&f&l» &eCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new LobbySelectorMenu(oldMenu, !restricted).openMenu(player);
            }
        });

        if (player.isOp() || BukkitAPI.getCommonAPI().getProfile(player.getUniqueId()).getRank().permissionPower() >= 100) {
            buttons.put(18, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Heads.COMMAND_BLOCK.toItemStack()).setName("&a&lCréer un lobby").setLore(
                            "&c⚠ &7Réservée aux administrateurs",
                            "",
                            "&f&l» &eCliquez-ici pour confirmer"
                    ).toItemStack();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    new ConfirmationMenu(() -> {
                        CloudServiceFactory cloudService = CloudNetDriver.getInstance().getCloudServiceFactory();
                        ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask("Lobby");

                        if(serviceTask == null) {
                            player.sendMessage(ChatUtil.prefix("&cImpossible de lancer un lobby. Erreur 101."));
                            return;
                        }

                        ServiceConfiguration config = ServiceConfiguration.builder(serviceTask).build();
                        ServiceInfoSnapshot service = cloudService.createCloudService(config);

                        if (service == null) {
                            player.sendMessage(ChatUtil.prefix("&cImpossible de lancer un lobby. Erreur 102."));
                            return;
                        }

                        service.provider().startAsync();
                        player.closeInventory();
                        player.sendMessage(ChatUtil.prefix("&cLancement d'un lobby en cours..."));
                    }, getButtonItem(player), new LobbySelectorMenu(oldMenu, restricted)).openMenu(player);
                }
            });
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        List<LobbyServer> servers = new ArrayList<>(BukkitAPI.getCommonAPI().getServerCache().getLobbyServers().values());
        servers.sort(Comparator.comparing(LobbyServer::getPort));

        for (ServiceInfoSnapshot cloudService : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices()) {
            if (!(cloudService.getName().startsWith("Lobby-"))) continue;
            if (!cloudService.getLifeCycle().equals(ServiceLifeCycle.RUNNING)) continue;

            LobbyServer server = getLobbyFromPort(cloudService.getAddress().getPort());

            if (server == null) continue;

            if (restricted && server.isRestricted()) {
                buttons.put(i++, new LobbyButton(server, cloudService));
            }

            if (!restricted && !server.isRestricted()) {
                buttons.put(i++, new LobbyButton(server, cloudService));
            }
        }

        return buttons;
    }

    public LobbyServer getLobbyFromPort(int port) {
        return BukkitAPI.getCommonAPI().getServerCache().getLobbyServers().get(port);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        return super.getButtons();
    }

    @RequiredArgsConstructor
    public class LobbyButton extends Button {
        private final LobbyServer server;
        private final ServiceInfoSnapshot serverSnapshot;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            HashMap<Rank, Integer> map = new HashMap<>();

            Heads head;
            if(restricted) {
                head = Heads.BALLOON_ORANGE;
            } else {
                head = Heads.BALLOON_GREEN;
            }

            if (server == null) {
                return new ItemBuilder(Heads.BALLOON_RED.toItemStack()).setName("&6&l" + serverSnapshot.getName()).setLore(
                        "",
                        "&cServeur en cours de création"
                ).toItemStack();
            }

            server.getRanks().forEach((s, integer) -> map.put(BukkitAPI.getCommonAPI().getRank(s), integer));

            List<Rank> ranks = new ArrayList<>(map.keySet());

            ranks = ranks.stream().sorted(Comparator.comparing(Rank::permissionPower).reversed()).collect(Collectors.toList());

            lore.add(" ");
            lore.add("&a&l" + server.getPlayers() + " &8joueurs en jeu");
            if (!ranks.isEmpty()) {
                lore.add(" ");
            }

            ranks.forEach(rank -> lore.add("&8┃ " + (rank.getChatPrefix().equalsIgnoreCase("&7") ? "&f&lJOUEUR &7" : rank.getChatPrefix()) + " &f" + map.get(rank)));
            lore.add(" ");
            if (Bukkit.getPort() == server.getPort()) {
                lore.add("&cVous êtes déjà connecté sur ce lobby");
            } else {
                lore.add("&f&l» &eCliquez-ici pour changer de lobby");
            }

            return new ItemBuilder(head.toItemStack()).setAmount(server.getPlayers()).setName("&6&l" + serverSnapshot.getName()).setLore(lore).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

            if (profile.getRank().permissionPower() < 20 && server.isRestricted()) {
                player.sendMessage(ChatUtil.prefix("&cVous ne pouvez pas rejoindre un lobby réservés aux gradés."));
                return;
            }

            if (Bukkit.getPort() != server.getPort()) {
                BukkitAPI.sendToServer(player, serverSnapshot.getName());
            }
        }
    }

}
