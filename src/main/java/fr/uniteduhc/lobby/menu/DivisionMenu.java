package fr.uniteduhc.lobby.menu;

import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.common.cache.data.Division;
import fr.uniteduhc.common.cache.data.ProfileData;
import fr.uniteduhc.lobby.utils.other.ProgressBar;
import fr.uniteduhc.menu.Button;
import fr.uniteduhc.menu.Menu;
import fr.uniteduhc.menu.pagination.PaginatedMenu;
import fr.uniteduhc.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DivisionMenu extends PaginatedMenu {

    private final Menu oldMenu;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Ma Division";
    }

    @Override
    public Menu backButton() {
        return oldMenu;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;

        for (Division value : Division.values()) {
            buttons.put(i++, new DivisionDisplay(value));
            buttons.put(i + 4, new DivisionInformation(value));

            if (i == 5) i += 10;
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private static class DivisionDisplay extends Button {
        private final Division division;

        @Override
        public ItemStack getButtonItem(Player player) {
            ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());
            Division playerDivision = profile.getDivision();

            List<Division> divisions = Arrays.stream(Division.values()).collect(Collectors.toList());
            int playerDivisionInt = divisions.indexOf(playerDivision);
            int divisionInt = divisions.indexOf(division);

            List<String> lore = new ArrayList<>();

            if (playerDivisionInt == divisionInt) {
                lore.add("&8┃ &7Experience: &a" + profile.getExperience() + "&8/&c" + division.getExperience());
                lore.add(getAdvancement(profile.getExperience(), division));
            } else if (playerDivisionInt < divisionInt) {
                lore.add("&cDépassez la division précédente pour");
                lore.add("&cdébloquer celle-ci.");
            } else {
                lore.add("&aVous avez passé cette division.");
            }


            return new ItemBuilder(Material.WATCH).setName(division.getDisplay()).setLore(lore).toItemStack();
        }
    }

    @RequiredArgsConstructor
    private static class DivisionInformation extends Button {
        private final Division division;

        @Override
        public ItemStack getButtonItem(Player player) {
            ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());
            Division playerDivision = profile.getDivision();

            List<Division> divisions = Arrays.stream(Division.values()).collect(Collectors.toList());
            int playerDivisionInt = divisions.indexOf(playerDivision);
            int divisionInt = divisions.indexOf(division);

            String display;
            int durability;

            if (playerDivisionInt == divisionInt) {
                display = "&6Vous venez de débloquer cette division";
                durability = 10;
            } else if (playerDivisionInt < divisionInt) {
                display = "&cVous n'avez pas débloqué cette division.";
                durability = 8;
            } else {
                display = "&aVous avez débloqué cette division.";
                durability = 12;
            }


            return new ItemBuilder(Material.INK_SACK).setDurability(durability).setName(display).toItemStack();
        }
    }

    static String getAdvancement(int experience, Division division) {
        int percentage = experience * 100 / division.getExperience();
        return "  " + ProgressBar.getProgressBar(experience, division.getExperience(), 7, "■", ChatColor.GREEN, ChatColor.GRAY) + "&f " + percentage + "%";
    }
}
