package com.medievallords.utils;

import com.medievallords.Dungeons;
import com.medievallords.dungeons.DungeonHandler;
import lombok.Getter;

@Getter
public class BaseCommand {

    private Dungeons main = Dungeons.getInstance();
    private DungeonHandler dungeonHandler = main.getDungeonHandler();

    public BaseCommand() {
        main.getCommandFramework().registerCommands(this);
    }
}
