package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.block.BlockShape;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 7/30/2021 by SuperMartijn642
 */
public class ChargerModelType {

    private static final BlockShape BASIC_SHAPE = BlockShape.or(
        BlockShape.createBlockShape(0, 0, 0, 16, 6, 16),
        BlockShape.createBlockShape(0, 6, 5, 2, 11, 11),
        BlockShape.createBlockShape(14, 6, 5, 16, 11, 11),
        BlockShape.createBlockShape(5, 6, 0, 11, 11, 2),
        BlockShape.createBlockShape(5, 6, 14, 11, 11, 16),
        BlockShape.createBlockShape(5, 6, 5, 11, 7, 11),
        BlockShape.createBlockShape(6, 7, 6, 10, 14.5, 10)
    );
    private static final BlockShape ADVANCED_SHAPE = BlockShape.or(
        BlockShape.createBlockShape(0, 0, 0, 16, 6, 16),
        BlockShape.createBlockShape(0, 6, 5, 2, 11, 11),
        BlockShape.createBlockShape(14, 6, 5, 16, 11, 11),
        BlockShape.createBlockShape(5, 6, 0, 11, 11, 2),
        BlockShape.createBlockShape(5, 6, 14, 11, 11, 16),
        BlockShape.createBlockShape(5, 6, 5, 11, 7, 11),
        BlockShape.createBlockShape(6, 7, 6, 10, 14.5, 10)
    );

    private static final BlockShape BASIC_OUTLINE_SHAPE = BlockShape.or(
        BlockShape.createBlockShape(7, 14, 7, 9, 14.5, 9),
        BlockShape.createBlockShape(5, 6, 5, 11, 7, 11),
        BlockShape.createBlockShape(0, 0, 0, 16, 5, 16),
        BlockShape.createBlockShape(0, 6, 5, 1, 11, 11),
        BlockShape.createBlockShape(0, 5, 0, 1, 6, 16),
        BlockShape.createBlockShape(1, 5, 0, 15, 6, 1),
        BlockShape.createBlockShape(15, 6, 5, 16, 11, 11),
        BlockShape.createBlockShape(5, 6, 0, 11, 11, 1),
        BlockShape.createBlockShape(1, 5, 15, 15, 6, 16),
        BlockShape.createBlockShape(5, 6, 15, 11, 11, 16),
        BlockShape.createBlockShape(15, 5, 0, 16, 6, 16),
        BlockShape.createBlockShape(1, 5, 5.5, 2, 10.5, 10.5),
        BlockShape.createBlockShape(14, 5, 5.5, 15, 10.5, 10.5),
        BlockShape.createBlockShape(5.5, 5, 1, 10.5, 10.5, 2),
        BlockShape.createBlockShape(5.5, 5, 14, 10.5, 10.5, 15),
        BlockShape.createBlockShape(3, 5, 3, 13, 6, 13),
        BlockShape.createBlockShape(6.5, 7, 6.5, 9.5, 14, 9.5),
        BlockShape.createBlockShape(6, 7, 7, 6.5, 13.5, 9),
        BlockShape.createBlockShape(9.5, 7, 7, 10, 13.5, 9),
        BlockShape.createBlockShape(7, 7, 6, 9, 13.5, 6.5),
        BlockShape.createBlockShape(7, 7, 9.5, 9, 13.5, 10)
    );
    private static final BlockShape ADVANCED_OUTLINE_SHAPE = BlockShape.or(
        BlockShape.createBlockShape(7, 15, 7, 9, 15.5, 9),
        BlockShape.createBlockShape(0, 0, 0, 16, 5, 16),
        BlockShape.createBlockShape(0, 6, 5, 1, 11, 11),
        BlockShape.createBlockShape(0, 5, 0, 1, 6, 16),
        BlockShape.createBlockShape(1, 5, 0, 15, 6, 1),
        BlockShape.createBlockShape(15, 6, 5, 16, 11, 11),
        BlockShape.createBlockShape(5, 6, 0, 11, 11, 1),
        BlockShape.createBlockShape(1, 5, 15, 15, 6, 16),
        BlockShape.createBlockShape(5, 6, 15, 11, 11, 16),
        BlockShape.createBlockShape(15, 5, 0, 16, 6, 16),
        BlockShape.createBlockShape(1, 5, 5.5, 2, 10.5, 10.5),
        BlockShape.createBlockShape(14, 5, 5.5, 15, 10.5, 10.5),
        BlockShape.createBlockShape(5.5, 5, 1, 10.5, 10.5, 2),
        BlockShape.createBlockShape(5.5, 5, 14, 10.5, 10.5, 15),
        BlockShape.createBlockShape(3, 5, 3, 13, 6, 13),
        BlockShape.createBlockShape(5, 6, 5, 11, 7, 11),
        BlockShape.createBlockShape(6.5, 7, 6.5, 9.5, 15, 9.5),
        BlockShape.createBlockShape(6, 7, 7, 6.5, 14.5, 9),
        BlockShape.createBlockShape(9.5, 7, 7, 10, 14.5, 9),
        BlockShape.createBlockShape(7, 7, 6, 9, 14.5, 6.5),
        BlockShape.createBlockShape(7, 7, 9.5, 9, 14.5, 10)
    );

    public static final ChargerModelType BASIC_WIRELESS_BLOCK_CHARGER = new ChargerModelType(BASIC_SHAPE, BASIC_OUTLINE_SHAPE, "block/basic_wireless_charger", "block/basic_wireless_block_charger_rings", 2 / 16d);
    public static final ChargerModelType ADVANCED_WIRELESS_BLOCK_CHARGER = new ChargerModelType(ADVANCED_SHAPE, ADVANCED_OUTLINE_SHAPE, "block/advanced_wireless_charger", "block/advanced_wireless_block_charger_rings", 3 / 16d);
    public static final ChargerModelType BASIC_WIRELESS_PLAYER_CHARGER = new ChargerModelType(BASIC_SHAPE, BASIC_OUTLINE_SHAPE, "block/basic_wireless_charger", "block/basic_wireless_player_charger_rings", 2 / 16d);
    public static final ChargerModelType ADVANCED_WIRELESS_PLAYER_CHARGER = new ChargerModelType(ADVANCED_SHAPE, ADVANCED_OUTLINE_SHAPE, "block/advanced_wireless_charger", "block/advanced_wireless_player_charger_rings", 3 / 16d);

    private static final ChargerModelType[] values = new ChargerModelType[]{BASIC_WIRELESS_BLOCK_CHARGER, ADVANCED_WIRELESS_BLOCK_CHARGER, BASIC_WIRELESS_PLAYER_CHARGER, ADVANCED_WIRELESS_PLAYER_CHARGER};

    public final BlockShape collisionShape, outlineShape;
    public final ResourceLocation blockModel, ringModel;
    public final double ringYOffset;

    private ChargerModelType(BlockShape collisionShape, BlockShape outlineShape, String blockModel, String ringModel, double ringYOffset){
        this.collisionShape = collisionShape;
        this.outlineShape = outlineShape;
        this.blockModel = new ResourceLocation("wirelesschargers", blockModel);
        this.ringModel = new ResourceLocation("wirelesschargers", ringModel);
        this.ringYOffset = ringYOffset;
    }

    public static ChargerModelType[] values(){
        return values;
    }

}
