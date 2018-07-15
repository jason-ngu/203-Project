import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class OreBlob extends MotionEntity
{
    private final String QUAKE_ID = "quake";
    private final int QUAKE_ACTION_PERIOD = 1100;
    private final int QUAKE_ANIMATION_PERIOD = 100;
    private final String QUAKE_KEY = "quake";

    public OreBlob(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public void scheduleActions(WorldEntity entity, EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(entity,
                new ActivityAction(entity, world, imageStore, 0),
                ((ActionEntity)entity).getActionPeriod());
        scheduler.scheduleEvent(entity,
                new AnimationAction(entity, null, null, 0), ((AnimationEntity)entity).getAnimationPeriod());
    }

    public Point nextPosition(WorldEntity entity, WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - entity.getPosition().x);
        Point newPos = new Point(entity.getPosition().x + horiz,
                entity.getPosition().y);

        Optional<WorldEntity> occupant = world.getOccupant(newPos);

        if (horiz == 0 ||
                (occupant.isPresent() && !(occupant.get().getClass().equals("ore"))))
        {
            int vert = Integer.signum(destPos.y - entity.getPosition().y);
            newPos = new Point(entity.getPosition().x, entity.getPosition().y + vert);
            occupant = world.getOccupant(newPos);

            if (vert == 0 ||
                    (occupant.isPresent() && !(occupant.get().getClass().equals("ore"))))
            {
                newPos = entity.getPosition();
            }
        }

        return newPos;
    }

    public boolean moveTo(WorldEntity blob, WorldModel world, WorldEntity target, EventScheduler scheduler)
    {
        if (adjacent(getPosition(), target.getPosition()))
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else
        {
            Point nextPos = nextPosition(blob, world, target.getPosition());

            if (!blob.getPosition().equals(nextPos))
            {
                Optional<WorldEntity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(blob, nextPos);
            }
            return false;
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<WorldEntity> blobTarget = world.findNearest(getPosition(), Vein.class);
        long nextPeriod = getActionPeriod();

        if (blobTarget.isPresent())
        {
            Point tgtPos = blobTarget.get().getPosition();

            if (moveTo(this, world, blobTarget.get(), scheduler))
            {
                ActionEntity quake = new Quake(QUAKE_ID, tgtPos,
                        imageStore.getImageList(QUAKE_KEY), QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);

                world.addEntity(quake);
                nextPeriod += getActionPeriod();
                quake.scheduleActions(quake, scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                new ActivityAction(this, world, imageStore, 0),
                nextPeriod);
    }
}
