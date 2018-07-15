import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerFull extends Miner
{
    public MinerFull(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
    }

    public void scheduleActions(WorldEntity entity, EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(entity,
                new ActivityAction(entity, world, imageStore, 0),
                ((ActionEntity)entity).getActionPeriod());
        scheduler.scheduleEvent(entity, new AnimationAction(entity, null, null, 0),
                ((AnimationEntity)entity).getAnimationPeriod());
    }

    public Point nextPosition(WorldEntity entity, WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - entity.getPosition().x);
        Point newPos = new Point(entity.getPosition().x + horiz,
                entity.getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos))
        {
            int vert = Integer.signum(destPos.y - entity.getPosition().y);
            newPos = new Point(entity.getPosition().x,
                    entity.getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos))
            {
                newPos = entity.getPosition();
            }
        }

        return newPos;
    }

    public boolean moveTo(WorldEntity miner, WorldModel world, WorldEntity target, EventScheduler scheduler)
    {
        if (adjacent(getPosition(), target.getPosition()))
        {
            return true;
        }
        else
        {
            Point nextPos = nextPosition(miner, world, target.getPosition());

            if (!miner.getPosition().equals(nextPos))
            {
                Optional<WorldEntity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(miner, nextPos);
            }
            return false;
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        ActionEntity miner = new MinerNotFull(getId(), getPosition(), getImages(), getResourceLimit(), getResourceLimit(), getActionPeriod(), getAnimationPeriod());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        scheduleActions(miner, scheduler, world, imageStore);
        return true;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<WorldEntity> fullTarget = world.findNearest(getPosition(),
                Blacksmith.class);

        if (fullTarget.isPresent() &&
                moveTo(this, world, fullTarget.get(), scheduler))
        {
            transform(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this,
                    new ActivityAction(this, world, imageStore, 0),
                    getActionPeriod());
        }
    }
}
