package al132.alib.client;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CapabilityDisplayWrapper {

    public int x, y, width, height;


    public CapabilityDisplayWrapper(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract int getStored();

    public abstract int getCapacity();

    public ITextComponent toTextComponent(){
        return ITextComponent.getTextComponentOrEmpty(toString());
    }
}