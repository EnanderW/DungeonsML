package com.medievallords.spawners;

import com.medievallords.Dungeons;
import com.medievallords.dungeons.DungeonHandler;
import com.medievallords.dungeons.instance.DungeonInstance;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.List;

/**
 * Created by WE on 2017-09-27.
 *
 */

public class SpawnerListener implements Listener {

    private DungeonHandler dungeonHandler = Dungeons.getInstance().getDungeonHandler();

    /*@EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        World world = event.getWorld();
        if (!world.getName().startsWith("dungeonInstance_")) {
            return;
        }

        String[] split = world.getName().split("_");

        DungeonInstance instance = dungeonHandler.getInstance(Integer.parseInt(split[1]));
        if (instance == null) {
            return;
        }

        Bukkit.broadcastMessage("After instance");

        List<Spawner> spawners = dungeonHandler.getSpawners(event.getChunk());
        if (spawners.isEmpty()) {
            return;
        }

        Bukkit.broadcastMessage("Get some spawners");

        for (Spawner spawner : spawners) {
            if (!instance.getMaxSpawns().containsKey(spawner.getName())) {
                instance.getMaxSpawns().put(spawner.getName(), 1);
                spawner.spawn(world);
                Bukkit.broadcastMessage("Put 1");
            } else {
                int amount = instance.getMaxSpawns().get(spawner.getName());
                if (amount >= spawner.getMaxSpawns()) {
                    return;
                }

                Bukkit.broadcastMessage("Not over");
                instance.getMaxSpawns().put(spawner.getName(), amount++);
                spawner.spawn(world);
            }
        }
    }*/

}
