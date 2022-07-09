package fr.kohei.lobby.menu;

import fr.kohei.BukkitAPI;
import fr.kohei.common.cache.data.ProfileData;
import fr.kohei.lobby.manager.box.Box;
import fr.kohei.lobby.manager.player.LobbyPlayer;
import fr.kohei.menu.Button;
import fr.kohei.menu.pagination.PaginatedMenu;
import fr.kohei.utils.ChatUtil;
import fr.kohei.utils.Heads;
import fr.kohei.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.awt.color.ProfileDataException;
import java.util.HashMap;
import java.util.Map;

public class BoxMenu extends PaginatedMenu {
    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Box";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(39, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Heads.LUCKY.toItemStack()).setName("&a&lDébloquer des Boxs").setLore(
                        "&fVous pouvez débloquer des boxs en achetant",
                        "&fdes coins sur notre &aboutique&f.",
                        "",
                        "&f&l» &eRendez-vous sur notre boutique"
                ).toItemStack();
            }
        });
        buttons.put(40, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());
                if (profile.getBox() <= 0) {
                    return new ItemBuilder(Heads.COMMAND_BLOCK.toItemStack()).setName("&6&lOuvrir").setLore(
                            "&fPermet d'ouvrir une box en utilisant vos",
                            "&ftickets.",
                            "",
                            "&f&l» &cVous n'avez pas de box"
                    ).toItemStack();
                }
                return new ItemBuilder(Heads.COMMAND_BLOCK.toItemStack()).setName("&6&lOuvrir").setLore(
                        "&fPermet d'ouvrir une box en utilisant vos",
                        "&ftickets.",
                        "",
                        "&f&l» &eCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());
                if (profile.getBox() <= 0) {
                    player.sendMessage(ChatUtil.prefix("&cIl semblerait que vous n'avez pas assez de boxs"));
                    return;
                }

                profile.setBox(profile.getBox() - 1);
                BukkitAPI.getCommonAPI().saveProfile(player.getUniqueId(), profile);
                new LobbyPlayer(player).refreshHotbar();
                new BoxOpenMenu(false, null, 1, false).openMenu(player);
            }
        });
        buttons.put(41, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Heads.SHOP.toItemStack()).setName("&6&lPreview").setLore(
                        "&fPermet de prévisualiser l'ouverture",
                        "&fd'une box.",
                        "",
                        "&f&l» &eCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new BoxOpenMenu(true, null, 1, false).openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Box value : Box.values()) {
            buttons.put(buttons.size(), new BoxButton(value));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private class BoxButton extends Button {
        private final Box value;

        @Override
        public ItemStack getButtonItem(Player player) {
            String rarity;
            if (value.getPercentage() >= 250) {
                rarity = " &7▎ &a&lCOMMUN";
            } else if (value.getPercentage() >= 25) {
                rarity = " &7▎ &e&lRARE";
            } else {
                rarity = " &7▎ &6&lLÉGENDAIRE";
            }

            return new ItemBuilder(value.getDisplay()).setName(value.getName() + rarity).toItemStack();
        }
    }
}
