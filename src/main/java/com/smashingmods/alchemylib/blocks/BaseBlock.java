package com.smashingmods.alchemylib.blocks;


import net.minecraft.world.level.block.Block;


abstract public class BaseBlock extends Block {

    public BaseBlock(Properties properties) {
        super(properties);
        //setRegistryName(modid, name);
        //data.BLOCKS.add(this);
    }


    //@Override
    /*
    public ActionResultType use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            BlockEntity tile = world.getBlockEntity(pos);
            boolean interactionSuccessful = true;
            if (tile instanceof ABaseTile) {
                interactionSuccessful = ((ABaseTile) tile).onBlockActivated(state, world, pos, player, hand, hit);
            }
            if (!interactionSuccessful) {
                if (tile instanceof Nameable) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, (Nameable) tile, tile.getBlockPos());
                } else {
                    throw new IllegalStateException("The named container provider is missing!");
                }
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }
    */
}