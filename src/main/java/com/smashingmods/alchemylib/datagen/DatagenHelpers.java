package com.smashingmods.alchemylib.datagen;

import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
public class DatagenHelpers {

    public static ItemStack toItemStack(String pString) {
        return toItemStack(pString, 1);
    }

    public static ItemStack toItemStack(String pString, int pCount) {
        return new ItemStack(getChemicalItem(pString), pCount);
    }

    public static IngredientStack toIngredientStack(String pString) {
        return toIngredientStack(pString, 1);
    }

    public static IngredientStack toIngredientStack(String pString, int pCount) {
        return new IngredientStack(getChemicalItem(pString), pCount);
    }

    private static ItemLike getChemicalItem(String pString) {
        ResourceLocation resourceLocation = ResourceLocation.parse(pString);

        Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(pString);
        Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(pString.replace(" ", "_"));

        Item outputItem = BuiltInRegistries.ITEM.getOptional(resourceLocation).orElse(null);
        Block outputBlock = BuiltInRegistries.BLOCK.getOptional(resourceLocation).orElse(null);

        if (optionalElement.isPresent()) {
            return optionalElement.get();
        } else if (optionalCompound.isPresent()) {
            return optionalCompound.get();
        } else if (outputItem != null && outputItem != Items.AIR) {
            return outputItem;
        } else if (outputBlock != null && outputBlock != Blocks.AIR && outputBlock != Blocks.WATER) {
            return outputBlock;
        }
        return Items.AIR;
    }

    public static ResourceLocation getLocation(ItemStack pItemStack, String pType, String pModId) {
        return ResourceLocation.fromNamespaceAndPath(pModId,
                String.format("%s/%s", pType, Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(pItemStack.getItem())).getPath()));
    }

    public static ResourceLocation getLocation(Item pItem, String pType, String pModId) {
        return ResourceLocation.fromNamespaceAndPath(pModId,
                String.format("%s/%s", pType, Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(pItem)).getPath()));
    }

    public static ResourceLocation getLocation(FluidStack pFluidStack, String pType, String pModId) {
        return ResourceLocation.fromNamespaceAndPath(pModId,
                String.format("%s/%s", pType, Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(pFluidStack.getFluid())).getPath()));
    }

    public static ModLoadedCondition modLoadedCondition(String pModId) {
        return new ModLoadedCondition(pModId);
    }

    public static NotCondition notCondition(ICondition pCondition) {
        return new NotCondition(pCondition);
    }
}
