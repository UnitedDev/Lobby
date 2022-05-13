package fr.kohei.lobby.commands;

import fr.kohei.lobby.Main;
import fr.kohei.command.Command;
import fr.kohei.command.param.Param;
import fr.kohei.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class JumpCommands {

    @Command(names = {"jump addcheckpoint"}, power = 1000)
    public static void setCheckpoints(Player sender) {
        Main.getJumpManager().getCheckpoints().add(sender.getLocation());
        Main.getJumpManager().updateCheckpoints();
        sender.sendMessage(ChatUtil.prefix("&fVous avez changé la nouvelle position du &acheckpoint &fnuméro &a" + (Main.getJumpManager().getCheckpoints().size() + 1) + " &f!"));
    }

    @Command(names = {"jump setcheckpoint"}, power = 1000)
    public static void setCheckpoint(Player sender, @Param(name = "numéro") int number) {
        Main.getJumpManager().getCheckpoints().set(number - 1, sender.getLocation());
        Main.getJumpManager().updateCheckpoints();
        sender.sendMessage(ChatUtil.prefix("&fVous avez changé la nouvelle position du &acheckpoint &fnuméro &a" + number + " &f!"));
    }

    @Command(names = {"jump list"}, power = 1000)
    public static void getCheckpoints(Player sender) {
        Location start = Main.getJumpManager().getJumpStart();
        Location end = Main.getJumpManager().getJumpEnd();

        sender.sendMessage(" ");
        sender.sendMessage(ChatUtil.translate("&8» &cPoint de départ"));
        sender.sendMessage(ChatUtil.translate(" &7■ &f" + start.getBlockX() + ", " + start.getBlockY() + ", " + start.getBlockY()));
        sender.sendMessage(ChatUtil.translate("&8» &cCheckpoints"));
        for (Location checkpoint : Main.getJumpManager().getCheckpoints()) {
            sender.sendMessage(ChatUtil.translate(" &7■ &f" + checkpoint.getBlockX() + ", " + checkpoint.getBlockY() + ", " + checkpoint.getBlockY()));
        }
        sender.sendMessage(ChatUtil.translate("&8» &cFin du jump"));
        sender.sendMessage(ChatUtil.translate(" &7■ &f" + end.getBlockX() + ", " + end.getBlockY() + ", " + end.getBlockY()));
        sender.sendMessage(" ");
    }

    @Command(names = {"jump setstart"}, power = 1000)
    public static void setStart(Player sender) {
        Main.getJumpManager().setJumpStart(sender.getLocation());
        sender.sendMessage(ChatUtil.prefix("&fVous avez changé la nouvelle &aposition &fdu &apoint de départ&f."));
    }

    @Command(names = {"jump setend"}, power = 1000)
    public static void setEnd(Player sender) {
        Main.getJumpManager().setJumpEnd(sender.getLocation());
        sender.sendMessage(ChatUtil.prefix("&fVous avez changé la nouvelle &aposition &fdu &apoint de fin&f."));
    }

}
