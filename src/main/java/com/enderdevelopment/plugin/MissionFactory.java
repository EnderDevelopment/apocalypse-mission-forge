package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class MissionFactory {
    private MissionFactory() { }

    public static List<Mission> createMissions(Main plugin) {
        int reward = plugin.getConfig().getInt("reward-experience", 25);
        List<Mission> missions = new ArrayList<>();
        missions.add(create(plugin, "tacz-fireteam", "TACZ Fireteam",
                plugin.getConfig().getString("mod-objectives.tacz", "Use a TACZ weapon to eliminate infected targets."),
                "tacz", 10, reward));
        missions.add(create(plugin, "lrtactical-supply", "LRTactical Supply Run",
                plugin.getConfig().getString("mod-objectives.lrtactical", "Collect LRTactical equipment from the infected zone."),
                "lrtactical", 5, reward));
        missions.add(create(plugin, "arsenal-clearance", "Marbled Arsenal Clearance",
                plugin.getConfig().getString("mod-objectives.marbled-arsenal-legacy", "Use a Marbled's Arsenal Legacy weapon to clear infected targets."),
                "marbled-arsenal-legacy", 10, reward));
        missions.add(create(plugin, "hydration-watch", "Tough As Nails Hydration Watch",
                plugin.getConfig().getString("mod-objectives.tough-as-nails", "Maintain survival supplies from Tough As Nails while crossing the infected zone."),
                "tough-as-nails", 3, reward));
        Collections.shuffle(missions, ThreadLocalRandom.current());
        int count = Math.max(1, Math.min(plugin.getConfig().getInt("missions-per-player", 3), missions.size()));
        return new ArrayList<>(missions.subList(0, count));
    }

    private static Mission create(Main plugin, String id, String title, String description,
                                  String modKey, int target, int rewardExperience) {
        String path = "loot-by-mod." + modKey;
        String material = plugin.getConfig().getString(path + ".material", "IRON_NUGGET");
        int amount = Math.max(1, plugin.getConfig().getInt(path + ".amount", 1));
        String label = plugin.getConfig().getString(path + ".label", modKey + " supply cache");
        return new Mission(id, title, description, Mission.Type.MOD_OBJECTIVE, modKey, target,
                rewardExperience, material, amount, label);
    }
}
