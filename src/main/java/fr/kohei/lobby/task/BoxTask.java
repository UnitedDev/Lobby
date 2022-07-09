package fr.kohei.lobby.task;

import fr.kohei.lobby.Main;
import fr.kohei.lobby.utils.MathUtil;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class BoxTask extends BukkitRunnable {
    private final Location box;

    public BoxTask(Main main) {
        World world = Bukkit.getWorld("world");
        box = new Location(world, -0.5, 46, 0.5);
        box.getBlock().setType(Material.ENDER_PORTAL_FRAME);

        ArmorStand click = (ArmorStand) Bukkit.getWorld("world").spawnEntity(box.clone().add(0, -0.5, 0), EntityType.ARMOR_STAND);
        click.setVisible(false);
        click.setCustomName("§f§l» §e§lCLIQUEZ-ICI §f§l«");
        click.setGravity(false);
        click.setCustomNameVisible(true);

        ArmorStand title = (ArmorStand) Bukkit.getWorld("world").spawnEntity(box.clone().add(0, -0.22, 0), EntityType.ARMOR_STAND);
        title.setVisible(false);
        title.setCustomName("§c§lKOHEI BOX");
        title.setGravity(false);
        title.setCustomNameVisible(true);
    }

    @Override
    public void run() {
        MathUtil.sendCircleParticle(EnumParticle.FLAME, box.clone().add(0, -0.25, 0), 1, 40);
    }
}
