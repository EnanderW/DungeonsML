package com.medievallords.dungeons;

import com.medievallords.Dungeons;
import com.medievallords.dungeons.instance.DungeonInstance;
import com.medievallords.dungeons.options.Options;
import com.medievallords.triggers.Trigger;
import com.medievallords.utils.Constants;
import com.medievallords.utils.MessageManager;
import com.medievallords.utils.WorldLoader;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by WE on 2017-09-27.
 *
 */

@Getter
@Setter
public class Dungeon {

    private DungeonHandler dungeonHandler = Dungeons.getInstance().getDungeonHandler();

    private String name;

    private List<MythicSpawner> spawners = new ArrayList<>();

    private World world;
    private HashMap<String, Location> locations = new HashMap<>();
    private Options options;

    public Dungeon(String name) {
        this.name = name;
    }

    public void startDungeon(List<Player> players) {
        if (world == null) {
            Bukkit.getLogger().log(Level.WARNING, "DUNGEON " + name + " DOES NOT HAVE A TEMPLATE WORLD");
            return;
        }

        if (dungeonHandler.getInstances().size() >= Constants.MAX_INSTANCES) {
            players.forEach(player -> MessageManager.sendMessage(player, "&cYou cannot start another dungeon"));
            return;
        }

        if (!locations.containsKey("spawn") || !locations.containsKey("lobby")) {
            Bukkit.getLogger().log(Level.WARNING, "DUNGEON " + name + " DOES NOT HAVE ANY SPAWN POINTS");
            return;
        }

        Constants.INSTANCES++;
        int id = Constants.INSTANCES;

        World newWorld = WorldLoader.createWorld(world, id);
        if (newWorld == null) {
            return;
        }

        List<Trigger> triggers = new ArrayList<>();
        for (int i = 0; i < Trigger.triggers.size(); i++) {
            Trigger trigger = Trigger.triggers.get(i);
            if (trigger.getLocation().getWorld().equals(world)) {
                triggers.add(trigger);
            }
        }

        DungeonInstance instance = new DungeonInstance(id, newWorld, Dungeon.this, players, spawners, triggers);
        dungeonHandler.getInstances().add(instance);
        instance.prepare();
    }
}
