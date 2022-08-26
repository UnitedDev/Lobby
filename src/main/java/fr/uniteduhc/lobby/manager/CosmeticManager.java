package fr.uniteduhc.lobby.manager;

import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.lobby.manager.cosmetics.pet.PetManager;
import fr.uniteduhc.lobby.manager.cosmetics.skin.SkinManager;
import lombok.Getter;

@Getter
public class CosmeticManager {

    private final Main main;
    private final PetManager petManager;
    private final SkinManager skinManager;

    public CosmeticManager(Main main) {
        this.main = main;

        this.skinManager = new SkinManager(this);
        this.petManager = new PetManager(this);
    }

}
