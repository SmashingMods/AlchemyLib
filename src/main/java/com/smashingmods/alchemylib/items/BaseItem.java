package com.smashingmods.alchemylib.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class BaseItem extends Item {

    public BaseItem(CreativeModeTab tab, Properties properties) {
        super(properties.tab(tab));
    }
}