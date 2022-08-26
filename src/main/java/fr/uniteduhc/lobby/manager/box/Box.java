package fr.uniteduhc.lobby.manager.box;

import fr.uniteduhc.utils.Heads;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public enum Box {

    COINS_1(Heads.LUCKY.toItemStack(), "&e200 Coins", 500),
    GRADE_1(Heads.DEKU.toItemStack(), "&cGrade Vilain &f(&77d&f)", 300),
    COINS_2(Heads.LUCKY.toItemStack(), "&e1500 Coins", 50),
    GRADE_2(Heads.SASUKE.toItemStack(), "&cGrade Héros", 50),
    GRADE_3(Heads.LUFFY.toItemStack(), "&cGrade Mugiwara &f(&73d&f)", 100),
    GRADE_4(Heads.LUFFY.toItemStack(), "&cGrade Mugiwara &f(&77d&f)", 30),

    // IRL

    CARTE(Heads.CHEST_MINECART.toItemStack(), "&aCarte Cadeau &f(&750e&f)", 1),
    ECRAN(Heads.SHOP.toItemStack(), "&bEcran &f(&7250e&f)", 1),
    CLAVIER(Heads.COAL_CHEST.toItemStack(), "&5Clavier &f(&7150e&f)", 1),
    SOURIS(Heads.SPECIAL_CHEST.toItemStack(), "&eSouris &f(&780e&f)", 1),
    ;

    private final ItemStack display;
    private final String name;
    private final int percentage;
}
