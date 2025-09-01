package com.minegolem.promoter.managers;

import com.minegolem.promoter.Promoter;
import com.minegolem.promoter.data.manager.DataManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

@RequiredArgsConstructor
public class CampaignManager {
    private final Promoter plugin;
    private final DataManager dataManager = Promoter.getDataManager();

    @RequiredArgsConstructor
    public static class PlayerEntry {
        public final UUID uuid;
        public final String username;
        public final long timestamp;
    }

    /** ==================== RESET ==================== **/

    public void reset() {
        dataManager.set("players", null)
            .set("links", null).saveSilently();
        this.removeAdminLink();
    }

    /** ==================== PLAYERS ==================== **/

    public void addReceived(UUID uuid, String username) {
        setPlayer("players.linkreceived", uuid, username);
    }

    public void addRewarded(UUID uuid, String username) {
        setPlayer("players.linkrewarded", uuid, username);
    }

    private void setPlayer(String basePath, UUID uuid, String username) {
        long timestamp = System.currentTimeMillis() / 1000;

        dataManager.set(basePath + "." + uuid.toString() + ".username", username)
                .set(basePath + "." + uuid.toString() + ".timestamp", timestamp)
                .saveSilently();
    }

    public PlayerEntry getReceived(UUID uuid) {
        return getPlayer("players.linkreceived", uuid);
    }

    public PlayerEntry getRewarded(UUID uuid) {
        return getPlayer("players.linkrewarded", uuid);
    }

    private PlayerEntry getPlayer(String basePath, UUID uuid) {
        ConfigurationSection sec = dataManager.getSection(basePath + "." + uuid.toString());
        if (sec == null) return null;

        String username = sec.getString("username");

        long ts = sec.getLong("timestamp", 0);
        return new PlayerEntry(uuid, username, ts);
    }

    public Set<UUID> getAllReceived() {
        return getAllUUIDs("players.linkreceived");
    }

    public Set<UUID> getAllRewarded() {
        return getAllUUIDs("players.linkrewarded");
    }

    private Set<UUID> getAllUUIDs(String basePath) {
        Set<String> keys = dataManager.getKeys(basePath, false);
        Set<UUID> uuids = new HashSet<>();
        for (String k : keys) {
            try {
                uuids.add(UUID.fromString(k));
            } catch (IllegalArgumentException ignored) {}
        }
        return uuids;
    }

    /** ==================== LINKS ==================== **/

    public List<String> getLinks() {
        return dataManager.getStringList("links");
    }

    public void addLink(String link) {
        List<String> links = new ArrayList<>(getLinks());
        links.add(link);
        dataManager.set("links", links).saveSilently();
    }

    public void removeLink(String link) {
        List<String> links = new ArrayList<>(getLinks());
        links.remove(link);
        dataManager.set("links", links).saveSilently();
    }

    public void setAdminLink(String link) {
        dataManager.set("adminlink", link).saveSilently();
    }

    public String getAdminLink() {
        return dataManager.getString("adminlink", "");
    }

    public void removeAdminLink() {
        dataManager.set("adminlink", null).saveSilently();
    }

    public void setPlayerReceivedLink(UUID uuid, String link) {
        dataManager.set("players.linkreceived" + uuid + ".link", link).saveSilently();
    }

    public String getPlayerReceivedLink(UUID uuid) {
        return dataManager.getString("players.linkreceived" + uuid + ".link", "");
    }


}
