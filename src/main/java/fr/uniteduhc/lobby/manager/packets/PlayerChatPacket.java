package fr.uniteduhc.lobby.manager.packets;

import fr.uniteduhc.common.utils.messaging.pigdin.Packet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerChatPacket implements Packet {

    private final UUID uuid;
    private final String message;
    private final boolean restricted;

}
