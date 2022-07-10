package fr.kohei.lobby.menu;

import fr.kohei.lobby.Main;
import fr.kohei.lobby.manager.box.Box;
import fr.kohei.lobby.utils.firework.InstantFirework;
import fr.kohei.menu.Button;
import fr.kohei.menu.Menu;
import fr.kohei.menu.buttons.DisplayButton;
import fr.kohei.utils.ChatUtil;
import fr.kohei.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BoxOpenMenu extends Menu {
    private final boolean preview;
    private final List<Box> boxs;
    private int ticks;
    private final boolean end;

    public BoxOpenMenu(boolean preview, List<Box> boxs, int ticks, boolean end) {
        this.preview = preview;
        this.ticks = ticks;
        this.end = end;

        if (boxs == null) {
            this.boxs = new ArrayList<>();
            for (Box box : Box.values()) {
                for (int j = 0; j <= box.getPercentage() / 10L; j++) {
                    this.boxs.add(box);
                }
            }

            List<Box> random = Arrays.asList(Box.CARTE, Box.ECRAN, Box.CLAVIER, Box.SOURIS);
            Collections.shuffle(random);
            if (preview) {
                // do this action 5 times
                for (int i = 0; i < 7; i++) {
                    this.boxs.add(random.get(0));
                }
            } else {
                this.boxs.add(random.get(0));
            }

            Collections.shuffle(this.boxs);
        } else {
            this.boxs = boxs;
        }
    }

    @Override
    public String getTitle(Player player) {
        return "Ouverture";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            ItemBuilder builder = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(getFromBox(boxs.get(i))).setName(" ");
            if (i == 4) builder.addEnchant(Enchantment.DAMAGE_ALL, 1);
            builder.hideItemFlags();
            buttons.put(i, new DisplayButton(builder.toItemStack()));
        }

        for (int i = 18; i < 27; i++) {
            ItemBuilder builder = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(getFromBox(boxs.get(i - 18))).setName(" ");
            if (i == 22) builder.addEnchant(Enchantment.DAMAGE_ALL, 1);
            builder.hideItemFlags();

            buttons.put(i, new DisplayButton(builder.toItemStack()));
        }

        if (end) {
            buttons.put(13, new DisplayButton(new ItemBuilder(boxs.get(4).getDisplay()).setName(boxs.get(4).getName()).toItemStack()));
        } else {
            for (int i = 9; i < 18; i++) {
                buttons.put(i, new DisplayButton(new ItemBuilder(boxs.get(i - 9).getDisplay()).setName(boxs.get(i - 9).getName()).toItemStack()));
            }
        }

        if (boxs.size() == 40) {
            ticks = 2;
        } else if (boxs.size() == 30) {
            ticks = 4;
        } else if (boxs.size() == 20) {
            ticks = 7;
        } else if (boxs.size() == 15) {
            ticks = 15;
        } else if (boxs.size() == 13) {
            ticks = 25;
        } else if (boxs.size() == 11) {
            ticks = 40;
        }

        if (end) return buttons;

        if (this.boxs.size() > 9) {
            if (this.boxs.size() == 10) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> new BoxOpenMenu(preview, boxs, ticks, true).openMenu(player), 1);

                if (!preview) {
                    new InstantFirework(FireworkEffect.builder().flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE)
                            .withColor(Color.BLUE, Color.AQUA).build(), player.getLocation());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.closeInventory();
                            player.sendMessage(ChatUtil.prefix("&fVous avez obtenu la box: &c" + boxs.get(4).getName()));
                        }
                    }.runTaskLater(Main.getInstance(), 50);
                }
            } else {
                this.boxs.remove(0);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () ->
                        new BoxOpenMenu(preview, boxs, ticks, false).openMenu(player), ticks
                );
            }

        }

        return buttons;
    }

    private int getFromBox(Box box) {
        if (box.getPercentage() >= 250) {
            return 5;
        } else if (box.getPercentage() >= 25) {
            return 4;
        } else {
            return 1;
        }
    }
}
