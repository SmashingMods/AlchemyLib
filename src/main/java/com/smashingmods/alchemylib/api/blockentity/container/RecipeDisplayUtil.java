package com.smashingmods.alchemylib.api.blockentity.container;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeDisplayUtil {

    public static List<Component> getItemTooltipComponent(ItemStack pItemStack, MutableComponent pComponent) {
        List<Component> components = new ArrayList<>();
        String namespace = StringUtils.capitalize(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(pItemStack.getItem())).getNamespace());

        components.add(pComponent.withStyle(ChatFormatting.UNDERLINE, ChatFormatting.YELLOW));
        components.add(MutableComponent.create(PlainTextContents.create(String.format("%dx %s", pItemStack.getCount(), pItemStack.getItem().getName().getString()))));

        if (pItemStack.getItem() instanceof Chemical chemical) {

            String abbreviation = chemical.getAbbreviation();

            if (chemical instanceof ElementItem element) {
                components.add(MutableComponent.create(PlainTextContents.create(String.format("%s (%d)", abbreviation, element.getAtomicNumber()))).withStyle(ChatFormatting.DARK_AQUA));
                components.add(MutableComponent.create(PlainTextContents.create(element.getGroupName())).withStyle(ChatFormatting.GRAY));
            } else if (chemical instanceof ChemicalItem chemicalItem && !chemicalItem.getItemType().equals(ChemicalItemType.COMPOUND)) {
                ElementItem element = (ElementItem) chemicalItem.getChemical();
                components.add(MutableComponent.create(PlainTextContents.create(String.format("%s (%d)", chemicalItem.getAbbreviation(), element.getAtomicNumber()))).withStyle(ChatFormatting.DARK_AQUA));
                components.add(MutableComponent.create(PlainTextContents.create(element.getGroupName())).withStyle(ChatFormatting.GRAY));
            } else if (chemical instanceof CompoundItem) {
                components.add(MutableComponent.create(PlainTextContents.create(abbreviation)).withStyle(ChatFormatting.DARK_AQUA));
            }
        }
        components.add(MutableComponent.create(PlainTextContents.create(namespace)).withStyle(ChatFormatting.BLUE));
        return components;
    }
}
