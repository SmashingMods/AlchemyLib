package al132.alib.blocks;


import al132.alib.tiles.ABaseTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ABaseTileBlock<T extends BlockEntity, U extends AbstractContainerMenu> extends ABaseBlock implements EntityBlock {

    Class<T> entityClass;
    Class<U> containerClass;

    public ABaseTileBlock(Block.Properties properties, Class<T> entityClass, Class<U> containerClass) {
        super(properties);
        this.entityClass = entityClass;
        this.containerClass = containerClass;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> output = super.getDrops(state, builder);
        ItemStack stack = output.stream().filter(x -> Block.byItem(x.getItem()) == this).findFirst().orElse(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            stack.setTag(builder.getParameter(LootContextParams.BLOCK_ENTITY).getUpdateTag());
            stack.getTag().remove("x");
            stack.getTag().remove("y");
            stack.getTag().remove("z");
        }
        return output;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof ABaseTile && stack.hasTag()) {
            stack.getTag().putInt("x", pos.getX());
            stack.getTag().putInt("y", pos.getY());
            stack.getTag().putInt("z", pos.getZ());
            tile.load(stack.getTag());
            //((ABaseTile) tile).markDirtyClient();
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        try {
            return entityClass.getConstructor(BlockPos.class, BlockState.class).newInstance(pos, state);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity tile = level.getBlockEntity(pos);
            boolean interactionSuccessful = true;
            if (tile instanceof ABaseTile) {
                interactionSuccessful = ((ABaseTile) tile).onBlockActivated(state, level, pos, player, hand, trace);
            }
            MenuProvider provider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return new TextComponent("");
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player p_39956_) {
                    try {
                        return containerClass.getConstructor(int.class, Level.class, BlockPos.class, Inventory.class)
                                .newInstance(id, level, pos, inv);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        return null;
                    }
                }
            };

            if (!interactionSuccessful) {
                if (tile instanceof Nameable) {
                    NetworkHooks.openGui((ServerPlayer) player, provider, tile.getBlockPos());
                } else {
                    throw new IllegalStateException("The named container provider is missing!");
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }
/*
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
*/

}