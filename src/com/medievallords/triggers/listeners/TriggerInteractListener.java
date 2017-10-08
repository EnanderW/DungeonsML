package com.medievallords.triggers.listeners;

import com.medievallords.triggers.InteractTrigger;
import com.medievallords.triggers.MobTrigger;
import com.medievallords.triggers.Trigger;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class TriggerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        List<Trigger> interactTriggers = Trigger.getTriggers(InteractTrigger.class);

        for (Trigger trigger : interactTriggers) {
            if (trigger.getLocation().equals(block.getLocation())) {
                ((InteractTrigger) trigger).trigger(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBossDeath(MythicMobDeathEvent event) {
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        List<Trigger> interactTriggers = Trigger.getTriggers(MobTrigger.class);

        for (Trigger trigger : interactTriggers) {
            MobTrigger mobTrigger = (MobTrigger) trigger;
            if (mobTrigger.getMobType().equalsIgnoreCase(event.getMob().getType().getInternalName()) && mobTrigger.getLocation().getWorld().equals(location.getWorld())) {
                mobTrigger.trigger(event.getKiller(), location);
            }
        }
    }
}
