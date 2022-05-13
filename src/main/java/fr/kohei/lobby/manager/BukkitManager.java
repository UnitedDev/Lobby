package fr.kohei.lobby.manager;

import fr.kohei.lobby.Main;
import fr.kohei.lobby.commands.JumpCommands;
import fr.kohei.lobby.listeners.PlayerListeners;
import fr.kohei.BukkitAPI;
import fr.kohei.utils.TimeUtil;
import org.bukkit.plugin.PluginManager;

public class BukkitManager {

    private final Main main;

    public BukkitManager(Main main) {
        this.main = main;

        this.registerListeners();
        this.loadCommands();
    }

    public void registerListeners() {
        PluginManager pluginManager = main.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerListeners(main), main);
    }

    public void loadCommands() {
        BukkitAPI.getCommandHandler().registerClass(JumpCommands.class);
    }
}
