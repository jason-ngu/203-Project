import processing.core.PImage;

import java.util.List;

public class Ore extends ActionEntity
{
    private final String BLOB_KEY = "blob";
    private final String BLOB_ID_SUFFIX = " -- blob";
    private final int BLOB_PERIOD_SCALE = 4;
    private final int BLOB_ANIMATION_MIN = 50;
    private final int BLOB_ANIMATION_MAX = 150;

    public Ore(String id, Point position, List<PImage> images, int actionPeriod)
    {
        super(id, position, images, actionPeriod);
    }

    public void scheduleActions(WorldEntity entity, EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(entity,
                new ActivityAction(entity, world, imageStore, 0),
                ((ActionEntity)entity).getActionPeriod());
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Point pos = getPosition();  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        ActionEntity blob = new OreBlob(getId() + BLOB_ID_SUFFIX,
                pos, imageStore.getImageList(BLOB_KEY),  getActionPeriod() / BLOB_PERIOD_SCALE,
                BLOB_ANIMATION_MIN +
                        getRandom().nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN));

        world.addEntity(blob);
        blob.scheduleActions(blob, scheduler, world, imageStore);
    }
}
