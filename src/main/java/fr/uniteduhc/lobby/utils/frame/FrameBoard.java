package fr.uniteduhc.lobby.utils.frame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FrameBoard
{
    private final List<FrameBoardEntry> entries;
    private final List<String> identifiers;
    private Scoreboard scoreboard;
    private Objective objective;
    
    public FrameBoard(final Player player) {
        this.entries = new ArrayList<FrameBoardEntry>();
        this.identifiers = new ArrayList<String>();
        this.setup(player);
    }
    
    private void setup(final Player player) {
        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        else {
            this.scoreboard = player.getScoreboard();
        }
        (this.objective = this.scoreboard.registerNewObjective("Default", "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(Frame.getInstance().getAdapter().getTitle(player));
        player.setScoreboard(this.scoreboard);
    }
    
    public FrameBoardEntry getEntryAtPosition(final int pos) {
        if (pos >= this.entries.size()) {
            return null;
        }
        return this.entries.get(pos);
    }
    
    public String getUniqueIdentifier(final String text) {
        String identifier;
        for (identifier = getRandomChatColor() + ChatColor.WHITE; this.identifiers.contains(identifier); identifier = identifier + getRandomChatColor() + ChatColor.WHITE) {}
        if (identifier.length() > 16) {
            return this.getUniqueIdentifier(text);
        }
        this.identifiers.add(identifier);
        return identifier;
    }
    
    private static String getRandomChatColor() {
        return ChatColor.values()[ThreadLocalRandom.current().nextInt(ChatColor.values().length)].toString();
    }
    
    public List<FrameBoardEntry> getEntries() {
        return this.entries;
    }
    
    public List<String> getIdentifiers() {
        return this.identifiers;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public Objective getObjective() {
        return this.objective;
    }
}
