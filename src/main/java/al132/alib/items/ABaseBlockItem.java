package al132.alib.items;

import al132.alib.ModData;
import al132.alib.blocks.ABaseBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class ABaseBlockItem extends BlockItem {
    public ABaseBlockItem(ModData data, ABaseBlock block) {
        super(block, new Item.Properties().group(data.itemGroup));
        setRegistryName(block.getRegistryName());
        //new BlockItem(ModBlocks.fermenter,new Item.Properties().group(itemGroup)).setRegistryName(ModBlocks.fermenter.getRegistryName());
    }
}
