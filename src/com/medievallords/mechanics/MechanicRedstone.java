package com.medievallords.mechanics;

import com.medievallords.utils.DungeonLineConfig;
import com.medievallords.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MechanicRedstone extends Mechanic {

    private Location location;

    public MechanicRedstone(DungeonLineConfig lineConfig) {
        super(lineConfig);

        this.location = LocationUtil.deserializeLocation(lineConfig.getString("location", "'@w;world:@x;0.0:@y;0.0:@z;0.0:@p;0.0:@ya;0.0'"));
    }

    public boolean cast() {
        Block block = location.getBlock();
        if (block == null || block.getType() == Material.AIR) {
            location.getBlock().setType(Material.REDSTONE_BLOCK);
            return true;
        } else {
            block.setType(Material.AIR);
            return true;
        }
    }
}
