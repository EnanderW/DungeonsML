package com.medievallords.mechanics.targeters;

import com.medievallords.mechanics.data.MechanicData;
import org.bukkit.entity.Player;

public class PlayerTarget extends Target {

    public PlayerTarget(String params) {
        super(params);
    }

    public Player getPlayer(MechanicData data) {
        return (Player) data.getTrigger();
    }
}
