import processing.core.PImage;

import java.util.List;

public abstract class WorldEntity
{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public WorldEntity(String id, Point position, List<PImage> images)
    {
        this.id = id;
        this.position = position;
        this.images = images;
    }

    public String getId()
    {
        return id;
    }

    public Point getPosition()
    {
        return position;
    }

    public void setPosition(Point p)
    {
        position = p;
    }

    public List<PImage> getImages()
    {
        return images;
    }

    public int getImageIndex()
    {
        return imageIndex;
    }

    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public PImage getCurrentImage(Object entity)
    {
        if (entity instanceof WorldEntity)
        {
            return (images.get(((WorldEntity)entity).getImageIndex()));
        }
        else
        {
            throw new UnsupportedOperationException(
                    String.format("getCurrentImage not supported for %s",
                            entity));
        }
    }
}
