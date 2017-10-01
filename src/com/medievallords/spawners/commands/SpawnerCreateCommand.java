package com.medievallords.spawners.commands;

import com.medievallords.dungeons.Dungeon;
import com.medievallords.utils.BaseCommand;
import com.medievallords.utils.Command;
import com.medievallords.utils.CommandArgs;
import com.medievallords.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by WE on 2017-09-29.
 *
 */

public class SpawnerCreateCommand extends BaseCommand {

    @Command(name = "dspawner.create", permission = "dungeons.commands.admin", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: &7/spawner create <name> <dungeon>");
            return;
        }

        Dungeon dungeon = getDungeonHandler().getDungeon(args[1]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
            return;
        }

        if (getDungeonHandler().getSpawner(args[0]) != null) {
            MessageManager.sendMessage(player, "&cThere is already a spawner with that name");
            return;
        }

        getDungeonHandler().createSpawner(args[0], dungeon, player.getTargetBlock((Set<Material>) null, 5).getLocation());
        getDungeonHandler().save();
        MessageManager.sendMessage(player, "&aA spawner has been created where you're looking");
    }
}
