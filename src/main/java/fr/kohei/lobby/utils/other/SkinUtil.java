package fr.kohei.lobby.utils.other;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;

public class SkinUtil {

    @SneakyThrows
    public static void setSkin(Player player, GameProfile profile, JavaPlugin plugin) {

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        //	setPlayerNames();

        Field field = EntityHuman.class.getDeclaredField("bH");
        field.setAccessible(true);

        GameProfile currentProfile = (GameProfile) field.get(entityPlayer);

        currentProfile.getProperties().clear();
        for (Property property : profile.getProperties().values()) {
            currentProfile.getProperties().put(property.getName(), property);
        }

        field.set(entityPlayer, currentProfile);

        sendPlayerUpdate(player, plugin);
    }

    private static void sendPlayerUpdate(Player player, JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendUpdateToPlayer(player);

                plugin.getServer().getOnlinePlayers().stream()
                        .filter(other -> !other.equals(player))
                        .filter(other -> other.canSee(player))
                        .forEach(other -> {
                            other.hidePlayer(player);
                            other.showPlayer(player);
                        });
            }
        }.runTask(plugin);
    }

    private static void sendUpdateToPlayer(Player player) {
        final org.bukkit.entity.Entity vehicle = player.getVehicle();
        if (vehicle != null) {
        vehicle.eject();
        }

        sendPackets(player);

        player.updateInventory();
        player.setGameMode(player.getGameMode());

        PlayerInventory inventory = player.getInventory();
        inventory.setHeldItemSlot(inventory.getHeldItemSlot());

        double oldHealth = player.getHealth();

        int oldFood = player.getFoodLevel();
        float oldSat = player.getSaturation();
        player.setFoodLevel(20);
        player.setFoodLevel(oldFood);
        player.setSaturation(5.0F);
        player.setSaturation(oldSat);

        player.setMaxHealth(player.getMaxHealth());

        player.setHealth(20.0F);
        player.setHealth(oldHealth);

        float experience = player.getExp();
        int totalExperience = player.getTotalExperience();
        player.setExp(experience);
        player.setTotalExperience(totalExperience);

        player.setWalkSpeed(player.getWalkSpeed());
    }

    private static void sendPackets(Player player) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Location previousLocation = player.getLocation().clone();

        entityPlayer.playerConnection.sendPacket(
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        entityPlayer.playerConnection.sendPacket(
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        entityPlayer.playerConnection.sendPacket(
                new PacketPlayOutRespawn(entityPlayer.getWorld().worldProvider.getDimension(),
                        entityPlayer.getWorld().worldData.getDifficulty(),
                        entityPlayer.getWorld().worldData.getType(),
                        WorldSettings.EnumGamemode.valueOf(entityPlayer.getBukkitEntity().getGameMode().name())));
        player.teleport(previousLocation);
    }



    public static class GameProfileUtil {

        @SneakyThrows
        public static GameProfile getSkinFromName(String name) {
            UUIDFetcher uuidFetcher = new UUIDFetcher(Collections.singletonList(name));
            Map<String, UUID> fetched = uuidFetcher.call();

            Optional<UUID> fetchedUuid = fetched.values().stream().findFirst();
            return fetchedUuid
                    .map(uuid -> getSkinFromUUID(uuid, name))
                    .orElseGet(() -> getSkinFromUUID(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve"));
        }

        @SneakyThrows
        public static GameProfile getSkinFromUUID(UUID uuid, String skinName) {
            GameProfile toReturn = new GameProfile(uuid, skinName);

            SignatureFetcher signatureFetcher = new SignatureFetcher(uuid);
            signatureFetcher.call();

            if (signatureFetcher.getSignature() != null) {
                toReturn.getProperties().put(
                        signatureFetcher.getPropertyName(),
                        new Property(signatureFetcher.getPropertyName(),
                                signatureFetcher.getValue(),
                                signatureFetcher.getSignature())
                );
            }
            return toReturn;
        }


        public static GameProfile getSkinFromSignature(String skinName, String value, String signature) {
            GameProfile toReturn = new GameProfile(UUID.randomUUID(), skinName);
            toReturn.getProperties().put("textures", new Property(
                    "textures",
                    value,
                    signature));
            return toReturn;
        }
    }

    static class UUIDFetcher implements Callable<Map<String, UUID>> {
        private static final double PROFILES_PER_REQUEST = 100.0D;

        private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";

        private final JSONParser jsonParser = new JSONParser();

        private final List<String> names;

        private final boolean rateLimiting;

        public UUIDFetcher(List<String> names, boolean rateLimiting) {
            this.names = ImmutableList.copyOf(names);
            this.rateLimiting = rateLimiting;
        }

        public UUIDFetcher(List<String> names) {
            this(names, true);
        }

        private static void writeBody(HttpURLConnection connection, String body) throws Exception {
            OutputStream stream = connection.getOutputStream();
            stream.write(body.getBytes());
            stream.flush();
            stream.close();
        }

        private static HttpURLConnection createConnection() throws Exception {
            URL url = new URL("https://api.mojang.com/profiles/minecraft");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            return connection;
        }

        private static UUID getUUID(String id) {
            return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
        }

        public static byte[] toBytes(UUID uuid) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
            byteBuffer.putLong(uuid.getMostSignificantBits());
            byteBuffer.putLong(uuid.getLeastSignificantBits());
            return byteBuffer.array();
        }

        public static UUID fromBytes(byte[] array) {
            if (array.length != 16)
                throw new IllegalArgumentException("Illegal byte array length: " + array.length);
            ByteBuffer byteBuffer = ByteBuffer.wrap(array);
            long mostSignificant = byteBuffer.getLong();
            long leastSignificant = byteBuffer.getLong();
            return new UUID(mostSignificant, leastSignificant);
        }

        public static UUID getUUIDOf(String name) throws Exception {
            return (new UUIDFetcher(Arrays.asList(name))).call().get(name);
        }

        public Map<String, UUID> call() throws Exception {
            Map<String, UUID> uuidMap = new HashMap<>();
            int requests = (int) Math.ceil(names.size() / 100.0D);
            for (int i = 0; i < requests; i++) {
                HttpURLConnection connection = createConnection();
                String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));
                writeBody(connection, body);
                JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
                for (Object profile : array) {
                    JSONObject jsonProfile = (JSONObject) profile;
                    String id = (String) jsonProfile.get("id");
                    String name = (String) jsonProfile.get("name");
                    UUID uuid = getUUID(id);
                    uuidMap.put(name, uuid);
                }
                if (rateLimiting && i != requests - 1)
                    Thread.sleep(100L);
            }
            return uuidMap;
        }
    }

    @Getter
    @RequiredArgsConstructor
    static class SignatureFetcher {

        private String value;
        private String signature;
        private String skinName;
        private String propertyName;

        public final UUID uuid;

        @SneakyThrows
        private HttpURLConnection createConnection() {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "Core");
            connection.setDoOutput(true);
            return connection;
        }

        @SneakyThrows
        public void call() {
            HttpURLConnection connection = createConnection();
            connection.connect();
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                reader.lines().forEach(stringBuilder::append);
                JsonObject object = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();

                skinName = object.get("name").getAsString();
                JsonArray array = object.get("properties").getAsJsonArray();

                for (Object obj : array) {
                    JsonObject property = (JsonObject) obj;
                    propertyName = property.get("name").getAsString();
                    signature = property.get("signature").getAsString();
                    value = property.get("value").getAsString();
                }
                reader.close();
            }
            connection.disconnect();
        }
    }
}