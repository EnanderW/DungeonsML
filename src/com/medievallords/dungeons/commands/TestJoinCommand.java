package com.medievallords.dungeons.commands;

import com.medievallords.dungeons.Dungeon;
import com.medievallords.utils.BaseCommand;
import com.medievallords.utils.Command;
import com.medievallords.utils.CommandArgs;
import com.medievallords.utils.MessageManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WE on 2017-09-28.
 *
 */

public class TestJoinCommand extends BaseCommand {

    @Command(name = "dungeon.join", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        Dungeon dungeon = getDungeonHandler().getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cNo dungeon");
            return;
        }

        List<Player> players = new ArrayList<>();
        players.add(player);
        dungeon.startDungeon(players);
    }
}
