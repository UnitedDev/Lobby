package fr.uniteduhc.lobby.manager.cosmetics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CosmeticRarity {
    COMMUN("&a&lCOMMUN"),
    RARE("&e&lRARE"),
    EPIQUE("&5&lÉPIQUE"),
    LEGENDAIRE("&6&lLÉGENDAIRE");

    private final String name;
}
