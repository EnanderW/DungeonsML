package com.medievallords.triggers;

import com.medievallords.Dungeons;
import com.medievallords.mechanics.Mechanic;
import com.medievallords.mechanics.data.MechanicData;
import com.medievallords.utils.DungeonLineConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DistanceTrigger extends Trigger {

    // 1 = ONCE
    // 2 = ONCE PER PERSON
    // 3 = UNLIMITED
    // 4 = REPEATING AS LONG AS SOMEONE IS CLOSE

    private int state;
    private double distance;
    private int interacts = 0;
    private List<UUID> playersInteracted = new ArrayList<>();

    public DistanceTrigger(String name, Location location, DungeonLineConfig dlc, File file, ConfigurationSection cs) {
        super(name, location, dlc, file, cs);

        this.distance = dlc.getDouble("distance", 10);
        this.state = dlc.getInt("state", 1);
        if (state == 4) {

            long period = dlc.getInt("period", 100);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Entity entity = playerIsNearby();
                    if (entity != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                trigger(entity);
                            }
                        }.runTask(Dungeons.getInstance());
                    }
                }
            }.runTaskTimerAsynchronously(Dungeons.getInstance(), 0, period);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Entity entity = playerIsNearby();
                    if (entity != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (state == 1 && interacts >= 1) {
                                    cancel();
                                    return;
                                }

                                trigger(entity);
                            }
                        }.runTask(Dungeons.getInstance());
                    }
                }
            }.runTaskTimerAsynchronously(Dungeons.getInstance(), 0, 20);
        }
    }

    private Entity playerIsNearby() {
        World world = getLocation().getWorld();
        for (Entity entity : world.getNearbyEntities(getLocation(), distance, distance, distance)) {
            if (entity instanceof Player) {
                return entity;
            }
        }

        return null;
    }


    public void trigger(Entity entity) {
        if (state == 1 && interacts >= 1) {
            return;
        } else if (state == 2 && playersInteracted.contains(entity.getUniqueId())) {
            return;
        }

        MechanicData data = new MechanicData(entity, getLocation());
        for (Mechanic mechanic : mechanics) {
            mechanic.runMechanic(data);
        }

        playersInteracted.add(entity.getUniqueId());
        interacts++;
    }
}
