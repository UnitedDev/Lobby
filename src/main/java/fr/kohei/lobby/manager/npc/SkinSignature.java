package fr.kohei.lobby.manager.npc;

import fr.kohei.utils.Heads;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum SkinSignature {
    CREATE_SERVER("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGI0MTBhOGE3ZWY3YTYxY2FlMDkwMWJjMzFkNDlmMTQ0YWUyZjIyZmNhMjVmNDc2YmMzYjZmYWM4ZDhiOThkZCJ9fX0="),
    SERVER_LIST("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVlMmM1OTA0MDU4YjA1MGUwNjJiNDU5MGIwODRkNjc2MWJmZmViN2NmYmYxY2RhM2ViODViOTNlNTlkNWE0YSJ9fX0="),
    SOON("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQyYTY4NTU2YWRiMzBlNGM0MzAyZTYwMmE1M2ZmZTg4YmU5NTk0N2EwYjMyMTRiZjliY2ZhNzZlMzFkOGM0YSJ9fX0="),
    MUGIWARA_UHC(Heads.LUFFY.getBase()),
    MHA_UHC(Heads.DEKU.getBase()),
    TERRAIN_GAMMA("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTJhMzMzOTM1YTk2NmNhZWZlNzU4NTAyZjM4ZjUwNzliNzExZGUwYmM4ZjRkNTE5NDU4ZGI5OWNmNmFhNWExZiJ9fX0=");

    private final String signature;
}
