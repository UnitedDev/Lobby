package fr.kohei.lobby.manager;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.AnimationModifier;
import com.github.juliarn.npc.modifier.MetadataModifier;
import fr.kohei.lobby.manager.npc.CustomNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NPCManager implements Listener {
    private final NPCPool npcPool;

    public NPCManager(Plugin plugin) {
        this.npcPool = NPCPool.builder(plugin)
                .spawnDistance(60)
                .actionDistance(30)
                .tabListRemoveTicks(20)
                .build();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.addNPCs();
    }

    public void addNPCs() {
        for (CustomNPC value : CustomNPC.values()) {
            NPC.builder()
                    .profile(value.getProfile())
                    .location(value.getLobbyLocation().getLocation())
                    .imitatePlayer(false)
                    .lookAtPlayer(false)
                    .build(this.npcPool);
        }
    }

    @EventHandler
    public void handleNPCShow(PlayerNPCShowEvent event) {
        NPC npc = event.getNPC();

        event.send(
                npc.animation().queue(AnimationModifier.EntityAnimation.SWING_MAIN_ARM),
                npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true)
        );

        Player player = event.getPlayer();
        Scoreboard scoreboard = player.getScoreboard();

        Team team;
        if (scoreboard.getTeam(npc.getProfile().getName()) == null) {
            team = scoreboard.registerNewTeam(npc.getProfile().getName());
        } else {
            team = scoreboard.getTeam(npc.getProfile().getName());
        }

        team.setNameTagVisibility(NameTagVisibility.NEVER);

        team.addEntry(npc.getProfile().getName());
    }

    @EventHandler
    public void handleNPCInteract(PlayerNPCInteractEvent event) {
        NPC npc = event.getNPC();
        CustomNPC customNPC = CustomNPC.getByNPC(npc);
        if (customNPC != null) {
            customNPC.getEvent().accept(event);
            event.send(npc.animation().queue(AnimationModifier.EntityAnimation.SWING_MAIN_ARM));
        }
    }
}
