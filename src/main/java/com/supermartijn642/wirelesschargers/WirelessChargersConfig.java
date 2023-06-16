package com.supermartijn642.wirelesschargers;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

/**
 * Created 1/25/2021 by SuperMartijn642
 */
public class WirelessChargersConfig {

    public static final Supplier<Integer> basicBlockChargerRange;
    public static final Supplier<Integer> basicBlockChargerCapacity;
    public static final Supplier<Integer> basicBlockChargerTransferRate;

    public static final Supplier<Integer> advancedBlockChargerRange;
    public static final Supplier<Integer> advancedBlockChargerCapacity;
    public static final Supplier<Integer> advancedBlockChargerTransferRate;

    public static final Supplier<Integer> basicPlayerChargerRange;
    public static final Supplier<Integer> basicPlayerChargerCapacity;
    public static final Supplier<Integer> basicPlayerChargerTransferRate;

    public static final Supplier<Integer> advancedPlayerChargerRange;
    public static final Supplier<Integer> advancedPlayerChargerCapacity;
    public static final Supplier<Integer> advancedPlayerChargerTransferRate;

    static{
        IConfigBuilder builder = ConfigBuilders.newTomlConfig("wirelesschargers", null, false);

        builder.push("General");

        builder.push("Basic Wireless Block Charger");
        basicBlockChargerRange = builder.comment("In what range should the Basic Wireless Block Charger power blocks? For example, a range of 1 means a 3x3x3 area.").define("basicBlockChargerRange", 2, 1, 5);
        basicBlockChargerCapacity = builder.comment("How much energy should the Basic Wireless Block Charger be able to store?").define("basicBlockChargerCapacity", 25000, 1000, 10000000);
        basicBlockChargerTransferRate = builder.comment("How much energy per tick per block should the Basic Wireless Block Charger transfer? 1 second = 20 ticks.").define("basicBlockChargerTransferRate", 50, 10, 10000);
        builder.pop();

        builder.push("Advanced Wireless Block Charger");
        advancedBlockChargerRange = builder.comment("In what range should the Advanced Wireless Block Charger power blocks? For example, a range of 1 means a 3x3x3 area.").define("advancedBlockChargerRange", 3, 1, 5);
        advancedBlockChargerCapacity = builder.comment("How much energy should the Advanced Wireless Block Charger be able to store?").define("advancedBlockChargerCapacity", 100000, 1000, 10000000);
        advancedBlockChargerTransferRate = builder.comment("How much energy per tick per block should the Advanced Wireless Block Charger transfer? 1 second = 20 ticks.").define("advancedBlockChargerTransferRate", 200, 10, 10000);
        builder.pop();

        builder.push("Basic Wireless Player Charger");
        basicPlayerChargerRange = builder.comment("In what range should the Basic Wireless Player Charger power players' items? For example, a range of 1 means a 3x3x3 area.").define("basicPlayerChargerRange", 4, 1, 10);
        basicPlayerChargerCapacity = builder.comment("How much energy should the Basic Wireless Player Charger be able to store?").define("basicPlayerChargerCapacity", 25000, 1000, 10000000);
        basicPlayerChargerTransferRate = builder.comment("How much energy per tick per block should the Basic Wireless Player Charger transfer? 1 second = 20 ticks.").define("basicPlayerChargerTransferRate", 50, 10, 10000);
        builder.pop();

        builder.push("Advanced Wireless Player Charger");
        advancedPlayerChargerRange = builder.comment("In what range should the Advanced Wireless Player Charger power players' items? For example, a range of 1 means a 3x3x3 area.").define("advancedPlayerChargerRange", 6, 1, 10);
        advancedPlayerChargerCapacity = builder.comment("How much energy should the Advanced Wireless Player Charger be able to store?").define("advancedPlayerChargerCapacity", 100000, 1000, 10000000);
        advancedPlayerChargerTransferRate = builder.comment("How much energy per tick per block should the Advanced Wireless Player Charger transfer? 1 second = 20 ticks.").define("advancedPlayerChargerTransferRate", 200, 10, 10000);
        builder.pop();

        builder.pop();

        builder.build();
    }

}
