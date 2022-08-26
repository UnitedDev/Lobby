package fr.uniteduhc.lobby.task;

import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.common.cache.data.ProfileData;
import fr.uniteduhc.common.cache.server.impl.LobbyServer;
import fr.uniteduhc.common.utils.messaging.list.packets.LobbyUpdatePacket;
import fr.uniteduhc.lobby.Main;
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

        LobbyServer lobbyServer = new LobbyServer(Bukkit.getPort(), Main.getInstance().isRestricted(), Bukkit.getOnlinePlayers().size(), ranks);
        BukkitAPI.getCommonAPI().getMessaging().sendPacket(new LobbyUpdatePacket(lobbyServer));
    }
}
