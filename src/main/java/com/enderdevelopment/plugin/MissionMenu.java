package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class MissionMenu implements Listener {
    private static final int INVENTORY_SIZE = 27;
    private static final int CLAIM_ALL_SLOT = 22;
    private static final int CLOSE_SLOT = 26;

    private final MissionStore store;

    public MissionMenu(MissionStore store) {
        this.store = store;
    }

    public void open(Player player) {
        List<Mission> missions = store.getOrCreate(player.getUniqueId());
        MissionMenuHolder holder = new MissionMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, INVENTORY_SIZE, Component.text("Apocalypse Missions", NamedTextColor.DARK_RED));
        holder.setInventory(inventory);

        fillBackground(inventory);
        int slot = 10;
        for (Mission mission : missions) {
            if (slot > 16) break;
            inventory.setItem(slot++, missionItem(mission));
        }
        inventory.setItem(CLAIM_ALL_SLOT, button(Material.EXPERIENCE_BOTTLE, "Claim completed", NamedTextColor.GREEN,
                List.of("Collect all rewards that are ready.")));
        inventory.setItem(CLOSE_SLOT, button(Material.BARRIER, "Close", NamedTextColor.RED, List.of()));
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof MissionMenuHolder)) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) return;

        int slot = event.getRawSlot();
        if (slot == CLOSE_SLOT) {
            player.closeInventory();
            return;
        }
        if (slot == CLAIM_ALL_SLOT) {
            claimAll(player);
            open(player);
            return;
        }
        if (slot < 10 || slot > 16) return;
        Mission mission = missionAt(player, slot - 10);
        if (mission == null || !mission.isComplete() || mission.isClaimed()) return;
        mission.setClaimed(true);
        MissionRewards.grant(player, mission);
        player.sendMessage(Component.text("Claimed " + mission.getTitle() + " for "
                + mission.getRewardExperience() + " XP and loot.", NamedTextColor.GREEN));
        open(player);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof MissionMenuHolder) event.setCancelled(true);
    }

    private void claimAll(Player player) {
        int claimed = 0;
        for (Mission mission : store.getOrCreate(player.getUniqueId())) {
            if (mission.isComplete() && !mission.isClaimed()) {
                mission.setClaimed(true);
                MissionRewards.grant(player, mission);
                claimed++;
            }
        }
        player.sendMessage(Component.text(claimed == 0
                ? "There are no completed missions to claim."
                : "Claimed " + claimed + " completed mission(s).", claimed == 0 ? NamedTextColor.YELLOW : NamedTextColor.GREEN));
    }

    private Mission missionAt(Player player, int index) {
        List<Mission> missions = store.getOrCreate(player.getUniqueId());
        return index >= 0 && index < missions.size() ? missions.get(index) : null;
    }

    private ItemStack missionItem(Mission mission) {
        Material material = mission.isClaimed() ? Material.GRAY_DYE
                : mission.isComplete() ? Material.EMERALD : Material.ROTTEN_FLESH;
        NamedTextColor color = mission.isClaimed() ? NamedTextColor.GRAY
                : mission.isComplete() ? NamedTextColor.GREEN : NamedTextColor.GOLD;
        String status = mission.isClaimed() ? "Claimed" : mission.isComplete() ? "Complete - click to claim" : "In progress";
        return button(material, mission.getTitle(), color, List.of(
                mission.getDescription(),
                "Progress: " + mission.getProgress() + "/" + mission.getTarget(),
                "Reward: " + mission.getRewardExperience() + " XP + " + mission.getLootAmount() + "x "
                        + mission.getLootMaterial() + " (" + mission.getLootLabel() + ")", 
                "Status: " + status));
    }

    private ItemStack button(Material material, String title, NamedTextColor color, List<String> lines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(title, color));
        List<Component> lore = new ArrayList<>();
        for (String line : lines) lore.add(Component.text(line, NamedTextColor.GRAY));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void fillBackground(Inventory inventory) {
        ItemStack filler = button(Material.BLACK_STAINED_GLASS_PANE, " ", NamedTextColor.BLACK, List.of());
        for (int slot = 0; slot < inventory.getSize(); slot++) inventory.setItem(slot, filler);
    }

    private static final class MissionMenuHolder implements InventoryHolder {
        private Inventory inventory;

        private void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
