package fr.kohei.lobby.task;

import fr.kohei.common.cache.ProfileData;
import fr.kohei.common.cache.Rank;
import fr.kohei.lobby.Main;
import fr.kohei.BukkitAPI;
import fr.kohei.manager.server.LobbyServer;
import fr.kohei.messaging.list.packet.LobbyUpdatePacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

@RequiredArgsConstructor
@Getter
public class LobbyUpdateTask extends BukkitRunnable {

    private final Main main;

    @Override
    public void run() {
        HashMap<String, Integer> ranks = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

            ranks.put(profile.getRank().token(), ranks.getOrDefault(profile.getRank().token(), 0) + 1);
        }

        LobbyServer lobbyServer = new LobbyServer(Bukkit.getPort(), Main.isRestricted(), Bukkit.getOnlinePlayers().size(), ranks);
        BukkitAPI.getMessaging().sendPacket(new LobbyUpdatePacket(lobbyServer));
    }
}
