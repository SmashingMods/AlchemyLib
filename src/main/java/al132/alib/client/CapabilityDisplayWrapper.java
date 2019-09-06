package al132.alib.client;

import java.util.List;

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

    public abstract List<String> toStringList();
}