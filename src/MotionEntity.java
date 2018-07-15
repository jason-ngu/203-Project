import processing.core.PImage;

import java.util.List;

public abstract class MotionEntity extends AnimationEntity
{
    public MotionEntity(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public abstract boolean moveTo(WorldEntity entity, WorldModel world, WorldEntity target, EventScheduler scheduler);
    public abstract Point nextPosition(WorldEntity entity, WorldModel world, Point destPos);

    public boolean adjacent(Point p1, Point p2)
    {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
    }
}
