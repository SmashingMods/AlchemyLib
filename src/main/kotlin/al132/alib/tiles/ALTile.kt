package al132.alib.tiles

import al132.alib.Reference.ENERGY_CAP
import al132.alib.Reference.FLUID_CAP
import al132.alib.Reference.ITEM_CAP
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.CombinedInvWrapper


abstract class ALTile : TileEntity() {

    var inputSlots: Int = 0
    var outputSlots: Int = 0
    val SIZE: Int
        get() = inventory.slots

    var dirtyTicks: Int = 0

    lateinit var input: ALTileStackHandler
    protected lateinit var automationInput: IItemHandlerModifiable
    lateinit var output: ALTileStackHandler
    protected lateinit var automationOutput: IItemHandlerModifiable

    open val inventory: IItemHandler
        get() = CombinedInvWrapper(input, output)


    open val automationInvHandler: CombinedInvWrapper
        get() = CombinedInvWrapper(this.automationInput, this.automationOutput)


    fun canInteractWith(playerIn: EntityPlayer) = !isInvalid
            && playerIn.getDistanceSq(pos.add(0.5, 0.5, 0.5)) <= 64.0

    fun initInventoryCapability(inputSlots: Int, outputSlots: Int) {
        this.inputSlots = inputSlots
        this.outputSlots = outputSlots

        this.initInventoryInputCapability()
        automationInput = object : ALWrappedItemHandler(input) {
            override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = ItemStack.EMPTY
        }

        output = object : ALTileStackHandler(outputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = stack
        }

        automationOutput = object : ALWrappedItemHandler(output) {
            override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
                if (!getStackInSlot(slot).isEmpty) return super.extractItem(slot, amount, simulate)
                else return ItemStack.EMPTY
            }
        }
    }

    open fun initInventoryInputCapability() {
        input = ALTileStackHandler(inputSlots, this)
    }

    /*
    public void initFluidCapability(boolean input, boolean output, int capacity) {
        if (input) {
            inputTank = new FluidTank(capacity);
            inputTank.setTileEntity(this);
            inputTank.setCanFill(true);
            inputTank.setCanDrain(false);
        }
        if (output) {
            outputTank = new FluidTank(Fluid.BUCKET_VOLUME * 10);
            outputTank.setTileEntity(this);
            outputTank.setCanFill(false);
            outputTank.setCanDrain(true);
        }

        this.hasFluidCapability = true;
    }*/

    override fun getUpdateTag(): NBTTagCompound = writeToNBT(NBTTagCompound())

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        val nbtTag = NBTTagCompound()
        this.writeToNBT(nbtTag)
        return SPacketUpdateTileEntity(pos, 0, nbtTag)
    }

    override fun onDataPacket(net: NetworkManager?, packet: SPacketUpdateTileEntity?) {
        this.readFromNBT(packet!!.nbtCompound)
    }

    //From McJtyLib, MIT license
    fun markDirtyClient() {
        markDirty()
        getWorld().let { world ->
            val state = world.getBlockState(getPos())
            world.notifyBlockUpdate(getPos(), state, state, 3)
        }
    }

    fun markDirtyClientEvery(ticks: Int) {
        this.dirtyTicks++
        if (this.dirtyTicks >= ticks) {
            this.markDirtyClient()
            this.dirtyTicks = 0
        }
    }

    fun markDirtyEvery(ticks: Int) {
        this.dirtyTicks++
        if (this.dirtyTicks >= ticks) {
            this.markDirty()
            this.dirtyTicks = 0
        }
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (this is IEnergyTile) {
            val energyStored = compound.getInteger("EnergyStored")
            energyStorage = EnergyStorage(this.energyCapacity())
            energyStorage.receiveEnergy(energyStored, false)
        }
        if (this is IItemTile) {
            input.deserializeNBT(compound.getCompoundTag("input"))
            output.deserializeNBT(compound.getCompoundTag("output"))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        if (this is IEnergyTile) {
            compound.setInteger("EnergyStored", energyStorage.energyStored)
        }
        if (this is IItemTile) {
            compound.setTag("input", input.serializeNBT())
            compound.setTag("output", output.serializeNBT())
        }
        return compound
    }

    open fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer,
                              hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (this is IFluidTile) {
            val heldItem = player.getHeldItem(hand)
            if (heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, facing)) {
                val didInteract = FluidUtil.interactWithFluidHandler(player, hand, world, pos, facing)
                this.markDirty()
                return didInteract
            }
        }
        return false
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        when (capability) {
            ENERGY_CAP -> return this is IEnergyTile
            FLUID_CAP  -> return this is IFluidTile
            ITEM_CAP   -> return this is IItemTile
        }
        return super.hasCapability(capability, facing)
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        when (capability) {
            ENERGY_CAP -> if (this is IEnergyTile) return ENERGY_CAP.cast<T>((this as IEnergyTile).energyStorage)
            FLUID_CAP  -> if (this is IFluidTile) return FLUID_CAP.cast<T>(fluidTanks)
            ITEM_CAP   -> if (this is IItemTile) return ITEM_CAP.cast<T>(automationInvHandler)
        }
        return super.getCapability(capability, facing)
    }
}