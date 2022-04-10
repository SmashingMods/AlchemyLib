package com.smashingmods.alchemylib;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AlchemyLib.MODID)
public class AlchemyLib {
    public static final String MODID = "alchemylib";
    private static final Logger LOGGER = LogManager.getLogger();

    public AlchemyLib() {
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        //MinecraftForge.EVENT_BUS.register(this);
        //MinecraftForge.EVENT_BUS.addListener(this::onRightClick);
    }

    /*
    private void onRightClick(RightClickBlock e) {
        World world = e.getWorld();
        BlockPos pos = e.getPos();
        PlayerEntity player = e.getPlayer();
        BlockState state = world.getBlockState(pos);
        Hand hand = e.getHand();
        ItemStack held = e.getPlayer().getHeldItem(hand);
        if (hand == Hand.MAIN_HAND && world.getBlockState(pos).getBlock() instanceof ABaseTileBlock) {
            if (!(player.isCrouching() && Block.getBlockFromItem(held.getItem()) != Blocks.AIR)) {
                TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof ABaseTile) {
                    if (!world.isRemote) {
                        ActionResultType interactionCompleted = ((ABaseTile) tile).onBlockActivated(state, world, pos, player, hand, hit);
                        if (!interactionCompleted.isSuccessOrConsume() && tile instanceof GuiTile) {
                            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, pos);
                        }
                    }
                    e.setCanceled(true);
                }
            }
        }
    }*/
}