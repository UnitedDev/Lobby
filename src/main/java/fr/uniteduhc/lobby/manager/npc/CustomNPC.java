package fr.uniteduhc.lobby.manager.npc;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.profile.Profile;
import fr.uniteduhc.common.cache.server.impl.UHCServer;
import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.lobby.manager.npc.count.NPCCount;
import fr.uniteduhc.lobby.manager.npc.count.impl.*;
import fr.uniteduhc.lobby.menu.AllServersSelectorMenu;
import fr.uniteduhc.lobby.menu.ChooseGameMenu;
import fr.uniteduhc.lobby.menu.UHCGamemodeMenu;
import fr.uniteduhc.lobby.utils.other.LobbyLocation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public enum CustomNPC {
    CREATE_SERVER(
            "&d&lCREER UN SERVEUR",
            SkinSignature.CREATE_SERVER,
            new LobbyLocation(-12.5, 46, -17.5, 22, 0),
            null,
            event -> new ChooseGameMenu(null).openMenu(event.getPlayer())
    ),
    SERVER_LIST(
            "&a&lSERVEURS",
            SkinSignature.SERVER_LIST,
            new LobbyLocation(-6.5, 46, -14.5, 45, 0),
            new GlobalCount(),
            event -> new AllServersSelectorMenu(null).openMenu(event.getPlayer())
    ),
    SOON(
            "&b&lBIENTOT",
            SkinSignature.SOON,
            new LobbyLocation(-12.5, 46, 18.5, 155, 0),
            null,
            event -> {
            }
    ),
    MUGIWARA_UHC(
            "&c&lMUGIWARA UHC",
            SkinSignature.MUGIWARA_UHC,
            new LobbyLocation(-2.5, 46, -7.5, 70, 0),
            new MugiwaraCount(),
            event -> new UHCGamemodeMenu(null, UHCServer.ServerType.MUGIWARA).openMenu(event.getPlayer())
    ),
    MHA_UHC(
            "&c&lMHA UHC",
            SkinSignature.MHA_UHC,
            new LobbyLocation(-2.5, 46, 8.5, 115, 0),
            new MHACount(),
            event -> new UHCGamemodeMenu(null, UHCServer.ServerType.MHA).openMenu(event.getPlayer())
    ),
    TERRAIN_GAMMA(
            "&b&lTERRAIN GAMMA",
            SkinSignature.TERRAIN_GAMMA,
            new LobbyLocation(-6.5, 46, 14.5, 135, 0),
            new TerrainGammaCount(),
            event -> {
            }
    )

    ;

    private ArmorStand countHologram;

    private final String display;
    private final SkinSignature signature;
    private final LobbyLocation lobbyLocation;
    private final NPCCount count;
    private final Consumer<PlayerNPCInteractEvent> event;

    public static CustomNPC getByNPC(NPC npc) {
        return Arrays.stream(values())
                .filter(cNpc -> cNpc.name().equalsIgnoreCase(npc.getProfile().getName()))
                .findFirst()
                .orElse(null);
    }

    public Profile getProfile() {
        Profile profile = new Profile(UUID.randomUUID(),
                Collections.singletonList(
                        new Profile.Property("textures", signature.getValue(), signature.getSignature())
                ));
        profile.complete();
        profile.setName(name().toLowerCase());

        this.setupHolograms();
        return profile;
    }

    private void setupHolograms() {
        double y = 0.33;
        if (count != null) y += 0.30;

        ArmorStand title = lobbyLocation.getWorld().spawn(lobbyLocation.getLocation().clone().add(0, y, 0), ArmorStand.class);
        title.setVisible(false);
        title.setGravity(false);
        title.setCustomName(display.replace("&", "§"));
        title.setCustomNameVisible(true);

        if (count != null) {
            countHologram = lobbyLocation.getWorld().spawn(lobbyLocation.getLocation().clone().add(0, 0.33, 0), ArmorStand.class);
            updateCount(countHologram);

            new BukkitRunnable() {
                @Override
                public void run() {
                    countHologram.remove();

                    countHologram = lobbyLocation.getWorld().spawn(lobbyLocation.getLocation().clone().add(0, 0.33, 0), ArmorStand.class);
                    updateCount(countHologram);
                }
            }.runTaskTimer(Main.getInstance(), 15 * 20, 15 * 20);
        }

        ArmorStand click = lobbyLocation.getWorld().spawn(lobbyLocation.getLocation(), ArmorStand.class);
        click.setVisible(false);
        click.setGravity(false);
        click.setCustomName("&f&l» &e&lCLIQUEZ-ICI &f&l«".replace("&", "§"));
        click.setCustomNameVisible(true);
    }

    private void updateCount(ArmorStand armorStand) {
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomName("§7" + this.count.getCount() + " joueurs");
        armorStand.setCustomNameVisible(true);
    }
}
