package al132.alib.utils.extensions;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by al132 on 4/23/2017.
 */


public class ListUtils {
    public static <E> ImmutableList<E> toImmutable(List<E> list) {
        return new ImmutableList.Builder().addAll(list).build();
    }

    public static boolean containsStack(List<ItemStack> list, ItemStack stack) {
        return list.stream().anyMatch(x -> ItemStack.areItemStacksEqual(x, stack));
    }

//fun List<ItemStack>.containsItem(stack: ItemStack): Boolean = any { ItemStack.areItemsEqual(it, stack) }

    public static boolean containsItem(List<ItemStack> list, ItemStack stack, boolean strict) { //strict = false default
/*        any {
            OreDictionary.itemMatches(it, stack, strict)
        }

 */
        return false;
    }


    public static List<ItemStack> copy(List<ItemStack> list) {
        List<ItemStack> temp = new ArrayList<>();
        list.forEach(x -> temp.add(x.copy()));
        return temp;
    }
}