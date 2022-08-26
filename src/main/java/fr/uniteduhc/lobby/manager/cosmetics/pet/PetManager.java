package fr.uniteduhc.lobby.manager.cosmetics.pet;

import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.lobby.manager.CosmeticManager;
import fr.uniteduhc.lobby.manager.cosmetics.CosmeticType;
import fr.uniteduhc.menu.Menu;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Getter
public class PetManager implements CosmeticType {

    private final CosmeticManager cosmeticManager;
    private final HashMap<Player, ArmorStand> entityPets;

    public PetManager(CosmeticManager cosmeticManager) {
        this.cosmeticManager = cosmeticManager;
        this.entityPets = new HashMap<>();

        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::updatePets, 0, 1);
    }

    public void updatePets() {

    }

    @Override
    public String getName() {
        return "Pets";
    }
}
