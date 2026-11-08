package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class MissionStore {
    private final Main plugin;
    private final Map<UUID, List<Mission>> missions = new HashMap<>();
    private final Map<UUID, Long> refreshTimes = new HashMap<>();

    public MissionStore(Main plugin) { this.plugin = plugin; }

    public void load() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection players = config.getConfigurationSection("players");
        if (players == null) return;
        for (String key : players.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                List<Mission> loaded = new ArrayList<>();
                for (String encoded : config.getStringList("players." + key + ".missions")) {
                    loaded.add(Mission.deserialize(List.of(encoded.split("\\|", -1))));
                }
                missions.put(uuid, loaded);
                refreshTimes.put(uuid, config.getLong("players." + key + ".refresh", 0));
            } catch (IllegalArgumentException ignored) { }
        }
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();
        config.set("players", null);
        for (Map.Entry<UUID, List<Mission>> entry : missions.entrySet()) {
            String path = "players." + entry.getKey();
            List<String> encoded = new ArrayList<>();
            for (Mission mission : entry.getValue()) encoded.add(String.join("|", mission.serialize()));
            config.set(path + ".missions", encoded);
            config.set(path + ".refresh", refreshTimes.getOrDefault(entry.getKey(), 0L));
        }
        plugin.saveConfig();
    }

    public List<Mission> getOrCreate(UUID uuid) {
        if (!missions.containsKey(uuid) || isExpired(uuid)) {
            missions.put(uuid, MissionFactory.createMissions(plugin));
            refreshTimes.put(uuid, System.currentTimeMillis());
        }
        if (missions.get(uuid).stream().anyMatch(mission -> mission.getType() != Mission.Type.MOD_OBJECTIVE
                || mission.getModKey() == null)) {
            missions.put(uuid, MissionFactory.createMissions(plugin));
            refreshTimes.put(uuid, System.currentTimeMillis());
        }
        return missions.get(uuid);
    }

    public void refresh(UUID uuid) {
        missions.put(uuid, MissionFactory.createMissions(plugin));
        refreshTimes.put(uuid, System.currentTimeMillis());
    }

    private boolean isExpired(UUID uuid) {
        long hours = plugin.getConfig().getLong("mission-refresh-hours", 24);
        return System.currentTimeMillis() - refreshTimes.getOrDefault(uuid, 0L) >= hours * 3_600_000L;
    }
}
