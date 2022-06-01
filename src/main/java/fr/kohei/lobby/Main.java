package fr.kohei.lobby;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.provider.service.CloudServiceFactory;
import de.dytanic.cloudnet.driver.service.ServiceConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import fr.kohei.BukkitAPI;
import fr.kohei.common.cache.ProfileData;
import fr.kohei.common.cache.Rank;
import fr.kohei.lobby.frame.Frame;
import fr.kohei.lobby.frame.ScoreboardAdapter;
import fr.kohei.lobby.manager.BukkitManager;
import fr.kohei.lobby.manager.CosmeticManager;
import fr.kohei.lobby.manager.JumpManager;
import fr.kohei.lobby.task.LobbyUpdateTask;
import fr.kohei.lobby.task.PlayersTask;
import fr.kohei.utils.ChatUtil;
import fr.kohei.utils.ScoreboardTeam;
import fr.kohei.utils.Title;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    private static BukkitManager bukkitManager;
    @Getter
    private static Location spawn;
    @Getter
    private static List<ScoreboardTeam> teams;
    @Getter
    private static JumpManager jumpManager;
    @Getter
    private static CosmeticManager cosmeticManager;
    @Getter
    private static boolean restricted;

    @Override
    public void onEnable() {
        saveConfig();
        instance = this;

        bukkitManager = new BukkitManager(this);
        spawn = new Location(Bukkit.getWorld("world"), 390.5, 89, 209.5, -90, 0);
        teams = new ArrayList<>();
        jumpManager = new JumpManager(this);
        cosmeticManager = new CosmeticManager(this);
        restricted = getConfig().getBoolean("restricted");

        new Frame(this, new ScoreboardAdapter());

        spawn.getWorld().setGameRuleValue("doDaylightCycle", "false");
        spawn.getWorld().setGameRuleValue("randomTickSpeed", "0");

        for (Rank value : BukkitAPI.getCommonAPI().getRanks()) {

            String position = String.valueOf(PlayersTask.number(value.permissionPower()));
            String prefix = ChatUtil.translate(value.getTabPrefix() + (value.token().equalsIgnoreCase("default") ? "" : " "));
            teams.add(new ScoreboardTeam(position, prefix));
        }

        new LobbyUpdateTask(this).runTaskTimer(this, 0, 5 * 20);
        new PlayersTask(this).runTaskTimer(this, 0, 20);
    }

    public static ScoreboardTeam getScoreboardTeam(String name) {
        return teams.stream().filter(t -> t.getName().equals(name)).findFirst().orElse(null);
    }

    public static ServiceInfoSnapshot getFactory(int port) {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
                .filter(service -> service.getAddress().getPort() == port)
                .findFirst().orElse(null);
    }

    public static void createUHC(Player player, String name) {
        ProfileData data = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

        if (data.getHosts() == 0 && data.getHosts() != -1L) {
            player.sendMessage(ChatUtil.prefix("&cVous n'avez pas assez d'hosts..."));
            return;
        }

        data.setHosts(data.getHosts() - 1);

        CloudServiceFactory cloudService = CloudNetDriver.getInstance().getCloudServiceFactory();
        ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(name);

        ServiceConfiguration config = ServiceConfiguration.builder(serviceTask).build();
        ServiceInfoSnapshot service = cloudService.createCloudService(config);

        if (service == null) {
            player.sendMessage(ChatUtil.prefix("&cImpossible de lancer l'uhc. Erreur 102."));
            return;
        }

        service.provider().start();
        player.closeInventory();
        player.sendMessage(ChatUtil.prefix("&cLancement de votre serveur en cours..."));
        int port = service.getAddress().getPort();

        new BukkitRunnable() {
            int currentLoad = 0;

            @Override
            public void run() {

                if (BukkitAPI.getServerCache().getUhcServers().containsKey(port)) {
                    player.sendMessage(ChatUtil.prefix("&cVotre serveur a été créé avec succès."));
                    BukkitAPI.sendToServer(player, service.getName());
                    cancel();
                    return;
                }

                Title.sendTitle(player, 0, 3, 0, " ", Main.LOADING.get(currentLoad));

                if (currentLoad + 1 >= Main.LOADING.size()) {
                    currentLoad = 0;
                    return;
                }
                currentLoad++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 2);
    }

    public static List<String> LOADING = Arrays.asList(
            "▁▂▃▄▅▆▇▉▉▇▆▅▄▃▂▁",
            "▁▁▂▃▄▅▆▇▉▉▇▆▅▄▃▂",
            "▂▁▁▂▃▄▅▆▇▉▉▇▆▅▄▃",
            "▃▂▁▁▂▃▄▅▆▇▉▉▇▆▅▄",
            "▄▃▂▁▁▂▃▄▅▆▇▉▉▇▆▅",
            "▅▄▃▂▁▁▂▃▄▅▆▇▉▉▇▆",
            "▆▅▄▃▂▁▁▂▃▄▅▆▇▉▉▇",
            "▇▆▅▄▃▂▁▁▂▃▄▅▆▇▉▉",
            "▉▇▆▅▄▃▂▁▁▂▃▄▅▆▇▉",
            "▉▉▇▆▅▄▃▂▁▁▂▃▄▅▆▇",
            "▇▉▉▇▆▅▄▃▂▁▁▂▃▄▅▆",
            "▅▆▇▉▉▇▆▅▄▃▂▁▁▂▃▄",
            "▄▅▆▇▉▉▇▆▅▄▃▂▁▁▂▃",
            "▂▃▄▅▆▇▉▉▇▆▅▄▃▂▁▁"
    );


}
