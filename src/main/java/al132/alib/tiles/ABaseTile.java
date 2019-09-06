package al132.alib.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.codehaus.plexus.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ABaseTile extends TileEntity implements INamedContainerProvider {

    public IEnergyStorage energy = new EnergyStorage(0);
    public LazyOptional<IEnergyStorage> energyHolder = LazyOptional.of(() -> energy);


    public ABaseTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        if (this instanceof EnergyTile) {
            energy = new EnergyStorage(((EnergyTile) this).getTileMaxEnergy());
        }
    }


    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        AtomicBoolean didInteract = new AtomicBoolean(false);
        if (this instanceof FluidTile) {
            ItemStack heldItem = player.getHeldItem(hand);
            heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(x -> {
                didInteract.set(FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
                this.markDirty();
            });
        }
        return didInteract.get();
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        if (this instanceof EnergyTile) {
            int max = ((EnergyTile) this).getTileMaxEnergy();
            this.energy = new EnergyStorage(max, max, max, compound.getInt("energy"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (this instanceof EnergyTile) {
            compound.putInt("energy", energy.getEnergyStored());
        }
        return super.write(compound);
    }

    public void markDirtyGUI() {
        markDirty();
        if (world != null) {
            BlockState state = world.getBlockState(getPos());
            world.notifyBlockUpdate(pos, state, state, 6);
        }
    }

    public void markDirtyClient() {
        markDirty();
        if (world != null) {
            BlockState state = world.getBlockState(getPos());
            world.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }


    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT updateTag = super.getUpdateTag();
        write(updateTag);
        return updateTag;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        write(nbtTag);
        return new SUpdateTileEntityPacket(pos, 3, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.read(packet.getNbtCompound());
    }

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
    public ITextComponent getDisplayName() {
        //TODO make more localization friendly
        String regName = getType().getRegistryName().getPath().replaceAll("_", " ");
        return new StringTextComponent(StringUtils.capitaliseAllWords(regName));
    }
}