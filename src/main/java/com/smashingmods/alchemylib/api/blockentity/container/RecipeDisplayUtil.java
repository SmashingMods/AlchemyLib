package com.smashingmods.alchemylib.api.blockentity.container;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeDisplayUtil {

    public static List<Component> getItemTooltipComponent(ItemStack pItemStack, BaseComponent pComponent) {
        List<Component> components = new ArrayList<>();
        String namespace = StringUtils.capitalize(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pItemStack.getItem())).getNamespace());

        components.add(pComponent.withStyle(ChatFormatting.UNDERLINE, ChatFormatting.YELLOW));
        components.add(new TextComponent(String.format("%dx %s", pItemStack.getCount(), pItemStack.getItem().getDescription().getString())));

        if (pItemStack.getItem() instanceof Chemical chemical) {

            String abbreviation = chemical.getAbbreviation();

            if (chemical instanceof ElementItem element) {
                components.add(new TextComponent(String.format("%s (%d)", abbreviation, element.getAtomicNumber())).withStyle(ChatFormatting.DARK_AQUA));
                components.add(new TextComponent(element.getGroupName()).withStyle(ChatFormatting.GRAY));
            } else if (chemical instanceof ChemicalItem chemicalItem && !chemicalItem.getItemType().equals(ChemicalItemType.COMPOUND)) {
                ElementItem element = (ElementItem) chemicalItem.getChemical();
                components.add(new TextComponent(String.format("%s (%d)", chemicalItem.getAbbreviation(), element.getAtomicNumber())).withStyle(ChatFormatting.DARK_AQUA));
                components.add(new TextComponent(element.getGroupName()).withStyle(ChatFormatting.GRAY));
            } else if (chemical instanceof CompoundItem) {
                components.add(new TextComponent(abbreviation).withStyle(ChatFormatting.DARK_AQUA));
            }
        }
        components.add(new TextComponent(namespace).withStyle(ChatFormatting.BLUE));
        return components;
    }
}
