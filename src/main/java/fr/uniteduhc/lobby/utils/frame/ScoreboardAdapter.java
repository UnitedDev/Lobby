package fr.uniteduhc.lobby.utils.frame;

import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.common.cache.data.Division;
import fr.uniteduhc.common.cache.data.ProfileData;
import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.lobby.manager.player.LobbyPlayer;
import fr.uniteduhc.lobby.utils.other.ProgressBar;
import fr.uniteduhc.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreboardAdapter implements FrameAdapter {

    @Override
    public String getTitle(final Player player) {
        return "&6&lUNITED";
    }

    public int index = 0;
    List<String> ip = Arrays.asList(
            "§6uniteduhc.fr",
            "§6§eu§6nited.fr",
            "§6u§en§6ited.fr",
            "§6un§ei§6ted.fr",
            "§6uni§et§6ed.fr",
            "§6unit§ee§6d.fr",
            "§6unite§ed§6.fr",
            "§6united§e.§6fr",
            "§6united.§ef§6r",
            "§6united.f§er",
            "§euniteduhc.fr",
            "§euniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr",
            "§euniteduhc.fr",
            "§euniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr",
            "§6uniteduhc.fr"
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
