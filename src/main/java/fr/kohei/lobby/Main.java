package fr.kohei.lobby;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.provider.service.CloudServiceFactory;
import de.dytanic.cloudnet.driver.service.ServiceConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import fr.kohei.BukkitAPI;
import fr.kohei.common.cache.data.ProfileData;
import fr.kohei.common.cache.rank.Rank;
import fr.kohei.lobby.manager.*;
import fr.kohei.lobby.utils.frame.Frame;
import fr.kohei.lobby.utils.frame.ScoreboardAdapter;
import fr.kohei.lobby.manager.packets.PlayerChatPacket;
import fr.kohei.lobby.manager.packets.PlayerChatSubscriber;
import fr.kohei.lobby.task.BoxTask;
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
import java.util.HashMap;
import java.util.List;

@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    private Location spawn;
    private List<ScoreboardTeam> teams;

    private BukkitManager bukkitManager;
    private JumpManager jumpManager;
    private CosmeticManager cosmeticManager;
    private AnnouncementsManager announcementsManager;
    private NPCManager npcManager;

    private boolean restricted;
    private BoxTask boxTask;

    @Override
    public void onEnable() {
        saveConfig();
        instance = this;

        this.restricted = getConfig().getBoolean("restricted");
        this.teams = new ArrayList<>();
        this.spawn = new Location(Bukkit.getWorld("world"), -20.5, 45, 0.5, -90, 0);

        this.bukkitManager = new BukkitManager(this);
        this.jumpManager = new JumpManager(this);
        this.cosmeticManager = new CosmeticManager(this);
        this.announcementsManager = new AnnouncementsManager(this);
        this.npcManager = new NPCManager(this);

        new Frame(this, new ScoreboardAdapter());

        this.spawn.getWorld().setGameRuleValue("doDaylightCycle", "false");
        this.spawn.getWorld().setGameRuleValue("randomTickSpeed", "0");

        BukkitAPI.getCommonAPI().getMessaging().registerAdapter(PlayerChatPacket.class, new PlayerChatSubscriber());

        int i = 0;
        for (Rank value : BukkitAPI.getCommonAPI().getRanks()) {
            String prefix = ChatUtil.translate(value.getTabPrefix() + (value.token().equalsIgnoreCase("default") ? "" : " "));

            char character = alphabet[i++];
            RANKS_ALPHABET.put(value.token(), character);
            teams.add(new ScoreboardTeam(String.valueOf(character), prefix));
        }

        new LobbyUpdateTask(this).runTaskTimer(this, 0, 5 * 20);
        new PlayersTask(this).runTaskTimer(this, 0, 20);

        this.boxTask = new BoxTask(this);
        this.boxTask.runTaskTimer(this, 0, 5);
    }

    public static final HashMap<String, Character> RANKS_ALPHABET = new HashMap<>();
    public static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase().toCharArray();

    public ScoreboardTeam getScoreboardTeam(String name) {
        return teams.stream().filter(t -> t.getName().equals(name)).findFirst().orElse(null);
    }

    public ServiceInfoSnapshot getFactory(int port) {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
                .filter(service -> service.getAddress().getPort() == port)
                .findFirst().orElse(null);
    }

    public void createUHC(Player player, String name) {
        ProfileData data = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

        if (data.getHosts() == 0 && data.getHosts() != -1L) {
            player.sendMessage(ChatUtil.prefix("&cVous n'avez pas assez d'hosts..."));
            return;
        }

        data.setHosts(data.getHosts() - 1);

        CloudServiceFactory cloudService = CloudNetDriver.getInstance().getCloudServiceFactory();
        ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(name);

        if (serviceTask == null) {
            player.sendMessage(ChatUtil.prefix("&cImpossible de lancer l'uhc. Erreur 101."));
            return;
        }
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
                if (BukkitAPI.getCommonAPI().getServerCache().getUhcServers().containsKey(port)) {
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

    public void sendLinkTitle(Player player) {
        Title.sendTitle(player, 0, 40, 0, "&c✗ &fCompte &anon lié&f &c✗", " ");
    }

}
