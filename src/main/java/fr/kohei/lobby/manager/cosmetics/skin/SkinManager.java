package fr.kohei.lobby.manager.cosmetics.skin;

import com.mojang.authlib.GameProfile;
import fr.kohei.lobby.Main;
import fr.kohei.lobby.manager.CosmeticManager;
import fr.kohei.lobby.manager.cosmetics.CosmeticType;
import fr.kohei.lobby.manager.player.LobbyProfileData;
import fr.kohei.lobby.utils.other.SkinUtil;
import fr.kohei.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkinManager implements CosmeticType {
    private final CosmeticManager cosmeticManager;

    public SkinManager(CosmeticManager cosmeticManager) {
        this.cosmeticManager = cosmeticManager;
    }

    public void setSkin(Player player, SkinCosmetic skin) {
        LobbyProfileData lobbyProfileData = new LobbyProfileData(player);

        if (lobbyProfileData.getSkinCosmetic() != null && lobbyProfileData.getSkinCosmetic().equals(skin)) {
            player.sendMessage(ChatUtil.prefix("&cVous avez déjà cette tenue."));
            return;
        }

        this.updateGameProfile(player, skin);
        lobbyProfileData.setSkin(skin.name());
    }

    public void onJoin(Player player) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            LobbyProfileData lobbyProfileData = new LobbyProfileData(player);
            if (lobbyProfileData.getSkinCosmetic() != null) {
                this.updateGameProfile(player, lobbyProfileData.getSkinCosmetic());
            }
        }, 10);
    }

    public void updateGameProfile(Player player, SkinCosmetic skin) {
        GameProfile gameProfile = SkinUtil.GameProfileUtil.getSkinFromSignature(skin.name(), skin.getSkin().getValue(), skin.getSkin().getSignature());
        SkinUtil.setSkin(player, gameProfile, Main.getInstance());
    }

    @Override
    public String getName() {
        return "Skins";
    }
}
