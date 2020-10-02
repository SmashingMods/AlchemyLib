package al132.alib;

import al132.alib.blocks.ABaseTileBlock;
import al132.alib.tiles.ABaseTile;
import al132.alib.tiles.GuiTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;

@Mod("alib")
public class ALib {
    //private static final Logger LOGGER = LogManager.getLogger();

    public ALib() {
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        //MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onRightClick);
    }

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
                        boolean interactionCompleted = ((ABaseTile) tile).onBlockActivated(state, world, pos, player, hand);
                        if (!interactionCompleted && tile instanceof GuiTile) {
                            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, pos);
                        }
                    }
                    e.setCanceled(true);
                }
            }
        }
    }
}