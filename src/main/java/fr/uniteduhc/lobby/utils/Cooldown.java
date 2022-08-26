package fr.uniteduhc.lobby.utils;

import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.utils.ChatUtil;
import fr.uniteduhc.utils.TimeUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@Getter
@RequiredArgsConstructor
public class Cooldown {

    private final String name;
    private BukkitTask runnable;
    private int seconds;

    public void setCooldown(int seconds) {
        this.seconds = seconds;
    }

    public void task() {
        if (runnable != null)
            runnable.cancel();
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (seconds > 0) onSecond();
                else cancel();
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    public void onSecond() {
        this.seconds -= 1;
    }

    public String getCooldownMessage() {
        return ChatUtil.prefix("&cVous ne pouvez pas utiliser cette capacité. Elle sera de nouveau disponible dans " + TimeUtil.getReallyNiceTime2(this.getSeconds() * 1000L));
    }

    public boolean isCooldown(Player player) {
        if (this.getSeconds() <= 0) return false;

        player.sendMessage(getCooldownMessage());

        return true;
    }
    public boolean isCooldownNoMessage(Player player) {
        return this.getSeconds() > 0;
    }

}
