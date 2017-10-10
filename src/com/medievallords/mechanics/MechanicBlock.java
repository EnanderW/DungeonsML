package com.medievallords.mechanics;

import com.medievallords.utils.DungeonLineConfig;
import com.medievallords.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MechanicBlock extends Mechanic {

    private Location location;
    private Material material;

    public MechanicBlock(DungeonLineConfig lineConfig) {
        super(lineConfig);

        this.location = LocationUtil.deserializeLocation(lineConfig.getString("location", "'@w;world:@x;0.0:@y;0.0:@z;0.0:@p;0.0:@ya;0.0'"));
        this.material = Material.getMaterial(lineConfig.getString("material", "REDSTONE_BLOCK"));
    }

    public boolean cast() {
        Block block = location.getBlock();
        if (block == null || block.getType() != material) {
            location.getBlock().setType(material);
            return true;
        } else {
            block.setType(Material.AIR);
            return true;
        }
    }
}
