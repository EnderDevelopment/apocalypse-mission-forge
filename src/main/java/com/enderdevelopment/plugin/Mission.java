package com.enderdevelopment.plugin;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class Mission {
    public enum Type {
        KILL_ZOMBIES,
        COLLECT_SUPPLIES,
        TRAVEL,
        MOD_OBJECTIVE
    }

    private final String id;
    private final String title;
    private final String description;
    private final Type type;
    private final String modKey;
    private final int target;
    private final int rewardExperience;
    private final String lootMaterial;
    private final int lootAmount;
    private final String lootLabel;
    private int progress;
    private boolean claimed;

    public Mission(String id, String title, String description, Type type, String modKey, int target,
                   int rewardExperience, String lootMaterial, int lootAmount, String lootLabel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.modKey = modKey;
        this.target = target;
        this.rewardExperience = rewardExperience;
        this.lootMaterial = lootMaterial;
        this.lootAmount = lootAmount;
        this.lootLabel = lootLabel == null || lootLabel.isBlank() ? "Mission supply cache" : lootLabel;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Type getType() { return type; }
    public String getModKey() { return modKey; }
    public int getTarget() { return target; }
    public int getProgress() { return progress; }
    public int getRewardExperience() { return rewardExperience; }
    public String getLootMaterial() { return lootMaterial; }
    public int getLootAmount() { return lootAmount; }
    public String getLootLabel() { return lootLabel; }
    public boolean isClaimed() { return claimed; }
    public boolean isComplete() { return progress >= target; }
    public void setProgress(int progress) { this.progress = Math.max(0, Math.min(progress, target)); }
    public void addProgress(int amount) { setProgress(progress + amount); }
    public void setClaimed(boolean claimed) { this.claimed = claimed; }

    public ItemStack createLootItem() {
        if (lootMaterial == null || lootMaterial.isBlank() || lootAmount <= 0) return null;
        Material material = Material.matchMaterial(lootMaterial);
        return material == null ? null : new ItemStack(material, lootAmount);
    }

    public List<String> serialize() {
        return List.of(id, title, description, type.name(), modKey == null ? "" : modKey,
                Integer.toString(target), Integer.toString(rewardExperience), Integer.toString(progress),
                Boolean.toString(claimed), lootMaterial == null ? "" : lootMaterial, Integer.toString(lootAmount), lootLabel);
    }

    public static Mission deserialize(List<String> values) {
        if (values.size() < 9) throw new IllegalArgumentException("Invalid mission data");
        String lootMaterial = values.size() >= 10 && !values.get(9).isEmpty() ? values.get(9) : null;
        int lootAmount = values.size() >= 11 ? Integer.parseInt(values.get(10)) : 0;
        Mission mission = new Mission(values.get(0), values.get(1), values.get(2), Type.valueOf(values.get(3)),
                values.get(4).isEmpty() ? null : values.get(4), Integer.parseInt(values.get(5)),
                Integer.parseInt(values.get(6)), lootMaterial, lootAmount,
                values.size() >= 12 && !values.get(11).isEmpty() ? values.get(11) : "Mission supply cache");
        mission.setProgress(Integer.parseInt(values.get(7)));
        mission.setClaimed(Boolean.parseBoolean(values.get(8)));
        return mission;
    }
}
