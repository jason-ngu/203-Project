import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Vein extends ActionEntity
{
    private final String ORE_KEY = "ore";
    private final String ORE_ID_PREFIX = "ore -- ";
    private final int ORE_CORRUPT_MIN = 20000;
    private final int ORE_CORRUPT_MAX = 30000;

    public Vein(String id, Point position, List<PImage> images, int actionPeriod)
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
        Optional<Point> openPt = world.findOpenAround(getPosition());

        if (openPt.isPresent())
        {
            ActionEntity ore = new Ore(ORE_ID_PREFIX + getId(),
                    openPt.get(), imageStore.getImageList(ORE_KEY),
                     ORE_CORRUPT_MIN +
                            getRandom().nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN));
            world.addEntity(ore);
            ore.scheduleActions(ore, scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                new ActivityAction(this, world, imageStore, 0),
                getActionPeriod());
    }
}
