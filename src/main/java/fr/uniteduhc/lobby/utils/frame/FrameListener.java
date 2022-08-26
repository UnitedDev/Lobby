package fr.uniteduhc.lobby.utils.frame;

import fr.uniteduhc.lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FrameListener implements Listener
{
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> Frame.getInstance().getBoards().put(event.getPlayer().getUniqueId(), new FrameBoard(event.getPlayer())), 20);
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Frame.getInstance().getBoards().remove(event.getPlayer().getUniqueId());
    }
}
