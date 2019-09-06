package al132.alib.blocks;


import al132.alib.ModData;
import net.minecraft.block.Block;

abstract public class ABaseBlock extends Block {

    public ABaseBlock(ModData data, String name, Properties properties) {
        super(properties);
        setRegistryName(data.MODID, name);
        data.BLOCKS.add(this);
    }
}