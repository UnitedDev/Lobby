package fr.uniteduhc.lobby.manager;

import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.lobby.manager.jump.JumpCommands;
import fr.uniteduhc.lobby.manager.listeners.PlayerListeners;
import fr.uniteduhc.BukkitAPI;
import org.bukkit.plugin.PluginManager;

public class BukkitManager {

    private final Main main;

    public BukkitManager(Main main) {
        this.main = main;

        this.registerListeners();
        this.loadCommands();
    }

    public void registerListeners() {
        PluginManager pluginManager = Main.getInstance().getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerListeners(), main);
    }

    public void loadCommands() {
        BukkitAPI.getCommandHandler().registerClass(JumpCommands.class);
    }
}
