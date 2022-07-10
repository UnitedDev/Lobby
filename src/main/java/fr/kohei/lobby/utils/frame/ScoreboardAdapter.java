package fr.kohei.lobby.utils.frame;

import fr.kohei.BukkitAPI;
import fr.kohei.common.cache.data.Division;
import fr.kohei.common.cache.data.ProfileData;
import fr.kohei.lobby.Main;
import fr.kohei.lobby.manager.player.LobbyPlayer;
import fr.kohei.lobby.utils.other.ProgressBar;
import fr.kohei.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreboardAdapter implements FrameAdapter {

    @Override
    public String getTitle(final Player player) {
        return "&6&LKOHEI";
    }

    public int index = 0;
    List<String> ip = Arrays.asList(
            "§6kohei.fr",
            "§6§ek§6ohei.fr",
            "§6k§eo§6hei.fr",
            "§6ko§eh§6ei.fr",
            "§6koh§ee§6i.fr",
            "§6kohe§ei§6.fr",
            "§6kohei§e.§6fr",
            "§6kohei.§ef§6r",
            "§ekohei.fr",
            "§ekohei.fr",
            "§6kohei.fr",
            "§6kohei.fr",
            "§ekohei.fr",
            "§ekohei.fr",
            "§6kohei.fr",
            "§6kohei.fr",
            "§6kohei.fr",
            "§6kohei.fr",
            "§6kohei.fr",
            "§6kohei.fr",
            "§6kohei.fr",
            "§6kohei.fr",
            "§6kohei.fr",
            "§6kohei.fr"
    );

    @Override
    public List<String> getLines(final Player player) {

        final List<String> toReturn = new ArrayList<>();
        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player);

        toReturn.add("");
        if (lobbyPlayer.isInParkour()) {
//            toReturn.add(" &8┃ &fMeilleur Temps: &a" + TimeUtil.niceTime(Main.getInstance().getPlayerCache().get(player.getUniqueId()).getParkourTime()));
            toReturn.add(" &8┃ &fTemps: &a" + TimeUtil.niceTime(lobbyPlayer.getParkour().getCurrentTime()));
        } else {
            String prefix = profile.getRank().getTabPrefix();
            toReturn.add(" &8┃ &fGrade: &r" + (prefix.equals("&7") ? "&7&lJOUEUR" : profile.getRank().getTabPrefix()));
            String display = (profile.getHosts() == 0 ? "&c✗" : String.valueOf(profile.getHosts()));
            if (profile.getHosts() <= -1) {
                display = "&aIllimité";
            }
            toReturn.add(" &8┃ &fHosts: &d" + display);
            toReturn.add(" &8┃ &fCoins: &e" + (profile.getCoins() + " ⛁"));
            toReturn.add(" ");
            toReturn.add(" &8┃ &fDivision: " + profile.getDivision().getDisplay());
            toReturn.add(getAdvancement(profile.getExperience(), profile.getDivision()));
        }
        toReturn.add("&2");
        toReturn.add(" &8┃ &fConnectés: &a" + BukkitAPI.getTotalPlayers());
        toReturn.add(" &8┃ &fHub: &a#" + (Main.getInstance().getFactory(Bukkit.getPort()).getName().replace("Lobby-", "")) + " &7(&f" + Bukkit.getOnlinePlayers().size() + "&7)");
        toReturn.add("&3");
        toReturn.add("&e        " + ip.get(index));

        return toReturn;
    }

    private String getAdvancement(int experience, Division division) {
        int percentage = experience * 100 / division.getExperience();
        return "  " + ProgressBar.getProgressBar(experience, division.getExperience(), 7, "■", ChatColor.GREEN, ChatColor.GRAY) + "&f " + percentage + "%";
    }
}
