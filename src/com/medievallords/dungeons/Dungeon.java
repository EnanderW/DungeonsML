package com.medievallords.dungeons;

import com.medievallords.Dungeons;
import com.medievallords.dungeons.instance.DungeonInstance;
import com.medievallords.dungeons.options.Options;
import com.medievallords.spawners.Spawner;
import com.medievallords.utils.Constants;
import com.medievallords.utils.WorldLoader;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by WE on 2017-09-27.
 *
 */

@Getter
@Setter
public class Dungeon {

    private DungeonHandler dungeonHandler = Dungeons.getInstance().getDungeonHandler();

    private String name;

    private World world;
    private List<Spawner> spawners = new ArrayList<>();
    private HashMap<String, Location> locations = new HashMap<>();
    private Options options;

    public Dungeon(String name) {
        this.name = name;
    }

    public void startDungeon(List<Player> players) {
        if (world == null) {
            Bukkit.broadcastMessage("Template world is null");
            return;
        }

        if (dungeonHandler.getInstances().size() >= Constants.MAX_INSTANCES) {
            Bukkit.broadcastMessage("Max instances");
            return;
        }

        if (!locations.containsKey("spawn") || !locations.containsKey("lobby")) {
            Bukkit.broadcastMessage("No spawn points");
            return;
        }

        Constants.INSTANCES++;
        int id = Constants.INSTANCES;

        World newWorld = WorldLoader.createWorld(world, id);
        if (newWorld == null) {
            Bukkit.broadcastMessage("World null");
            return;
        }

        DungeonInstance instance = new DungeonInstance(id, newWorld, Dungeon.this, players);
        dungeonHandler.getInstances().add(instance);
        instance.prepare();
    }
}
