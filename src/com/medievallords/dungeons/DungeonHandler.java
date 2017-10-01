package com.medievallords.dungeons;

import com.medievallords.Dungeons;
import com.medievallords.dungeons.instance.DungeonInstance;
import com.medievallords.player.DPlayer;
import com.medievallords.spawners.Spawner;
import com.medievallords.utils.LocationUtil;
import com.medievallords.utils.WorldLoader;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by WE on 2017-09-27.
 *
 */

@Getter
public class DungeonHandler {

    private Dungeons main = Dungeons.getInstance();

    private List<Dungeon> dungeons = new ArrayList<>();

    private List<DungeonInstance> instances = new ArrayList<>();

    public void load() {
        ConfigurationSection cs = main.getDungeonsFileConfiguration().getConfigurationSection("Dungeons");
        if (cs == null) {
            main.getDungeonsFileConfiguration().createSection("Dungeons");
            main.saveConfiguration(main.getDungeonsFileConfiguration(), main.getDungeonsFile(), "dungeons.yml");
            return;
        }

        dungeons.clear();

        for (String key : cs.getKeys(false)) {
            HashMap<String, Location> locations = new HashMap<>();

            World world = null;
            if (cs.getString(key + ".World") != null) {
                world = Bukkit.getWorld(cs.getString(key + ".World"));
            }

            for (String location : cs.getStringList(key + ".Locations")) {
                String[] split = location.split(",");
                locations.put(split[0], LocationUtil.deserializeLocation(split[1]));
            }

            Dungeon dungeon = new Dungeon(key);
            dungeon.setWorld(world);
            dungeon.setLocations(locations);

            dungeons.add(dungeon);

            ConfigurationSection spawnerSection = cs.getConfigurationSection(key + ".Spawners");
            if (spawnerSection != null) {
                for (String spawnerName : spawnerSection.getKeys(false)) {
                    Location location = null;
                    if (spawnerSection.getString(spawnerName + ".Location") != null) {
                        location = LocationUtil.deserializeLocation(spawnerSection.getString(spawnerName + ".Location"));
                    }

                    double radius = spawnerSection.getDouble(spawnerName + ".Radius");

                    HashMap<String, Integer> mobMap = new HashMap<>();

                    if (spawnerSection.getStringList(spawnerName + ".Mobs") != null) {
                        for (String mobs : spawnerSection.getStringList(spawnerName + ".Mobs")) {
                            String[] split = mobs.split(",");
                            mobMap.put(split[0], Integer.parseInt(split[1]));
                        }
                    }

                    int maxSpawns = spawnerSection.getInt(spawnerName + ".MaxSpawns");

                    Spawner spawner = new Spawner(spawnerName, dungeon, location, mobMap, radius);
                    spawner.setMaxSpawns(maxSpawns);
                    dungeon.getSpawners().add(spawner);
                }
            }
        }
    }

    public void save() {
        ConfigurationSection cs = main.getDungeonsFileConfiguration().getConfigurationSection("Dungeons");
        if (cs == null) {
            cs = main.getDungeonsFileConfiguration().createSection("Dungeons");
        }

        for (int i = 0; i < dungeons.size(); i++) {
            Dungeon dungeon = dungeons.get(i);

            String name = dungeon.getName();
            HashMap<String, Location> locations = dungeon.getLocations();
            World world = dungeon.getWorld();
            List<String> ser = new ArrayList<>();
            for (String location : locations.keySet()) {
                ser.add(location + "," + LocationUtil.serializeLocation(locations.get(location)));
            }

            cs.createSection(name);
            cs.set(name + ".Locations", ser);

            if (world != null)
            cs.set(name + ".World", world.getName());

            ConfigurationSection spawnerSection = cs.createSection(name + ".Spawners");

            for (int s = 0; s < dungeon.getSpawners().size(); s++) {
                Spawner spawner = dungeon.getSpawners().get(s);
                String spawnerName = spawner.getName();
                spawnerSection.createSection(spawnerName);
                List<String> mobString = new ArrayList<>();
                for (String mobName : spawner.getMobs().keySet()) {
                    int amount = spawner.getMobs().get(mobName);
                    mobString.add(mobName + "," + amount);
                }

                spawnerSection.set(spawnerName + ".Mobs", mobString);
                if (world != null)
                spawnerSection.set(spawnerName + ".Location", LocationUtil.serializeLocation(spawner.getLocation(world)));

                spawnerSection.set(spawnerName + ".Radius", spawner.getRadius());
                spawnerSection.set(spawnerName + ".MaxSpawns", spawner.getMaxSpawns());
            }
        }

        main.saveConfiguration(main.getDungeonsFileConfiguration(), main.getDungeonsFile(), "dungeons.yml");
    }

    public void createDungeon(String name) {
        Dungeon dungeon = new Dungeon(name);
        dungeons.add(dungeon);
        main.getDungeonsFileConfiguration().set("Dungeons", null);
        main.saveConfiguration(main.getDungeonsFileConfiguration(), main.getDungeonsFile(), "dungeons.yml");
        save();
    }

    public Dungeon getDungeon(String name) {
        for (int i = 0; i < dungeons.size(); i++) {
            Dungeon dungeon = dungeons.get(i);
            if (dungeon.getName().equalsIgnoreCase(name)) {
                return dungeon;
            }
        }

        return null;
    }

    public void createSpawner(String name, Dungeon dungeon, Location location) {
        Spawner spawner = new Spawner(name, dungeon, location);
        dungeon.getSpawners().add(spawner);
    }

    public Spawner getSpawner(String name) {
        for (int i = 0; i < dungeons.size(); i++) {
            Dungeon dungeon = dungeons.get(i);
            for (int l = 0; l < dungeon.getSpawners().size(); l++) {
                Spawner spawner = dungeon.getSpawners().get(l);
                if (spawner.getName().equalsIgnoreCase(name)) {
                    return spawner;
                }
            }
        }

        return null;
    }

    public List<Spawner> getSpawners(Chunk chunk) {
        List<Spawner> spawners = new ArrayList<>();

        for (int i = 0; i < dungeons.size(); i++) {
            Dungeon dungeon = dungeons.get(i);
            for (int l = 0; l < dungeon.getSpawners().size(); l++) {
                Spawner spawner = dungeon.getSpawners().get(l);
                if (spawner.getLocation(chunk.getWorld()).getChunk().equals(chunk)) {
                    spawners.add(spawner);
                }
            }
        }

        return spawners;
    }

    public DPlayer getDPlayer(UUID uuid) {
        for (int i = 0; i < instances.size(); i++) {
            DungeonInstance instance = instances.get(i);
            for (int l = 0; l < instance.getPlayers().size(); l++) {
                DPlayer dPlayer = instance.getPlayers().get(l);
                if (dPlayer.getPlayer().getUniqueId().equals(uuid)) {
                    return dPlayer;
                }
            }
        }

        return null;
    }

    public DungeonInstance getInstance(int id) {
        for (int i = 0; i < instances.size(); i++) {
            DungeonInstance instance = instances.get(i);
            if (instance.getId() == id) {
                return instance;
            }
        }

        return null;
    }

    public void cancelAll() {
        for (DungeonInstance instance : instances) {
            World world = instance.getWorld();
            for (int i = 0; i < world.getPlayers().size(); i++) {
                Player player = world.getPlayers().get(i);
                player.teleport(new Location(Bukkit.getWorld("world"), -729.5, 104, 317.5));
            }

            instance.cancelTask();
        }
    }
}
