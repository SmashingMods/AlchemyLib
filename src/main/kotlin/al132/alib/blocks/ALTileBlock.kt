package al132.alib.blocks


import al132.alib.tiles.ALTile
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry

open class ALTileBlock(name: String,
                       creativeTab: CreativeTabs,
                       var tileClass: Class<out TileEntity>,
                       var modInstance: Any,
                       var guiID: Int,
                       material: Material = Material.ROCK,
                       hardness: Float = 3.0f)
    : ALBlock(name, creativeTab, material, hardness), ITileEntityProvider {


    init {
        GameRegistry.registerTileEntity(tileClass, name)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity = tileClass.newInstance()

    override fun onBlockActivated(world: World?, pos: BlockPos?, state: IBlockState, player: EntityPlayer?,
                                  hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!world!!.isRemote) {
            val tile = world.getTileEntity(pos!!)
            var interactionSuccessful = true
            if (tile is ALTile) interactionSuccessful = tile.onBlockActivated(world, pos, state, player!!, hand, facing, hitX, hitY, hitZ)
            if (!interactionSuccessful) player!!.openGui(modInstance, guiID, world, pos.x, pos.y, pos.z)
        }
        return true
    }

    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer,
                                 willHarvest: Boolean): Boolean {
        if (willHarvest) return true
        else return super.removedByPlayer(state, world, pos, player, willHarvest)
    }

    override fun harvestBlock(world: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack) {
        super.harvestBlock(world, player, pos, state, te, stack)
        world.setBlockToAir(pos)
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        val item = Item.getItemFromBlock(this)
        if (item != Items.AIR) {
            drops.add(ItemStack(item, 1, this.damageDropped(state))
                    .apply {
                        this.tagCompound = world.getTileEntity(pos)?.updateTag
                        tagCompound?.removeTag("x")
                        tagCompound?.removeTag("y")
                        tagCompound?.removeTag("z")
                    })
        }
    }

    override fun onBlockPlacedBy(world: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        val tile: TileEntity? = world?.getTileEntity(pos)
        if (tile != null && tile is ALTile) {
            stack?.tagCompound?.setInteger("x",pos!!.x)
            stack?.tagCompound?.setInteger("y",pos!!.y)
            stack?.tagCompound?.setInteger("z",pos!!.z)
            stack?.tagCompound?.let { tile.readFromNBT(it) }

            tile.markDirtyClient()
        }
    }
}