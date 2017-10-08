package com.medievallords.mechanics;

import com.medievallords.mechanics.data.MechanicData;
import com.medievallords.mechanics.targeters.LocationTarget;
import com.medievallords.mechanics.targeters.PIRTarget;
import com.medievallords.mechanics.targeters.PlayerTarget;
import com.medievallords.mechanics.targeters.Target;
import com.medievallords.mechanics.targeters.instances.ITargetEntity;
import com.medievallords.mechanics.targeters.instances.ITargetLocation;
import com.medievallords.utils.DungeonLineConfig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class Mechanic {

    private Target target;

    public Mechanic(DungeonLineConfig lineConfig) {
        String targetName = lineConfig.getString("target", "LT");
        this.target = Target.getTarget(targetName);

        System.out.println("CONSTRUCTOR " + targetName);
    }

    public void runMechanic(MechanicData data) {
        if (this instanceof MechanicRedstone) {
            ((MechanicRedstone) this).cast();
        }

        if (target instanceof LocationTarget) {
            data.getTargetLocations().add(((LocationTarget) target).getLocation(data));
        } else if (target instanceof PIRTarget) {
            List<Player> players = ((PIRTarget) target).getPlayers(data);
            data.getTargetEntities().addAll(players);
        }

        if (this instanceof ITargetLocation) {
            if (target instanceof LocationTarget) {
                ((ITargetLocation) this).cast(data.getTargetLocations().get(0));
            }
        } else if (this instanceof ITargetEntity) {
            if (target instanceof PIRTarget) {
                for (Entity entity : data.getTargetEntities()) ((ITargetEntity) this).cast(entity);

            } else if (target instanceof PlayerTarget) {
                ((ITargetEntity) this).cast(data.getTrigger());
            }
        }
    }
    public static Mechanic getMechanic(String name, List<String> data) {
        switch(name.toLowerCase()) {
            case "dropitem":
                return new MechanicDropItem(new DungeonLineConfig(data));
            case "message":
                return new MechanicMessage(new DungeonLineConfig(data));
            case "spawnentity":
                return new MechanicSpawnEntity(new DungeonLineConfig(data));
            case "redstone":
                return new MechanicRedstone(new DungeonLineConfig(data));
        }

        return null;
    }
}
