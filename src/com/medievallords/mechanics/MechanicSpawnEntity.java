package com.medievallords.mechanics;

import com.medievallords.mechanics.targeters.instances.ITargetLocation;
import com.medievallords.utils.DungeonLineConfig;
import com.medievallords.utils.ItemBuilder;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.*;

public class MechanicSpawnEntity extends Mechanic implements ITargetLocation {

    private HashMap<String, Integer> entities = new HashMap<>();
    private MobManager mobManager = MythicMobs.inst().getMobManager();

    public MechanicSpawnEntity(DungeonLineConfig lineConfig) {
        super(lineConfig);

        String[] splitItems = lineConfig.getString("entities", "ZOMBIE:3,").split(",");
        for (String key : splitItems) {
            int amount = 1;
            String[] amountSplit = key.split(":");
            String mobName = amountSplit[0];
            if (amountSplit.length == 2) {
                try {
                    amount = Integer.parseInt(amountSplit[1]);
                } catch (NumberFormatException e) {
                }
            }

            entities.put(mobName, amount);
        }
    }

    @Override
    public boolean cast(Location location) {
        World world = location.getWorld();

        for (String entityName : entities.keySet()) {
            MythicMob mob = mobManager.getMythicMob(entityName);
            if (mob != null) {
                int amount = 0;
                while (amount <= entities.get(entityName)) {
                    mobManager.spawnMob(entityName, location);
                    amount++;
                }
            } else {
                try {
                    EntityType entityType = EntityType.valueOf(entityName.toUpperCase());
                    int amount = 0;
                    while (amount <= entities.get(entityName)) {
                        world.spawnEntity(location, entityType);
                        amount++;
                    }
                } catch (IllegalArgumentException e) {

                }

            }
        }

        return true;
    }
}
