package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class MissionsCommand implements CommandExecutor, TabCompleter {
    private final MissionStore store;
    private final MissionMenu menu;

    public MissionsCommand(Main plugin, MissionStore store, MissionMenu menu) {
        this.store = store;
        this.menu = menu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use missions.");
            return true;
        }
        String action = args.length == 0 ? "list" : args[0].toLowerCase();
        if (action.equals("refresh")) {
            if (!player.hasPermission("apocalypsemissions.admin")) {
                player.sendMessage(Component.text("You do not have permission.", NamedTextColor.RED));
                return true;
            }
            store.refresh(player.getUniqueId());
            player.sendMessage(Component.text("Your apocalypse missions were refreshed.", NamedTextColor.GREEN));
            return true;
        }
        if (action.equals("claim")) {
            claim(player);
            return true;
        }
        if (action.equals("list")) {
            menu.open(player);
            return true;
        }
        player.sendMessage(Component.text("Usage: /missions [list|claim|refresh]", NamedTextColor.YELLOW));
        return true;
    }

    private void claim(Player player) {
        int claimed = 0;
        for (Mission mission : store.getOrCreate(player.getUniqueId())) {
            if (mission.isComplete() && !mission.isClaimed()) {
                mission.setClaimed(true);
                MissionRewards.grant(player, mission);
                claimed++;
                player.sendMessage(Component.text("Claimed " + mission.getTitle() + " for "
                        + mission.getRewardExperience() + " XP and loot.", NamedTextColor.GREEN));
            }
        }
        if (claimed == 0) player.sendMessage(Component.text("There are no completed missions to claim.", NamedTextColor.YELLOW));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) return List.of();
        List<String> options = new ArrayList<>(List.of("list", "claim"));
        if (sender.hasPermission("apocalypsemissions.admin")) options.add("refresh");
        return options.stream().filter(option -> option.startsWith(args[0].toLowerCase())).toList();
    }
}
