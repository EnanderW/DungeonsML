package com.medievallords.dungeons.commands;

import com.medievallords.dungeons.Dungeon;
import com.medievallords.utils.BaseCommand;
import com.medievallords.utils.Command;
import com.medievallords.utils.CommandArgs;
import com.medievallords.utils.MessageManager;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import org.bukkit.entity.Player;

public class DungeonEditCommand extends BaseCommand {

    @Command(name = "dungeon.edit", permission = "dungeons.administrator", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon edit <dungeon>");
            return;
        }

        Dungeon dungeon = getDungeonHandler().getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
            return;
        }

        for (MythicSpawner spawner : dungeon.getSpawners()) {
            MessageManager.sendMessage(player, spawner.getInternalName());
        }
    }
}
