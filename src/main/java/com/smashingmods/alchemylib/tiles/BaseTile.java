package com.smashingmods.alchemylib.tiles;

import com.smashingmods.alchemylib.utils.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseTile extends BlockEntity implements Nameable {

    private int notifyTicks = 0;

    public IEnergyStorage energy;// = new EnergyStorage(0);
    public LazyOptional<IEnergyStorage> energyHolder;// = LazyOptional.of(() -> energy);


    public BaseTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        if (this instanceof EnergyTile) {
            energy = ((EnergyTile) this).initEnergy();
            energyHolder = LazyOptional.of(() -> energy);
        }
    }


    public boolean onBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        AtomicBoolean didInteract = new AtomicBoolean(false);
        if (this instanceof FluidTile) {
            ItemStack heldItem = player.getItemInHand(hand);
            heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(x -> {
                didInteract.set(FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
                this.setChanged();
            });
        }
        return didInteract.get();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (this instanceof EnergyTile) {
            int max = ((EnergyTile) this).getEnergy().getMaxEnergyStored();
            this.energy = ((EnergyTile) this).initEnergy();
            if (energy instanceof CustomEnergyStorage) {
                ((CustomEnergyStorage) energy).receiveEnergyInternal(compound.getInt("energy"), false);
            } else {
                energy.receiveEnergy(compound.getInt("energy"), false);
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this instanceof EnergyTile) {
            compound.putInt("energy", energy.getEnergyStored());
        }
        //return super.save(compound);
    }

    /*
    public void markDirtyGUI() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 6);
        }
    }

    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 3);
        }
    }
*/

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        saveAdditional(updateTag);
        return updateTag;
    }
    /*
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundTag nbtTag = new CompoundTag();
        saveAdditional(nbtTag);
        return new SUpdateTileEntityPacket(getBlockPos(), 3, nbtTag);
    }
    */

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        /*CompoundTag tag = pkt.getTag();
        if (tag == null) {
            return;
        }*/
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    /*
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.load(null, packet.getTag());
    }
     */

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY && this instanceof EnergyTile) {
            return this.energyHolder.cast();
        } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this instanceof InventoryTile) {
            return ((InventoryTile) this).getExternalInventory().cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this instanceof FluidTile) {
            return ((FluidTile) this).getFluidHandler().cast();
        } else return super.getCapability(cap, side);
    }

    @Override
    public TextComponent getDisplayName() {
        //TODO make more localization friendly
        String regName = getType().getRegistryName().getPath().replaceAll("_", " ");
        return new TextComponent(Utils.capitalizeAllWords(regName));
    }

    /**
     * Sets a block state into this world.Flags are as follows:
     * 1 will cause a block update.
     * 2 will send the change to clients.
     * 4 will prevent the block from being re-rendered.
     * 8 will force any re-renders to run on the main thread instead
     * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
     * 32 will prevent neighbor reactions from spawning drops.
     * 64 will signify the block is being moved.
     * Flags can be OR-ed
     */
    public void updateGUIEvery(int ticks) {
        this.notifyTicks++;
        if (this.notifyTicks >= ticks) {
            if (this.level != null) {
                BlockState state = this.getBlockState();
                this.level.sendBlockUpdated(getBlockPos(), state, state, 22);
            }
            this.notifyTicks = 0;
        }
    }

    public void updateGUI() {
        if (this.level != null) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(getBlockPos(), state, state, 22);
        }
    }
}