package fr.kohei.lobby.manager;

import fr.kohei.lobby.Main;
import fr.kohei.BukkitAPI;
import fr.kohei.utils.ChatUtil;
import fr.kohei.utils.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Getter
@SuppressWarnings("unchecked")
public class JumpManager {

    private final Main main;
    private final List<Location> checkpoints;
    private Location jumpStart;
    private Location jumpEnd;
    private final HashMap<String, Long> leaderboard;
    private List<ArmorStand> armorStands;

    public JumpManager(Main main) {
        this.main = main;
        this.main.saveConfig();

        this.checkpoints = (List<Location>) Main.getInstance().getConfig().get("checkpoints");
        this.jumpStart = (Location) Main.getInstance().getConfig().get("jump-start");
        this.jumpEnd = (Location) Main.getInstance().getConfig().get("jump-end");
        this.leaderboard = new HashMap<>();
        this.armorStands = new ArrayList<>();

//        for (final Document document : Core.getProfileManager().getProfileCollection().find().filter(Filters.not(Filters.eq("bestParkourTime", -1L))).sort(Sorts.ascending("bestParkourTime")).limit(10)) {
//            String name = document.getString("dname");
//            Long value = document.getLong("bestParkourTime");
//            leaderboard.put(name, value);
//        }

        List<String> str = new ArrayList<>(this.leaderboard.keySet());
        str.sort(Comparator.comparing(this.leaderboard::get));

        Bukkit.getScheduler().runTaskLater(main, () -> {
            double x = 0.5;
            double y = 97;
            double z = -34.5;
            int place = 1;

            World world = Bukkit.getWorld("world");
            ArmorStand title = (ArmorStand) Bukkit.getWorld("world").spawnEntity(new Location(world, x, y, z), EntityType.ARMOR_STAND);
            title.setVisible(false);
            title.setCustomName("§cClassement §8┃ §fParkour");
            title.setGravity(false);
            title.setCustomNameVisible(true);
            this.armorStands.add(title);

            y -= 0.27;

            ArmorStand line = (ArmorStand) Bukkit.getWorld("world").spawnEntity(new Location(world, x, y, z), EntityType.ARMOR_STAND);
            line.setVisible(false);
            line.setCustomName(ChatUtil.translate("&7&m--------------------"));
            line.setGravity(false);
            line.setCustomNameVisible(true);
            this.armorStands.add(line);

            for (String name : str) {
                Long value = leaderboard.get(name);
                y -= 0.27;

                ArmorStand board = (ArmorStand) Bukkit.getWorld("world").spawnEntity(new Location(world, x, y, z), EntityType.ARMOR_STAND);
                board.setVisible(false);
                board.setCustomName(ChatUtil.translate("&c" + place + " &8» &c" + name + "&8: &7" + TimeUtil.niceTime(value)));
                board.setGravity(false);
                board.setCustomNameVisible(true);
                this.armorStands.add(board);
                place++;
            }

            y -= 0.27;

            ArmorStand line2 = (ArmorStand) Bukkit.getWorld("world").spawnEntity(new Location(world, x, y, z), EntityType.ARMOR_STAND);
            line2.setVisible(false);
            line2.setCustomName(ChatUtil.translate("&7&m--------------------"));
            line2.setGravity(false);
            line2.setCustomNameVisible(true);
            this.armorStands.add(line2);
        }, 10 * 20);
    }

    public List<Location> getCheckpoints() {
        return checkpoints;
    }

    public void updateCheckpoints() {
        Main.getInstance().getConfig().set("checkpoints", this.checkpoints);
        Main.getInstance().saveConfig();
    }

    public Location getJumpStart() {
        return jumpStart;
    }

    public void setJumpStart(Location jumpStart) {
        this.jumpStart = jumpStart;
        Main.getInstance().getConfig().set("jump-start", jumpStart);
        Main.getInstance().saveConfig();
    }

    public Location getJumpEnd() {
        return jumpEnd;
    }

    public void setJumpEnd(Location jumpEnd) {
        this.jumpEnd = jumpEnd;
        Main.getInstance().getConfig().set("jump-end", jumpEnd);
        Main.getInstance().saveConfig();
    }
}
