import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import processing.core.*;

public final class VirtualWorld extends PApplet
{
   private final String SMITH_KEY = "blacksmith";
   private final int SMITH_NUM_PROPERTIES = 4;
   private final int SMITH_ID = 1;
   private final int SMITH_COL = 2;
   private final int SMITH_ROW = 3;

   private final String VEIN_KEY = "vein";
   private final int VEIN_ACTION_PERIOD = 4;
   private final int VEIN_ROW = 3;
   private final int VEIN_COL = 2;
   private final int VEIN_ID = 1;
   private final int VEIN_NUM_PROPERTIES = 5;

   private final String OBSTACLE_KEY = "obstacle";
   private final int OBSTACLE_NUM_PROPERTIES = 4;
   private final int OBSTACLE_ID = 1;
   private final int OBSTACLE_COL = 2;
   private final int OBSTACLE_ROW = 3;

   private final String ORE_KEY = "ore";
   private final int ORE_NUM_PROPERTIES = 5;
   private final int ORE_ID = 1;
   private final int ORE_COL = 2;
   private final int ORE_ROW = 3;
   private final int ORE_ACTION_PERIOD = 4;

   private final String MINER_KEY = "miner";
   private final int MINER_NUM_PROPERTIES = 7;
   private final int MINER_ID = 1;
   private final int MINER_COL = 2;
   private final int MINER_ROW = 3;
   private final int MINER_LIMIT = 4;
   private final int MINER_ACTION_PERIOD = 5;
   private final int MINER_ANIMATION_PERIOD = 6;

   private final int TIMER_ACTION_PERIOD = 100;

   private final int VIEW_WIDTH = 640;
   private final int VIEW_HEIGHT = 480;
   private final int TILE_WIDTH = 32;
   private final int TILE_HEIGHT = 32;
   private final int WORLD_WIDTH_SCALE = 2;
   private final int WORLD_HEIGHT_SCALE = 2;

   private final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   private final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   private final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   private final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;
   private final String IMAGE_LIST_FILE_NAME = "imagelist";
   private final String DEFAULT_IMAGE_NAME = "background_default";
   private final int DEFAULT_IMAGE_COLOR = 0x808080;

   private final String LOAD_FILE_NAME = "gaia.sav";

   private static final String FAST_FLAG = "-fast";
   private static final String FASTER_FLAG = "-faster";
   private static final String FASTEST_FLAG = "-fastest";
   private static final double FAST_SCALE = 0.5;
   private static final double FASTER_SCALE = 0.25;
   private static final double FASTEST_SCALE = 0.10;

   private static double timeScale = 1.0;

   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;

   private final int PROPERTY_KEY = 0;

   private final String BGND_KEY = "background";
   private final int BGND_NUM_PROPERTIES = 4;
   private final int BGND_ID = 1;
   private final int BGND_COL = 2;
   private final int BGND_ROW = 3;

   private long next_time;

   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   public void setup()
   {
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);

      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void draw()
   {
      long time = System.currentTimeMillis();
      if (time >= next_time)
      {
         scheduler.updateOnTime(time);
         next_time = time + TIMER_ACTION_PERIOD;
      }

      view.drawViewport();
   }

   public void keyPressed()
   {
      if (key == CODED)
      {
         int dx = 0;
         int dy = 0;

         switch (keyCode)
         {
            case UP:
               dy = -1;
               break;
            case DOWN:
               dy = 1;
               break;
            case LEFT:
               dx = -1;
               break;
            case RIGHT:
               dx = 1;
               break;
         }
         view.shiftView(dx, dy);
      }
   }

   public Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   public PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         load(in);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public void scheduleActions(WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      for (WorldEntity entity : world.getEntities())
      {
         if(entity instanceof ActionEntity)
         {
             ((ActionEntity)entity).scheduleActions(entity, scheduler, world, imageStore);
         }
      }
   }

   public static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public void load(Scanner in)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine()))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }

   public boolean processLine(String line)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return parseBackground(properties);
            case MINER_KEY:
               return parseMiner(properties);
            case OBSTACLE_KEY:
               return parseObstacle(properties);
            case ORE_KEY:
               return parseOre(properties);
            case SMITH_KEY:
               return parseSmith(properties);
            case VEIN_KEY:
               return parseVein(properties);
         }
      }

      return false;
   }

   public boolean parseBackground(String [] properties)
   {
      if (properties.length == BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                 Integer.parseInt(properties[BGND_ROW]));
         String id = properties[BGND_ID];
         world.setBackground(pt, new Background(id, imageStore.getImageList(id)));
      }

      return properties.length == BGND_NUM_PROPERTIES;
   }

   public boolean parseMiner(String[] properties)
   {
      if (properties.length == MINER_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[MINER_COL]),
                 Integer.parseInt(properties[MINER_ROW]));
         WorldEntity entity = new MinerNotFull(properties[MINER_ID],
                 pt, imageStore.getImageList(MINER_KEY),
                 Integer.parseInt(properties[MINER_LIMIT]), 0,
                 Integer.parseInt(properties[MINER_ACTION_PERIOD]),
                 Integer.parseInt(properties[MINER_ANIMATION_PERIOD]));
         world.tryAddEntity(entity);
      }

      return properties.length == MINER_NUM_PROPERTIES;
   }

   public boolean parseObstacle(String [] properties)
   {
      if (properties.length == OBSTACLE_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[OBSTACLE_COL]),
                 Integer.parseInt(properties[OBSTACLE_ROW]));
         WorldEntity entity = new Obstacle(properties[OBSTACLE_ID],
                 pt, imageStore.getImageList(OBSTACLE_KEY));
         world.tryAddEntity(entity);
      }

      return properties.length == OBSTACLE_NUM_PROPERTIES;
   }

   public boolean parseOre(String[] properties)
   {
      if (properties.length == ORE_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[ORE_COL]),
                 Integer.parseInt(properties[ORE_ROW]));
         WorldEntity entity = new Ore(properties[ORE_ID],
                 pt, imageStore.getImageList(ORE_KEY), Integer.parseInt(properties[ORE_ACTION_PERIOD]));
         world.tryAddEntity(entity);
      }

      return properties.length == ORE_NUM_PROPERTIES;
   }

   public boolean parseSmith(String[] properties)
   {
      if (properties.length == SMITH_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[SMITH_COL]),
                 Integer.parseInt(properties[SMITH_ROW]));
         WorldEntity entity = new Blacksmith(properties[SMITH_ID],
                 pt, imageStore.getImageList(SMITH_KEY));
         world.tryAddEntity(entity);
      }

      return properties.length == SMITH_NUM_PROPERTIES;
   }

   public boolean parseVein(String[] properties)
   {
      if (properties.length == VEIN_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[VEIN_COL]),
                 Integer.parseInt(properties[VEIN_ROW]));
         WorldEntity entity = new Vein(properties[VEIN_ID],
                 pt,
                 imageStore.getImageList(VEIN_KEY),
                 Integer.parseInt(properties[VEIN_ACTION_PERIOD]));
         world.tryAddEntity(entity);
      }

      return properties.length == VEIN_NUM_PROPERTIES;
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
