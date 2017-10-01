package com.medievallords.spawners.commands;

import com.medievallords.spawners.Spawner;
import com.medievallords.utils.BaseCommand;
import com.medievallords.utils.Command;
import com.medievallords.utils.CommandArgs;
import com.medievallords.utils.MessageManager;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-09-29.
 *
 */

public class SpawnerSetMobCommand extends BaseCommand {

    @Command(name = "dspawner.setmob", permission = "dungeons.commands.admin", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 3) {
            MessageManager.sendMessage(player, "&cUsage: &7/spawner setmob <spawner> <mob> <amount>");
            return;
        }

        Spawner spawner = getDungeonHandler().getSpawner(args[0]);
        if (spawner == null) {
            MessageManager.sendMessage(player, "&cCould not find a spawner with that name");
            return;
        }

        MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(args[1]);
        if (mob == null) {
            MessageManager.sendMessage(player, "&cCould find a mob with that name");
            return;
        }

        int amount = 1;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cYou need to specify a number for the amount");
            return;
        }

        spawner.getMobs().put(mob.getInternalName(), amount);
        getDungeonHandler().save();
        MessageManager.sendMessage(player, "&aMob &6" + args[1] + "&7:&6" + amount + " &6has been set");
    }
}
