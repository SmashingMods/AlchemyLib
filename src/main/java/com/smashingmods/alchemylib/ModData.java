package com.smashingmods.alchemylib;


import com.smashingmods.alchemylib.blocks.BaseBlock;
import com.smashingmods.alchemylib.items.BaseItem;
import com.smashingmods.alchemylib.tiles.BaseTile;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class ModData {

    public final String MODID;
    public final List<BaseBlock> BLOCKS = new ArrayList<>();
    public final List<BaseItem> ITEMS = new ArrayList<>();
    public final List<BlockEntityType<?>> TILES = new ArrayList<>();
    public final List<MenuType> CONTAINERS = new ArrayList<>();

    public final CreativeModeTab itemGroup;
    public ModData(String modid, ItemStack itemIcon) {
        this.MODID = modid;
        itemGroup = new CreativeModeTab(MODID){

            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Blocks.DIRT);
            }
        };
    }

    public abstract void registerTiles(RegistryEvent.Register<BlockEntityType<?>> e);

    public abstract void registerContainers(RegistryEvent.Register<MenuType<?>> e);

    public <T extends BaseTile> BlockEntityType<T> registerTile(BlockEntityType.BlockEntitySupplier<T> factory, Block block, String name) {
        BlockEntityType<T> type = BlockEntityType.Builder.of(factory, block).build(null);
        type.setRegistryName(MODID, name);
        TILES.add(type);
        return type;
    }

    public <T extends Container> MenuType registerContainer(MenuType type, String id) {
        type.setRegistryName(MODID, id);
        CONTAINERS.add(type);
        return type;
    }
}