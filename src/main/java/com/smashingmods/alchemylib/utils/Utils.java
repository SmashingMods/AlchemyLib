package com.smashingmods.alchemylib.utils;

import net.minecraftforge.items.IItemHandler;

import java.util.stream.IntStream;

/**
 * Created by al132 on 1/2/2017.
 */
public class Utils {
/*
    public static boolean oreExists(String dictName ){
       return OreDicOretionary.doesOreNameExist(dictName);
    }*/

    // public static void firstOre(dictEntry: String): ItemStack = OreDictionary.getOres(dictEntry).firstOrNull()?: ItemStack.EMPTY

    //public static void toDictName(prefix: String, element: String) = prefix + element.first().toUpperCase() + element.substring(1)


    public static boolean isHandlerEmpty(IItemHandler handler) {
        return IntStream.range(0, handler.getSlots()).allMatch(x -> handler.getStackInSlot(x).isEmpty());
    }
/*
    public static void canStacksMerge(ItemStack origin, ItemStack target: stacksCanbeEmpty:Boolean=true):Boolean

    {
        if (stacksCanbeEmpty && (target.isEmpty || origin.isEmpty)) return true
        else {
            return origin.item == = target.item
                    && origin.count + target.count <= origin.maxStackSize
                    && origin.itemDamage == target.itemDamage
                    && origin.tagCompound == = target.tagCompound
        }
    }

    public static void areItemsEqualIgnoreMeta(stack1:ItemStack, stack2:ItemStack):Boolean

    {
        if (stack1.isEmpty && stack2.isEmpty) return true
        else if (!stack1.isEmpty && !stack2.isEmpty) return stack1.item == = stack2.item
        else return false
    }

    public static void getOres(name:String, quantity:Int):ImmutableList<ItemStack>

    {
        val temp = ImmutableList.Builder < ItemStack > ()
        OreDictionary.getOres(name).forEach {
        x ->
                val y = x.copy()
        y.count = quantity
        temp.add(y)
    }
        return temp.build()
    }


    public static void damage(inStack:ItemStack):ItemStack

    {
        val temp = inStack
        temp.itemDamage = inStack.itemDamage + 1

        if (temp.itemDamage >= temp.maxDamage) return ItemStack.EMPTY
        else return temp
    }*/

    public static String capitalizeAllWords(String str) { //StringUtils.capitaliseAllWords
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder buffer = new StringBuilder(sz);
        boolean space = true;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
                buffer.append(ch);
                space = true;
            } else if (space) {
                buffer.append(Character.toTitleCase(ch));
                space = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }
}