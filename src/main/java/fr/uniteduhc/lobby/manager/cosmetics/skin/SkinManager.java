package fr.uniteduhc.lobby.manager.cosmetics.skin;

import com.mojang.authlib.GameProfile;
import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.common.cache.data.ProfileData;
import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.lobby.manager.CosmeticManager;
import fr.uniteduhc.lobby.manager.cosmetics.CosmeticType;
import fr.uniteduhc.lobby.manager.player.LobbyProfileData;
import fr.uniteduhc.lobby.utils.other.SkinUtil;
import fr.uniteduhc.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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

    public void setupProfile(ProfileData profile, Player player) {
        if (!profile.getServersData().containsKey("skin"))
            profile.getServersData().put("skin", "aucun");
        else
            Main.getInstance().getCosmeticManager().getSkinManager().onJoin(player);

        if (!profile.getServersData().containsKey("pet"))
            profile.getServersData().put("pet", "aucun");

        if (!profile.getServersData().containsKey("fly"))
            profile.getServersData().put("fly", "aucun");

        if (!profile.getServersData().containsKey("bought_skins"))
            profile.getServersData().put("bought_skins", new ArrayList<>());

        if (!profile.getServersData().containsKey("bought_pets"))
            profile.getServersData().put("bought_pets", new ArrayList<>());

        BukkitAPI.getCommonAPI().saveProfile(player.getUniqueId(), profile);
    }

    @Override
    public String getName() {
        return "Skins";
    }
}
