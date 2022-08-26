package fr.uniteduhc.lobby.manager.player;

import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.common.cache.data.ProfileData;
import fr.uniteduhc.lobby.manager.cosmetics.skin.SkinCosmetic;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class LobbyProfileData {
    private final UUID uuid;

    private List<String> boughtSkins;
    private String skin;

    private List<String> boughtPets;
    private String pet;

    private String fly;
    private String gadget;

    public LobbyProfileData(Player player) {
        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());
        this.uuid = player.getUniqueId();

        this.boughtSkins = (List<String>) profile.getServersData().get("bought_skins");
        this.skin = (String) profile.getServersData().get("skin");

        this.boughtPets = (List<String>) profile.getServersData().get("bought_pets");
        this.pet = (String) profile.getServersData().get("pet");

        this.fly = (String) profile.getServersData().get("fly");
        this.gadget = (String) profile.getServersData().get("gadget");
    }

    public void setBoughtSkins(List<String> boughtSkins) {
        this.boughtSkins = boughtSkins;

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(uuid);
        profile.getServersData().put("bought_skins", boughtSkins);
        BukkitAPI.getCommonAPI().saveProfile(uuid, profile);
    }

    public void setSkin(String skin) {
        this.skin = skin;

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(uuid);
        profile.getServersData().put("skin", skin);
        BukkitAPI.getCommonAPI().saveProfile(uuid, profile);
    }

    public void setGadget(String gadget) {
        this.gadget = gadget;

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(uuid);
        profile.getServersData().put("gadget", gadget);
        BukkitAPI.getCommonAPI().saveProfile(uuid, profile);
    }

    public void setFly(String fly) {
        this.fly = fly;

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(uuid);
        profile.getServersData().put("fly", fly);
        BukkitAPI.getCommonAPI().saveProfile(uuid, profile);
    }

    public void setBoughtPets(List<String> boughtPets) {
        this.boughtPets = boughtPets;

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(uuid);
        profile.getServersData().put("bought_pets", boughtSkins);
        BukkitAPI.getCommonAPI().saveProfile(uuid, profile);
    }

    public void setPet(String pet) {
        this.pet = pet;

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(uuid);
        profile.getServersData().put("pet", skin);
        BukkitAPI.getCommonAPI().saveProfile(uuid, profile);
    }

    public SkinCosmetic getSkinCosmetic() {
        return Arrays.stream(SkinCosmetic.values()).filter(skinType -> skinType.name().equals(skin))
                .findFirst().orElse(null);
    }
}
