package al132.alib;

import al132.alib.blocks.ABaseBlock;
import al132.alib.items.ABaseItem;
import al132.alib.tiles.ABaseTile;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ModData {

    public final String MODID;
    public final List<ABaseBlock> BLOCKS = new ArrayList<>();
    public final List<ABaseItem> ITEMS = new ArrayList<>();
    public final List<TileEntityType<?>> TILES = new ArrayList<>();
    public final List<ContainerType> CONTAINERS = new ArrayList<>();

    public final ItemGroup itemGroup;

    public ModData(String modid, ItemStack itemIcon) {
        this.MODID = modid;
        itemGroup = new ItemGroup(MODID){

            @Override
            public ItemStack createIcon() {
                return new ItemStack(Blocks.DIRT);
            }
        };
    }

    public abstract void registerTiles(RegistryEvent.Register<TileEntityType<?>> e);

    public abstract void registerContainers(RegistryEvent.Register<ContainerType<?>> e);

    public <T extends ABaseTile> TileEntityType<T> registerTile(Supplier<T> factory, Block block, String name) {
        TileEntityType<T> type = TileEntityType.Builder.create(factory, block).build(null);
        type.setRegistryName(MODID, name);
        TILES.add(type);
        return type;
    }

    public <T extends Container> ContainerType<T> registerContainer(ContainerType<T> type, String id) {
        type.setRegistryName(MODID, id);
        CONTAINERS.add(type);
        return type;
    }
}