package fr.kohei.lobby.manager;

import fr.kohei.lobby.Main;
import fr.kohei.lobby.manager.jump.JumpCommands;
import fr.kohei.lobby.listeners.PlayerListeners;
import fr.kohei.BukkitAPI;
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
