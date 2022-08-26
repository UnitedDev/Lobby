package fr.uniteduhc.lobby.manager.cosmetics.skin;

import fr.uniteduhc.lobby.manager.cosmetics.CosmeticRarity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkinCosmetic {
    DEKU("Deku", CosmeticRarity.LEGENDAIRE, 2500, SkinType.DEKU),
    BAKUGO("Bakugo", CosmeticRarity.EPIQUE, 1500, SkinType.BAKUGO),
    ALL_MIGHT("All Might", CosmeticRarity.COMMUN, 750, SkinType.ALL_MIGHT),
    ALL_FOR_ONE("All For One", CosmeticRarity.RARE, 750, SkinType.ALL_FOR_ONE),
    SHOTO("Shoto", CosmeticRarity.EPIQUE, 1500, SkinType.SHOTO),

    LUFFY("Luffy", CosmeticRarity.LEGENDAIRE, 2500, SkinType.LUFFY),
    ZORO("Zoro", CosmeticRarity.EPIQUE, 1500, SkinType.ZORO),
    NAMI("Nami", CosmeticRarity.RARE, 750, SkinType.NAMI),

    IFAYROX("iFayRox", CosmeticRarity.RARE, 750, SkinType.IFAYROX),
    SHOTOW("Shot0w", CosmeticRarity.RARE, 750, SkinType.SHOTOW),
    NATOM("Natom59", CosmeticRarity.COMMUN, 500, SkinType.NATOM),
    PIKA("pika0201", CosmeticRarity.COMMUN, 500, SkinType.PIKA),
    RHODLESS("Rhodless", CosmeticRarity.COMMUN, 500, SkinType.RHODLESS);

    private final String name;
    private final CosmeticRarity rarity;
    private final int price;
    private final SkinType skin;
}
