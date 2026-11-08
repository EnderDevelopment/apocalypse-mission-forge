package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {
    private MissionStore missionStore;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        missionStore = new MissionStore(this);
        missionStore.load();
        Bukkit.getPluginManager().registerEvents(this, this);
        MissionMenu missionMenu = new MissionMenu(missionStore);
        Bukkit.getPluginManager().registerEvents(missionMenu, this);
        MissionsCommand command = new MissionsCommand(this, missionStore, missionMenu);
        getCommand("missions").setExecutor(command);
        getCommand("missions").setTabCompleter(command);
    }

    @Override
    public void onDisable() {
        if (missionStore != null) missionStore.save();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        missionStore.getOrCreate(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onZombieDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie) || zombie.getKiller() == null) return;
        Player player = zombie.getKiller();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        String weaponIdentity = identity(weapon);
        if (containsConfigured(weaponIdentity, "tacz")) {
            update(player, "tacz", 1);
        }
        if (containsConfigured(weaponIdentity, "marbled-arsenal-legacy")) {
            update(player, "marbled-arsenal-legacy", 1);
        }
    }

    @EventHandler
    public void onCollect(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        String itemIdentity = identity(item);
        if (containsConfigured(itemIdentity, "lrtactical")) update(player, "lrtactical", item.getAmount());
        if (containsConfigured(itemIdentity, "tough-as-nails")) update(player, "tough-as-nails", item.getAmount());
    }

    private void update(Player player, String modKey, int amount) {
        for (Mission mission : missionStore.getOrCreate(player.getUniqueId())) {
            if (modKey.equals(mission.getModKey()) && !mission.isComplete()) {
                mission.addProgress(amount);
                if (mission.isComplete()) {
                    player.sendMessage(Component.text("Mission complete: " + mission.getTitle()
                            + ". Use /missions claim.", NamedTextColor.GREEN));
                }
            }
        }
    }

    private String identity(ItemStack item) {
        return item.getType().getKey().toString().toLowerCase();
    }

    private boolean containsConfigured(String identity, String modKey) {
        for (String configuredId : getConfig().getStringList("item-matches." + modKey)) {
            if (identity.equals(configuredId.trim().toLowerCase())) return true;
        }
        return false;
    }
}
