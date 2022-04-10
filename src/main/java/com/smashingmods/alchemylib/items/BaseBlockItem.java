package com.smashingmods.alchemylib.items;

import com.smashingmods.alchemylib.blocks.BaseBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;


public class BaseBlockItem extends BlockItem {
    public BaseBlockItem(CreativeModeTab tab, BaseBlock block) {
        super(block, new Item.Properties().tab(tab));
    }
}
