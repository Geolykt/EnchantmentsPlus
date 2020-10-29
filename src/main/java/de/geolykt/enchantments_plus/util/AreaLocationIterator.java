package de.geolykt.enchantments_plus.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * The AreaLocationIterator is a utility Class to Iterate over Cube-shaped areas of Locations.
 * @since 2.2.0
 */
public class AreaLocationIterator implements Iterator<Location> {

    private final int width, height, length, baseX, baseY, baseZ;

    private final Location base;

    private int x, y, z = 0;

    /**
     * Constructor for the Iterator. The internal x, y and z pointers are set to 0.
     * The total amount of times the next()-method can be invoked is widthX*heightY*lengthZ. After that the reset() method needs to be called.
     * @param anchor The basepoint for the iterator which is the Location from which the resulting Locations are originating from
     * @param widthX The Size of the Iterator on the X-Axis.
     * @param heightY The Size of the Iterator on the Y-Axis.
     * @param lengthZ The Size of the Iterator on the Z-Axis.
     */
    public AreaLocationIterator(@NotNull Location anchor, int widthX, int heightY, int lengthZ) {
        this(anchor, widthX, heightY, lengthZ, 0, 0, 0);
    }

    /**
     * Constructor for the Iterator. The internal x, y and z pointers are set to provided default values,.
     * The total amount of times the next()-method can be invoked is widthX*heightY*lengthZ. After that the reset() method needs to be called.
     * @param anchor The basepoint for the iterator which is the Location from which the resulting Locations are originating from
     * @param widthX The Size of the Iterator on the X-Axis.
     * @param heightY The Size of the Iterator on the Y-Axis.
     * @param lengthZ The Size of the Iterator on the Z-Axis.
     */
    public AreaLocationIterator(@NotNull Location anchor, int widthX, int heightY, int lengthZ, int minX, int minY, int minZ) {
        width = widthX+minX;
        height = heightY+minY;
        length = lengthZ+minZ;
        base = anchor.clone();
        baseX = minX;
        baseY = minY;
        baseZ = minZ;
        reset();
    }

    @Override
    public boolean hasNext() {
        return length < z; // That's all, pretty easy
    }

    @Override
    public Location next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Location loc = base.clone().add(x, y, z);
        if (++x >= width) {
            x = 0;
            if (++z >= length) {
                z = 0;
                if (++y >= height) {
                    y = 0;
                }
            }
        }
        return loc;
    }

    /**
     * Returns the anchor of the Iterator, which is the Location from which the rest is abstracted from.
     * @return The anchor
     * @since 2.2.0
     */
    public Location getAnchor() {
        return base;
    }

    /**
     * Returns the width of the Iterator (which is on the X-Axis)
     * @return the width
     * @since 2.2.0
     */
    public int getWidth() {
        return width - baseX;
    }

    /**
     * Returns the height of the Iterator (which is on the Y-Axis)
     * @return the height
     * @since 2.2.0
     */
    public int getHeight() {
        return height - baseY;
    }

    /**
     * Returns the length of the Iterator (which is on the Z-Axis)
     * @return the length
     * @since 2.2.0
     */
    public int getLength() {
        return length - baseZ;
    }

    /**
     * Resets the internal location pointers to 0.
     * @since 2.2.0
     */
    public void reset() {
        x = baseX;
        y = baseY;
        z = baseZ;
    }
}
