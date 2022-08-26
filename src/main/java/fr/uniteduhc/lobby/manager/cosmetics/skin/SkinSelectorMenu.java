package fr.uniteduhc.lobby.manager.cosmetics.skin;

import fr.uniteduhc.BukkitAPI;
import fr.uniteduhc.common.cache.data.ProfileData;
import fr.uniteduhc.lobby.Main;
import fr.uniteduhc.lobby.manager.player.LobbyProfileData;
import fr.uniteduhc.menu.Button;
import fr.uniteduhc.menu.Menu;
import fr.uniteduhc.menu.pagination.PaginatedMenu;
import fr.uniteduhc.utils.ChatUtil;
import fr.uniteduhc.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SkinSelectorMenu extends PaginatedMenu {
    private final Menu oldMenu;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Skins";
    }

    @Override
    public int getGlassColor() {
        return 14;
    }

    @Override
    public Menu backButton() {
        return oldMenu;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BARRIER).setName("&c&lRetirer son skin").setLore(
                        "&fPermet de retirer son skin sur les",
                        "&flobbies.",
                        "",
                        "&f&l» &eCliquez-ici pour y accéder"
                ).toItemStack();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                LobbyProfileData profileData = new LobbyProfileData(player);
                profileData.setSkin("aucun");

                player.closeInventory();
                player.sendMessage(ChatUtil.prefix("&fIl vous suffit de changer de lobby pour &arafraichir&f votre skin."));
            }
        });

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (SkinCosmetic value : Arrays.stream(SkinCosmetic.values()).sorted(Comparator.comparingInt(Enum::ordinal)).toArray(SkinCosmetic[]::new)) {
            buttons.put(buttons.size(), new SkinDisplayButton(value));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private static class SkinDisplayButton extends Button {
        private final SkinCosmetic value;

        @Override
        public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            String clickLine = "&f&l» &aCliquez-ici pour sélectionner";
            int amount = 1;

            LobbyProfileData profileData = new LobbyProfileData(player);

            if (!profileData.getBoughtSkins().contains(value.name())) {
                clickLine = "&f&l» &eCliquez-ici pour acheter";
                amount = 0;
            }

            if (profileData.getSkinCosmetic() != null && profileData.getSkinCosmetic().name().equalsIgnoreCase(value.name())) {
                clickLine = "&f&l» &cVous avez déjà sélectionné ce skin";
            }

            return new ItemBuilder(Material.SKULL_ITEM).setAmount(amount).setDurability(SkullType.PLAYER.ordinal())
                    .setTexture(value.getSkin().getHead())
                    .setName("&6&l" + value.getName() + " &7▎ " + value.getRarity().getName())
                    .setLore(
                            "",
                            "&8▎ &7Prix: &e" + value.getPrice() + " ⛁",
                            "&8▎ &7Skin: &a" + value.getName(),
                            "",
                            clickLine
                    ).toItemStack();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            LobbyProfileData profileData = new LobbyProfileData(player);
            if (!profileData.getBoughtSkins().contains(value.name())) {
                ProfileData profile = BukkitAPI.getCommonAPI().getProfile(player.getUniqueId());

                if (profile.getCoins() < value.getPrice()) {
                    player.sendMessage(ChatUtil.prefix("&cVous n'avez pas assez de coins pour acheter cette tenue."));
                    return;
                }

                player.sendMessage(ChatUtil.prefix("&fVous avez acheté la tenue &a" + value.getName() + " &fpour &e" + value.getPrice() + " ⛁"));
                profile.setCoins(profile.getCoins() - value.getPrice());
                profileData.getBoughtSkins().add(value.name());
                BukkitAPI.getCommonAPI().saveProfile(player.getUniqueId(), profile);
                return;
            }

            if (profileData.getSkinCosmetic() != null && profileData.getSkinCosmetic().name().equalsIgnoreCase(value.name())) {
                return;
            }

            player.closeInventory();
            Main.getInstance().getCosmeticManager().getSkinManager().setSkin(player, value);
        }
    }
}
