package al132.alib.items;

import al132.alib.ModData;
import net.minecraft.item.Item;

public class ABaseItem extends Item {

    public ABaseItem(ModData data, String name, Properties properties) {
        super(properties.group(data.itemGroup));
        setRegistryName(data.MODID, name);
        data.ITEMS.add(this);
    }

    public ABaseItem(ModData data, String name) {
        this(data, name, new Item.Properties().group(data.itemGroup));
    }
}