package com.medievallords.mechanics.targeters;


import com.medievallords.utils.DungeonLineConfig;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Target {

    protected DungeonLineConfig dlc;

    public Target(String params) {
        if (params.contains(";")) {
            String[] split = params.split(";");
            this.dlc = new DungeonLineConfig(Arrays.asList(split));
        } else {
            this.dlc = new DungeonLineConfig(new ArrayList<>());
        }
    }

    public static Target getTarget(String name) {
        String finalString = name;
        String rest = name;
        if (finalString.contains("(") && finalString.contains(")")) {
            String[] split = finalString.split("\\(");
            rest = split[0];
            finalString = split[1];
            split = finalString.split("\\)");
            finalString = split[0];
        }

        System.out.println("GETTARGET " + finalString);
        System.out.println("GETTARGET " + rest);

        switch (rest.toUpperCase()) {
            case "PIR":
                return new PIRTarget(finalString);
            case "PT":
                return new PlayerTarget(finalString);
            case "LT":
                return new LocationTarget(finalString);
        }

        return null;
    }

}
