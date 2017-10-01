package com.medievallords.player;

import com.medievallords.dungeons.instance.DungeonInstance;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by WE on 2017-09-27.
 *
 */

@Getter
@Setter
public class DPlayer {

    private Player player;
    private DungeonInstance instance;

    private boolean inCombat = false;

    private boolean ready = false;

    public DPlayer(Player player, DungeonInstance instance) {
        this.player = player;
        this.instance = instance;
    }
}
