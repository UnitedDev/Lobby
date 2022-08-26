package fr.uniteduhc.lobby.manager.npc.count;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class NPCCount {
    public abstract int getCount();
}
