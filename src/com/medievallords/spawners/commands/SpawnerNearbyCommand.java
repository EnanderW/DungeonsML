package com.medievallords.spawners.commands;

import com.medievallords.dungeons.Dungeon;
import com.medievallords.spawners.Spawner;
import com.medievallords.utils.BaseCommand;
import com.medievallords.utils.Command;
import com.medievallords.utils.CommandArgs;
import com.medievallords.utils.MessageManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WE on 2017-09-29.
 *
 */

public class SpawnerNearbyCommand extends BaseCommand {

    @Command(name = "dspawner.nearby", permission = "dungeons.commands.admin", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
         Player player = commandArgs.getPlayer();
         String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/spawner.nearby <radius>");
            return;
        }

        int radius = 50;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cThe radius can only be a number");
            return;
        }

        List<String> spawners = new ArrayList<>();
        for (int i = 0; i < getDungeonHandler().getDungeons().size(); i++) {
            Dungeon dungeon = getDungeonHandler().getDungeons().get(i);
            for (int l = 0; i < dungeon.getSpawners().size(); l++) {
                Spawner spawner = dungeon.getSpawners().get(l);
                if (player.getWorld() != dungeon.getWorld()) {
                    continue;
                }

                if (spawner.getLocation(dungeon.getWorld()).distance(player.getLocation()) <= radius) {
                    spawners.add(spawner.getName());
                }
            }
        }

        if (spawners.isEmpty()) {
            MessageManager.sendMessage(player, "&cThere are no spawners nearby");
            return;
        } else {
            MessageManager.sendMessage(player, "&aSpawners within a &b" + radius + " &ablock radius");
            spawners.forEach(spawner -> MessageManager.sendMessage(player, "&7- &6" + spawner));
        }
     }
}
