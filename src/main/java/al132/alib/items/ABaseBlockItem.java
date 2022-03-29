package al132.alib.items;

import al132.alib.blocks.ABaseBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;


public class ABaseBlockItem extends BlockItem {
    public ABaseBlockItem(CreativeModeTab tab, ABaseBlock block) {
        super(block, new Item.Properties().tab(tab));
    }
}
