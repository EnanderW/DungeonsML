package com.medievallords.mechanics;

import com.medievallords.mechanics.targeters.instances.ITargetEntity;
import com.medievallords.utils.DungeonLineConfig;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

public class MechanicMessage extends Mechanic implements ITargetEntity {

    private String message;

    public MechanicMessage(DungeonLineConfig lineConfig) {
        super(lineConfig);

        this.message = lineConfig.getString("message", "");
    }

    @Override
    public boolean cast(Entity entity) {
        entity.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }
}
