package com.medievallords.dungeons.commands;

import com.medievallords.dungeons.Dungeon;
import com.medievallords.utils.BaseCommand;
import com.medievallords.utils.Command;
import com.medievallords.utils.CommandArgs;
import com.medievallords.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WE on 2017-09-28.
 *
 */

public class TestJoinCommand extends BaseCommand implements Listener {

    private List<Player> players = new ArrayList<>();

    @Command(name = "dungeon.join", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        Dungeon dungeon = getDungeonHandler().getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cNo dungeon");
            return;
        }

        if (players.isEmpty()) {
            MessageManager.sendMessage(player, "&cNot enough players");
            return;
        }

        dungeon.startDungeon(players);
        players.clear();
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (players.contains(event.getPlayer())) return;
        players.add(event.getPlayer());
    }
}
