package com.medievallords.spawners;

import com.medievallords.dungeons.Dungeon;
import com.medievallords.utils.DungeonLineConfig;
import com.medievallords.utils.LocationUtil;
import com.medievallords.utils.MathUtil;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by WE on 2017-09-27.
 *
 */

@Getter
@Setter
public class Spawner {

    private String name;

    private Dungeon dungeon;
    private HashMap<String, Integer> mobs = new HashMap<>();
    private double[] location = new double[3];
    private double radius = 0;

    private int maxSpawns = 1;

    public Spawner(String name, Dungeon dungeon, Location location) {
        this.name = name;
        this.dungeon = dungeon;
        this.location[0] = location.getX();
        this.location[1] = location.getY();
        this.location[2]  = location.getZ();
    }

    public Spawner(String name, Dungeon dungeon, Location location, HashMap<String, Integer> mobs, double radius) {
        this.name = name;
        this.dungeon = dungeon;
        this.location[0] = location.getX();
        this.location[1] = location.getY();
        this.location[2]  = location.getZ();
        this.mobs = mobs;
        this.radius = radius;
    }

    public Location getLocation(World world) {
        return new Location(world, location[0], location[1], location[2]);
    }

    public void spawn(World world) {
        Location spawnLocation = new Location(world, location[0], location[1], location[2]);

        for (String mob : mobs.keySet()) {
            int amount = mobs.get(mob);
            if (amount == 1) {
                Location random = LocationUtil.findSafeSpot(spawnLocation, radius);
                if (!random.getChunk().isLoaded()) {
                    random.getChunk().load();
                }

                MythicMobs.inst().getMobManager().spawnMob(mob, random);
            } else {
                for (int i = 1; i < amount; i++) {
                    Location random = LocationUtil.findSafeSpot(spawnLocation, radius);
                    if (!random.getChunk().isLoaded()) {
                        random.getChunk().load();
                    }

                    MythicMobs.inst().getMobManager().spawnMob(mob, random);
                }
            }
        }
    }

    public boolean isPlayerNearby(World world) {
        Location location = getLocation(world);

        for (Entity entity : world.getNearbyEntities(location, 40, 40, 40)) {
            if (entity instanceof Player) {
                return true;
            }
        }

        return false;
    }
}
