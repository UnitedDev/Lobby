package fr.kohei.lobby.menu;

import fr.kohei.common.cache.data.ProfileData;
import fr.kohei.BukkitAPI;
import fr.kohei.menu.Button;
import fr.kohei.menu.Menu;
import fr.kohei.menu.pagination.PaginatedMenu;
import fr.kohei.utils.Heads;
import fr.kohei.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class IgnoreMenu extends PaginatedMenu {

    private final Menu oldMenu;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Joueurs ignorés";
    }

    @Override
    public Menu backButton() {
        return oldMenu;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new AddIgnoreButton());

        for (String i : BukkitAPI.getCommonAPI().getProfile(player.getUniqueId()).getSilentPlayer()) {
            buttons.put(buttons.size(), new ManageIgnoreButton(i));
        }

        return buttons;
    }

    private class AddIgnoreButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Heads.LIME_PLUS.toItemStack()).setName("&6&lIgnorer un joueur").setLore(
                    "&fPour ignorer un joueur utilisez la commande",
                    "&f/&4i &cadd &f<joueur>"
            ).toItemStack();
        }
    }

    @RequiredArgsConstructor
    private class ManageIgnoreButton extends Button {

        private final String ignore;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SKULL_ITEM).setDurability(SkullType.PLAYER.ordinal()).setSkullOwner(ignore).setName("&6&l" + ignore).setLore(
                    "&fCliquez-ici pour supprimer &c" + ignore,
                    "&fde votre de joueurs ignorés"
            ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            ProfileData data = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

            data.getSilentPlayer().remove(ignore);

            BukkitAPI.getCommonAPI().saveProfile(player.getUniqueId(), data);

            new IgnoreMenu(oldMenu).openMenu(player);
        }

    }
}
