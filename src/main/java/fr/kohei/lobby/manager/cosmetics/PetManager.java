package fr.kohei.lobby.manager.cosmetics;

import fr.kohei.lobby.Main;
import fr.kohei.lobby.manager.CosmeticManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Getter
public class PetManager {

    private final CosmeticManager cosmeticManager;
    private final HashMap<Player, ArmorStand> entityPets;

    public PetManager(CosmeticManager cosmeticManager) {
        this.cosmeticManager = cosmeticManager;
        this.entityPets = new HashMap<>();

        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::updatePets, 0, 1);
    }

    public void updatePets() {

    }
}
