package al132.alib.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ABaseItem extends Item {

    public ABaseItem(CreativeModeTab tab, Properties properties) {
        super(properties.tab(tab));
    }
}