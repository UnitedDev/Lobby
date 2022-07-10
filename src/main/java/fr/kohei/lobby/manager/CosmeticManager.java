package fr.kohei.lobby.manager;

import fr.kohei.lobby.Main;
import fr.kohei.lobby.manager.cosmetics.pet.PetManager;
import fr.kohei.lobby.manager.cosmetics.skin.SkinManager;
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
