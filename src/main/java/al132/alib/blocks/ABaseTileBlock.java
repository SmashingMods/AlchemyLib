package al132.alib.blocks;


import al132.alib.ModData;
import al132.alib.tiles.ABaseTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ABaseTileBlock extends ABaseBlock {

    Supplier<TileEntity> tileSupplier;

    public ABaseTileBlock(ModData data, String name, Block.Properties properties, Supplier<TileEntity> tileSupplier) {
        super(data, name, properties);
        this.tileSupplier = tileSupplier;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> output = super.getDrops(state, builder);
        ItemStack stack = output.stream().filter(x -> Block.getBlockFromItem(x.getItem()) == this).findFirst().orElse(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            stack.setTag(builder.get(LootParameters.BLOCK_ENTITY).getUpdateTag());
            stack.getTag().remove("x");
            stack.getTag().remove("y");
            stack.getTag().remove("z");
        }
        return output;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tile  = worldIn.getTileEntity(pos);
        if (tile instanceof ABaseTile && stack.hasTag()){
                stack.getTag().putInt("x", pos.getX());
                stack.getTag().putInt("y", pos.getY());
                stack.getTag().putInt("z", pos.getZ());
                tile.read(stack.getTag());
                ((ABaseTile) tile).markDirtyClient();
            }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return tileSupplier.get();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            boolean interactionSuccessful = true;
            if (tile instanceof ABaseTile) {
                interactionSuccessful = ((ABaseTile) tile).onBlockActivated(state, world, pos, player, hand, result);
            }
            if (!interactionSuccessful) {
                if (tile instanceof INamedContainerProvider) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());
                } else {
                    throw new IllegalStateException("Our named container provider is missing!");
                }
            }
            return true;
        }
        return super.onBlockActivated(state, world, pos, player, hand, result);
    }
}