package com.medievallords.dungeons.commands;

import com.medievallords.utils.BaseCommand;
import com.medievallords.utils.Command;
import com.medievallords.utils.CommandArgs;
import com.medievallords.utils.MessageManager;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-09-27.
 *
 */

public class DungeonCreateCommand extends BaseCommand {

    @Command(name = "dungeon.create", permission = "dungeon.commands.creator", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon create <name>");
            return;
        }

        String name = args[0];

        if (getDungeonHandler().getDungeon(name) != null) {
            MessageManager.sendMessage(player, "&cThere is already a dungeon with that name");
            return;
        }

        getDungeonHandler().createDungeon(name);

        MessageManager.sendMessage(player, "&6Dungeon &b" + name + "&6 has been created.");
    }
}
