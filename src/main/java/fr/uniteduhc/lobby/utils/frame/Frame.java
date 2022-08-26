package fr.uniteduhc.lobby.utils.frame;

import fr.uniteduhc.utils.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class Frame
{
    private static Frame instance;
    private final JavaPlugin plugin;
    private final FrameAdapter adapter;
    private final Map<UUID, FrameBoard> boards;

    public Frame(final JavaPlugin plugin, final FrameAdapter adapter) {
        if (Frame.instance != null) {
            throw new RuntimeException("Frame a déjà été lancé !");
        }
        Frame.instance = this;
        this.plugin = plugin;
        this.adapter = adapter;
        this.boards = new HashMap<>();
        this.setup();
    }

    private void setup() {

        this.plugin.getServer().getPluginManager().registerEvents(new FrameListener(), this.plugin);
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            if(titleIndex + 1 >= title.size()) titleIndex = 0;
            else titleIndex++;

            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                try {
                    FrameBoard board = this.boards.get(player.getUniqueId());
                    if (board == null)
                        continue;
                    Scoreboard scoreboard = board.getScoreboard();
                    Objective objective = board.getObjective();
                    String title = ChatUtil.translate(this.title.get(titleIndex));
                    if (!objective.getDisplayName().equals(title))
                        objective.setDisplayName(title);
                    List<String> newLines = this.adapter.getLines(player);
                    if (newLines == null || newLines.isEmpty()) {
                        board.getEntries().forEach(FrameBoardEntry::remove);
                        board.getEntries().clear();
                    } else {
                        Collections.reverse(newLines);
                        if (board.getEntries().size() > newLines.size())
                            for (int j = newLines.size(); j < board.getEntries().size(); j++) {
                                FrameBoardEntry entry = board.getEntryAtPosition(j);
                                if (entry != null)
                                    entry.remove();
                            }
                        for (int i = 0; i < newLines.size(); i++) {
                            FrameBoardEntry entry = board.getEntryAtPosition(i);
                            String line = ChatColor.translateAlternateColorCodes('&', newLines.get(i));
                            if (entry == null)
                                entry = new FrameBoardEntry(board, line);
                            entry.setText(line);
                            entry.setup();
                            entry.send(i);
                        }
                    }
                    player.setScoreboard(scoreboard);
                } catch (Exception ignored) {}
            }
            if (((ScoreboardAdapter) this.getAdapter()).index  + 1 >= ((ScoreboardAdapter) this.getAdapter()).ip.size()) ((ScoreboardAdapter) this.getAdapter()).index  = 0;
            else ((ScoreboardAdapter) this.getAdapter()).index ++;

        }, 1L, 3L);
    }
    
    public JavaPlugin getPlugin() {
        return this.plugin;
    }
    
    public FrameAdapter getAdapter() {
        return this.adapter;
    }
    
    public Map<UUID, FrameBoard> getBoards() {
        return this.boards;
    }
    
    public static Frame getInstance() {
        return Frame.instance;
    }

    public List<String> title = Arrays.asList(
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&6&lUNITED",
            "&c&lU&6&LNITED",
            "&c&lUN&6&LITED",
            "&c&lUNI&6&LTED",
            "&c&lUNIT&6&LD",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&c&lUNITED",
            "&6&lU&c&lNITED",
            "&6&lUN&c&lITED",
            "&6&lUNI&c&lTED",
            "&6&lUNIT&c&lED",
            "&6&lUNITE&c&lD",
            "&6&lUNITED"
    );
    int titleIndex = 0;

}
