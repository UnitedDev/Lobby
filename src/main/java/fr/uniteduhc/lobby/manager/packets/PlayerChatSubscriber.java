package fr.uniteduhc.lobby.manager.packets;

import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.common.cache.data.ProfileData;
import fr.uniteduhc.common.cache.server.impl.LobbyServer;
import fr.uniteduhc.common.utils.messaging.pigdin.IncomingPacketHandler;
import fr.uniteduhc.common.utils.messaging.pigdin.PacketListener;
import fr.uniteduhc.utils.ChatUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayerChatSubscriber implements PacketListener {
    @IncomingPacketHandler
    public void onPacketReceive(PlayerChatPacket packet) {

        LobbyServer lobbyServer = BukkitAPI.getCommonAPI().getServerCache().getLobbyServers().get(Bukkit.getPort());

        if (lobbyServer.isRestricted() != packet.isRestricted()) return;

        ProfileData profile = BukkitAPI.getCommonAPI().getProfile(packet.getUuid());

        String message = packet.getMessage();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (message.contains(onlinePlayer.getName())) {
                ProfileData targetProfile = BukkitAPI.getCommonAPI().getProfile(onlinePlayer.getUniqueId());
                if (targetProfile.isNotifications()) {
                    onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                }
                message = message.replace(onlinePlayer.getName(), "§b@" + onlinePlayer.getName() + "§f");
            }
        }

        TextComponent text = new TextComponent("§c⚠ ");
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatUtil.prefix("&cSignaler &l" + profile.getDisplayName()))}));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatreport confirm;name:" + profile.getDisplayName() + " message:" + message.replace("§b", "").replace("§f", "")));
        String finalMessage = message;

        Bukkit.getOnlinePlayers().forEach(player1 -> player1.spigot().sendMessage(text, new TextComponent(
                profile.getRank().getTabPrefix().replace("&", "§") + " " + profile.getDisplayName() + " §8§l» §f" + finalMessage
        )));
    }
}
