import processing.core.PImage;

import java.util.List;

public abstract class Miner extends MotionEntity
{
    private int resourceLimit;
    private int resourceCount;

    public Miner(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
    }

    public int getResourceLimit() { return resourceLimit; }

    public void setResourceCount(int num)
    {
        resourceCount = num;
    }

    public int getResourceCount()
    {
        return resourceCount;
    }

    public abstract boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
