package fr.kohei.lobby.task;

import fr.kohei.common.cache.rank.Rank;
import fr.kohei.lobby.Main;
import fr.kohei.lobby.manager.player.LobbyPlayer;
import fr.kohei.BukkitAPI;
import fr.kohei.utils.ScoreboardTeam;
import fr.kohei.utils.TimeUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@RequiredArgsConstructor
public class PlayersTask extends BukkitRunnable {

    private final Main main;

    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            LobbyPlayer lobbyPlayer = new LobbyPlayer(player);
//            if (lobbyPlayer.isInParkour()) {
//                Title.sendActionBar(player, ChatUtil.translate("&cMeilleurs temps: &7" + getNiceDuration(Main.getInstance().getPlayerCache().get(player.getUniqueId()).getParkourTime()) + " &8┃ &cTemps: &7" + getNiceDuration(lobbyPlayer.getParkour().getCurrentTime())));
//            }
        }

        updateTabRanks();
    }

    public String getNiceDuration(long duration) {
        if (duration == -1L) {
            return "Aucun";
        }
        return TimeUtil.niceTime(duration);
    }

    public void updateTabRanks() {
        for (Player players1 : Bukkit.getOnlinePlayers()) {
            for (Player players2 : Bukkit.getOnlinePlayers()) {
                try {
                    Rank rank = BukkitAPI.getCommonAPI().getProfile(players2.getUniqueId()).getRank();
                    ScoreboardTeam team = Main.getInstance().getScoreboardTeam(String.valueOf(Main.RANKS_ALPHABET.get(rank.token()))  );

                    ((CraftPlayer) players1).getHandle().playerConnection.sendPacket(team.addOrRemovePlayer(3, players2.getName()));
                } catch (Exception ignored) {}
            }
        }
    }
}