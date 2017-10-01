package com.medievallords.dungeons.commands;

import com.medievallords.dungeons.Dungeon;
import com.medievallords.utils.BaseCommand;
import com.medievallords.utils.Command;
import com.medievallords.utils.CommandArgs;
import com.medievallords.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by WE on 2017-09-28.
 *
 */

public class DungeonSetCommand extends BaseCommand {

    @Command(name = "dungeon.set", permission = "dungeon.command.admin", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 3) {
            Dungeon dungeon = getDungeonHandler().getDungeon(args[0]);
            if (dungeon == null) {
                MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
                return;
            }

            if (args[1].equalsIgnoreCase("location")) {
                dungeon.getLocations().put(args[2], player.getTargetBlock((Set<Material>) null, 10).getLocation());
                MessageManager.sendMessage(player, "&6Location &b" + args[2] + "&6 has been set to where you're looking");
                getDungeonHandler().save();
            } else if (args[1].equalsIgnoreCase("world")) {
                World world = Bukkit.getWorld(args[2]);
                if (world == null) {
                    MessageManager.sendMessage(player, "&cCould not find a world with that name");
                    return;
                }

                dungeon.setWorld(world);
                MessageManager.sendMessage(player, "&6World &b" + args[2] + "&6 has been set");
                getDungeonHandler().save();
            }
        }
    }
}
