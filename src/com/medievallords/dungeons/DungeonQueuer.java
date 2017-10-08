package com.medievallords.dungeons;

import com.medievallords.Dungeons;
import com.medievallords.utils.MessageManager;
import org.bukkit.entity.Player;

import java.util.List;

public class DungeonQueuer {

    private DungeonHandler dungeonHandler = Dungeons.getInstance().getDungeonHandler();

    public void startDungeon(List<Player> playerList, String dungeonName) {
        Dungeon dungeon = dungeonHandler.getDungeon(dungeonName);
        if (dungeon == null) {
            playerList.forEach(player -> MessageManager.sendMessage(player, "&cThere's no such dungeon"));
            return;
        }

        dungeon.startDungeon(playerList);
    }



}
