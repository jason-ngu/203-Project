import processing.core.PImage;

import java.util.List;
import java.util.Random;

public abstract class ActionEntity extends WorldEntity
{
    private final Random rand;
    private int actionPeriod;

    public ActionEntity(String id, Point position, List<PImage> images, int actionPeriod)
    {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
        rand = new Random();
    }

    public int getActionPeriod() {return actionPeriod;}

    public Random getRandom() {return rand;}

    public abstract void scheduleActions(WorldEntity entity, EventScheduler scheduler, WorldModel world, ImageStore imageStore);
    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
