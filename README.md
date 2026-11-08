# Minecraft Zombie Apocalypse Mission Plugin

Apocalypse Mission Forge is a Minecraft Java plugin that automatically generates zombie-apocalypse missions connected to TACZ, LRTactical, Marbled's Arsenal Legacy, and Tough As Nails, with configurable objectives, progress tracking, rewards, persistence, and an inventory mission menu.

## Install

1. Use a Paper Minecraft server running version 1.21.11 or a compatible 1.21 server.
2. Install Java 21.
3. Build the plugin with Maven:

   `mvn package`

4. Copy the generated `target/plugin.jar` file into the server's `plugins` folder.
5. Start the server once to generate the configuration.
6. Open `plugins/ApocalypseMissions/config.yml`.
7. Replace the example item IDs under `item-matches` with the exact registry IDs used by the installed mod versions.
8. Restart the server.

The related gameplay mods should be installed on the server if you want objectives to track their items and weapons. The plugin identifies items by their complete Minecraft registry key.

## Use

Use `/missions` or `/missions list` to open the mission menu.

The plugin gives each player up to three randomly selected missions from these objective types:

- TACZ zombie elimination objectives
- LRTactical item collection objectives
- Marbled's Arsenal Legacy zombie elimination objectives
- Tough As Nails item collection objectives

Kill vanilla Minecraft zombies while holding a configured TACZ or Marbled's Arsenal Legacy item to progress the matching elimination mission.

Collect configured LRTactical or Tough As Nails items through the inventory interface to progress the matching collection mission.

Completed missions can be claimed from the menu or with:

`/missions claim`

Players can also click individual completed missions in the menu, or use the Claim completed button to claim all available rewards.

Missions refresh automatically after the configured number of hours. The default refresh period is 24 hours.

Players with the `apocalypsemissions.admin` permission can manually refresh their missions with:

`/missions refresh`

The main configuration options are:

- `missions-per-player`: Number of missions generated per player.
- `mission-refresh-hours`: Automatic refresh interval.
- `reward-experience`: Experience reward for each mission.
- `mod-objectives`: Mission descriptions.
- `loot-by-mod`: Vanilla reward item, amount, and display label for each mod objective.
- `item-matches`: Exact item registry IDs used to detect mod weapons and supplies.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/1c68d5f5-5f29-439c-bc06-16ba5b8ebf52?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=1c68d5f5-5f29-439c-bc06-16ba5b8ebf52
