package com.smashingmods.alchemylib.api.block;

import com.mojang.serialization.MapCodec;
import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * BaseEntityBlock-derived helper that wires an {@link AbstractProcessingBlock} to its
 * {@link BlockEntity} factory. Adds horizontal facing, drop-on-break, and standard model render shape.
 */
@SuppressWarnings("unused")
public class AbstractProcessingBlock extends BaseEntityBlock {

    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFunction;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public AbstractProcessingBlock(BiFunction<BlockPos, BlockState, BlockEntity> pBlockEntity) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
        blockEntityFunction = pBlockEntity;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        throw new UnsupportedOperationException("AbstractProcessingBlock does not provide a serialization codec");
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, LevelAccessor pLevelAccessor, BlockPos pBlockPos, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof InventoryBlockEntity inventoryBlockEntity) {
                inventoryBlockEntity.dropContents(pLevel, pPos);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return blockEntityFunction.apply(pPos, pState);
    }
}
