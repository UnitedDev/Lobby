package fr.kohei.lobby.manager.npc.count.impl;

import fr.kohei.BukkitAPI;
import fr.kohei.common.cache.server.impl.UHCServer;
import fr.kohei.lobby.manager.npc.count.NPCCount;

import java.util.Map;

public class MugiwaraCount extends NPCCount {
    @Override
    public int getCount() {
        int count = 0;

        for (Map.Entry<Integer, UHCServer> entry : BukkitAPI.getCommonAPI().getServerCache().getUhcServers().entrySet()) {
            UHCServer uhcServer = entry.getValue();
            if(uhcServer.getType() != UHCServer.ServerType.MUGIWARA) continue;

            count += uhcServer.getPlayers();
        }

        return count;
    }
}
