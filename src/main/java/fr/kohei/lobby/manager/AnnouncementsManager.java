package fr.kohei.lobby.manager;

import fr.kohei.lobby.Main;
import fr.kohei.utils.ChatUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsManager {

    private final List<String> messages;
    private int index;

    public AnnouncementsManager(Plugin plugin) {
        this.messages = new ArrayList<>();

        messages.add("&fNous recrutons activement des &9&lModérateurs &fainsi que des &5&lGame-Designers&f, si tu es intéressé par un des postes suivants, rends toi sur le discord pour postuler.");
        messages.add("&fSi vous avez un problème contactez au plus vite un staff en jeu, la commande &d/stafflist &fvous permet d'afficher les staffs connectés. Sinon vous pouvez créer un ticket sur le discord.");
        messages.add("&cLe saviez vous ? &fLes Divisions sont un moyen d'avoir des tickets d'hosts, des coins et même des grades et ce &agratuitement &f! Il vous suffit de jouer pour gagner de l'exp.");
        messages.add("&fN'hésitez pas à nous suivre sur nos différents &bréseaux-sociaux &fpour pouvoir continuer à vous tenir au courant de l'actualité du serveur !");

        this.index = 0;

        this.startTask();
    }

    private void startTask() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if (index >= messages.size()) {
                index = 0;
            }

            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage(ChatUtil.prefix(messages.get(index)));
            if(index == 0) {
                TextComponent click = new TextComponent(ChatUtil.translate("&e&l[OUVRIR LE LIEN]"));
                click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(
                        ChatUtil.prefix("&cCliquez-ici pour y accéder."))}));
                click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/kohei"));
                Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(click));
            }
            Bukkit.broadcastMessage(" ");
            index++;
        }, 0, 90 * 20);
    }

}
