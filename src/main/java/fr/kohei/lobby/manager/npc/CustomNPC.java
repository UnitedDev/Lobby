package fr.kohei.lobby.manager.npc;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.profile.Profile;
import fr.kohei.common.cache.server.impl.UHCServer;
import fr.kohei.lobby.menu.AllServersSelectorMenu;
import fr.kohei.lobby.menu.ChooseGameMenu;
import fr.kohei.lobby.menu.UHCGamemodeMenu;
import fr.kohei.lobby.utils.LobbyLocation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public enum CustomNPC {
    CREATE_SERVER(
            "&d&lCREER UN SERVEUR",
            "a73a51e7-a87f-44b0-8421-76229f367d95",
            new LobbyLocation(-12.5, 46, -17.5, 22, 0),
            event -> new ChooseGameMenu(null).openMenu(event.getPlayer())
    ),
    SERVER_LIST(
            "&a&lSERVEURS",
            "d1df6061-901a-44ec-811c-c7e9dc1ed1ee",
            new LobbyLocation(-12.5, 46, 18.5, 155, 0),
            event -> new AllServersSelectorMenu(null).openMenu(event.getPlayer())
    ),
    SOON(
            "&b&lBIENTOT",
            "640a5372-780b-4c2a-b7e7-8359d2f9a6a8",
            new LobbyLocation(-6.5, 46, -14.5, 45, 0),
            event -> {
            }
    ),
    MUGIWARA_UHC(
            "&c&lMUGIWARA UHC",
            "e3dabc84-cb48-47cd-8004-a83eb30ba53f",
            new LobbyLocation(-2.5, 46, -7.5, 70, 0),
            event -> new UHCGamemodeMenu(null, UHCServer.ServerType.MUGIWARA).openMenu(event.getPlayer())
    ),
    MHA_UHC(
            "&c&lMHA UHC",
            "07b29e12-f9e7-4541-b6ad-727e6fc68de2",
            new LobbyLocation(-2.5, 46, 8.5, 115, 0),
            event -> new UHCGamemodeMenu(null, UHCServer.ServerType.MHA).openMenu(event.getPlayer())
    ),
    TERRAIN_GAMMA(
            "&b&lTERRAIN GAMMA",
            "6a0fa77b-1f50-496b-9ae5-227b5a8e2a0d",
            new LobbyLocation(-6.5, 46, 14.5, 135, 0),
            event -> {
            }
    );

    private final String display;
    private final String signature;
    private final LobbyLocation lobbyLocation;
    private final Consumer<PlayerNPCInteractEvent> event;

    public static CustomNPC getByNPC(NPC npc) {
        return Arrays.stream(values())
                .filter(cNpc -> cNpc.name().equalsIgnoreCase(npc.getProfile().getName()))
                .findFirst()
                .orElse(null);
    }

    public Profile getProfile() {
        Profile profile = new Profile(UUID.fromString(signature));
        profile.complete();

        profile.setName(name().toLowerCase());
        return profile;
    }
}
