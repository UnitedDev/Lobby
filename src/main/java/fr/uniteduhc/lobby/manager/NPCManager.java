package fr.uniteduhc.lobby.manager;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.AnimationModifier;
import com.github.juliarn.npc.modifier.MetadataModifier;
import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.lobby.manager.npc.CustomNPC;
import fr.uniteduhc.lobby.utils.other.LobbyLocation;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

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

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            LobbyLocation lobbyLocation = CustomNPC.getByNPC(npc).getLobbyLocation();
            npc.rotation().queueRotate(lobbyLocation.getYaw(), lobbyLocation.getPitch()).send(event.getPlayer());
        }, 10);

        Player player = event.getPlayer();
        Scoreboard scoreboard = ((CraftScoreboard) player.getScoreboard()).getHandle();

        ScoreboardTeam scoreboardTeam = scoreboard.getTeam(npc.getProfile().getName()) == null ? new ScoreboardTeam(scoreboard, npc.getProfile().getName()) : scoreboard.getTeam(npc.getProfile().getName());
        scoreboardTeam.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);

        scoreboardTeam.getPlayerNameSet().add(npc.getProfile().getName());
        scoreboard.addPlayerToTeam(npc.getProfile().getName(), scoreboardTeam.getName());

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 1));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 0));
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
