package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class MissionRewards {
    private MissionRewards() { }

    public static void grant(Player player, Mission mission) {
        player.giveExp(mission.getRewardExperience());
        ItemStack loot = mission.createLootItem();
        if (loot != null) {
            MapResult result = giveOrDrop(player, loot);
            player.sendMessage(Component.text(mission.getLootLabel() + ": " + loot.getAmount() + "x "
                    + loot.getType().getKey().getKey()
                    + (result.dropped ? " (some dropped at your feet)" : ""), NamedTextColor.GOLD));
        }
    }

    private static MapResult giveOrDrop(Player player, ItemStack item) {
        MapResult result = new MapResult();
        for (ItemStack remainder : player.getInventory().addItem(item).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), remainder);
            result.dropped = true;
        }
        return result;
    }

    private static final class MapResult {
        private boolean dropped;
    }
}
