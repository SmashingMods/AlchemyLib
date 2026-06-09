package com.smashingmods.alchemylib.api.block;

import com.mojang.serialization.MapCodec;
import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * AbstractProcessingBlock extends BaseEntityBlock to add helpful
 * methods for simplifying creating a Block for a BlockEntity.
 *
 * @see BaseEntityBlock
 */
@SuppressWarnings("unused")
public class AbstractProcessingBlock extends BaseEntityBlock {

    /**
     * Every concrete {@link BlockBehaviour} must supply a {@link #codec}; {@link BaseEntityBlock}
     * re-declares it abstract. This shared codec satisfies that contract for {@code AbstractProcessingBlock} and
     * every block extending it, so subclasses need not declare their own. It only serializes the block's
     * {@link BlockBehaviour.Properties} (which vanilla encodes as a unit anyway -- see {@code Properties.CODEC}),
     * because the {@link #blockEntityFunction block entity factory} is a behavioural reference with no serialized
     * form; the decode side therefore reconstructs with a no-op factory. These blocks are only ever instantiated
     * through their registry suppliers, never decoded from this codec, so the placeholder factory is never invoked.
     */
    public static final MapCodec<AbstractProcessingBlock> CODEC = simpleCodec(pProperties -> new AbstractProcessingBlock((pPos, pState) -> null));

    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFunction;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    /**
     * The block needs to have a reference to its block entity so that it can return that
     * reference in {@link #newBlockEntity}. The block entity's BlockPos and BlockState can't be known
     * in advance, pass a function that can apply the BlockPos and BlockState at runtime.
     *
     * @param pBlockEntity takes a BiFunction that requires a BlockPos and BlockState and returns a BlockEntity.
     *
     * @see BlockPos
     * @see BlockState
     * @see BlockEntity
     */
    public AbstractProcessingBlock(BiFunction<BlockPos, BlockState, BlockEntity> pBlockEntity) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
        blockEntityFunction = pBlockEntity;
    }

    @Override
    protected MapCodec<? extends AbstractProcessingBlock> codec() {
        return CODEC;
    }

    /**
     * Uses the BlockPlaceContext parameter to determine the opposite facing direction.
     * This means that when you place the block, it's forward face will be looking at you
     * as expected.
     *
     * @param pContext {@link BlockPlaceContext}
     * @return {@link BlockState}
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    /**
     * Handles rotation for this block using the {@link BlockStateProperties#HORIZONTAL_FACING FACING} property.
     *
     * @see BaseEntityBlock#rotate(BlockState, LevelAccessor, BlockPos, Rotation)
     */
    @Override
    public BlockState rotate(BlockState pState, LevelAccessor pLevelAccessor, BlockPos pBlockPos, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    /**
     * Add {@link BlockStateProperties#HORIZONTAL_FACING FACING} to this block's default block state definition.
     *
     * @see BaseEntityBlock#createBlockStateDefinition(StateDefinition.Builder)
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    /**
     * This method is called whenever a block is broken in the world by a player or anything else. The first priority
     * is to make sure that if this block's block entity is an instance of {@link InventoryBlockEntity} that the item contents
     * of its container are dropped into the world and not deleted.
     */

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof InventoryBlockEntity inventoryBlockEntity) {
                inventoryBlockEntity.dropContents(pLevel, pPos);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    /**
     * This method is called by the game when the block is placed in the world to create its
     * BlockEntity. Apply the {@link BlockPos} and {@link BlockState} parameters to {@link AbstractProcessingBlock#blockEntityFunction blockEntityFunction}.
     *
     * @return a new BlockEntity by applying BlockPos and BlockState to blockEntityFunction.
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return blockEntityFunction.apply(pPos, pState);
    }
}
