package fr.kohei.lobby.manager;

import fr.kohei.lobby.Main;
import fr.kohei.lobby.manager.cosmetics.PetManager;
import lombok.Getter;

@Getter
public class CosmeticManager {

    private final Main main;
    private final PetManager petManager;

    public CosmeticManager(Main main) {
        this.main = main;

        this.petManager = new PetManager(this);
    }

}
