import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


public abstract class Entity
{

   private static int PROPERTY_KEY = 0;
   public EntityKind kind;
   private String id;
   private Point position;
   private List<PImage> images;
   private int imageIndex;
   private int resourceLimit;
   private int resourceCount;
   private int actionPeriod;
   private int animationPeriod;

   public static final Random rand = new Random();



   public Entity(EntityKind kind, String id, Point position,
                 List<PImage> images, int resourceLimit, int resourceCount,
                 int actionPeriod, int animationPeriod)
   {
      this.kind = kind;
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
      this.resourceLimit = resourceLimit;
      this.resourceCount = resourceCount;
      this.actionPeriod = actionPeriod;
      this.animationPeriod = animationPeriod;
   }

   public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

   //public abstract boolean parse(String[] properties, WorldModel world, ImageStore imageStore);

   protected Point getPosition(){
      return position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

   protected int getActionPeriod(){
      return actionPeriod;
   }

   protected List<PImage> getImages(){
      return images;
   }

   protected String getId(){
      return id;
   }

   protected int getResourceCount(){
      return resourceCount;
   }
   protected int getResourceLimit(){
      return resourceLimit;
   }

   protected int getPropertyKey(){
      return PROPERTY_KEY;
   }

   protected void setResourceLimit(int resourceLimit){
      this.resourceLimit = resourceLimit;
   }

   // why did i put this here
   public int getAnimationPeriod()
   {
      switch (this.kind)
      {
         case OCTO_FULL:
         case OCTO_NOT_FULL:
         case CRAB:
         case QUAKE:
         case ATLANTIS:
            return this.animationPeriod;
         default:
            throw new UnsupportedOperationException(
                    String.format("getAnimationPeriod not supported for %s",
                            this.kind));
      }
   }

   public static PImage getCurrentImage(Object entity)
   {
      if (entity instanceof Entity)
      {
         return ((Entity)entity).images.get(((Entity)entity).imageIndex);
      }

      else
      {
         throw new UnsupportedOperationException(
                 String.format("getCurrentImage not supported for %s",
                         entity));
      }
   }


   public boolean transformNotFull(WorldModel world,
                                          EventScheduler scheduler, ImageStore imageStore)
   {
      if (this.resourceCount >= this.resourceLimit)
      {
         Entity octo = new FullOcto(EntityKind.OCTO_FULL, this.id, this.position,
                 this.images, this.resourceLimit,0,
                 this.actionPeriod, this.animationPeriod);

         world.removeEntity(this);
         scheduler.unscheduleAllEvents(this);

         world.addEntity(octo);
         scheduleActions(world, scheduler, imageStore);

         return true;
      }

      return false;
   }

   public void transformFull(WorldModel world,
                             EventScheduler scheduler, ImageStore imageStore)
   {
      Entity octo = new NotFullOcto(EntityKind.OCTO_NOT_FULL, this.id, this.position,
              this.images, this.resourceLimit,0,
               this.actionPeriod, this.animationPeriod);

      world.removeEntity(this);
      scheduler.unscheduleAllEvents(this);

      world.addEntity(octo);
      scheduleActions(world, scheduler, imageStore);
   }


   public boolean moveToCrab(WorldModel world, Entity target, EventScheduler scheduler)
   {
      if (adjacent(this.position, target.position))
      {
         world.removeEntity(target);
         scheduler.unscheduleAllEvents(target);
         return true;
      }
      else
      {
         Point nextPos = this.nextPositionCrab(world, target.position);

         if (!this.position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(this, nextPos);
         }
         return false;
      }
   }

   public Point nextPositionCrab(WorldModel world,
                                        Point destPos)
   {
      int horiz = Integer.signum(destPos.x - this.position.x);
      Point newPos = new Point(this.position.x + horiz,
              this.position.y);

      Optional<Entity> occupant = world.getOccupant(newPos);

      if (horiz == 0 ||
              (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
      {
         int vert = Integer.signum(destPos.y - this.position.y);
         newPos = new Point(this.position.x, this.position.y + vert);
         occupant = world.getOccupant(newPos);

         if (vert == 0 ||
                 (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
         {
            newPos = this.position;
         }
      }

      return newPos;
   }

   public void scheduleActions(EventScheduler scheduler,
                               WorldModel world, ImageStore imageStore)
   {
      switch (this.kind)
      {
         case OCTO_FULL:
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    this.actionPeriod);
            scheduler.scheduleEvent(this, createAnimationAction(0),
                    this.getAnimationPeriod());
            break;

         case OCTO_NOT_FULL:
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    this.actionPeriod);
            scheduler.scheduleEvent(this,
                    createAnimationAction( 0), this.getAnimationPeriod());
            break;

         case FISH:
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    this.actionPeriod);
            break;

         case CRAB:
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    this.actionPeriod);
            scheduler.scheduleEvent(this,
                    createAnimationAction(0), this.getAnimationPeriod());
            break;

         case QUAKE:
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    this.actionPeriod);
            scheduler.scheduleEvent(this,
                    createAnimationAction(Quake.QUAKE_ANIMATION_REPEAT_COUNT),
                    this.getAnimationPeriod());
            break;

         case SGRASS:
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    this.actionPeriod);
            break;
         case ATLANTIS:
            scheduler.scheduleEvent(this,
                    createAnimationAction(Atlantis.ATLANTIS_ANIMATION_REPEAT_COUNT),
                    this.getAnimationPeriod());
            break;

         default:
      }
   }

   public Action createAnimationAction(int repeatCount)
   {
      return new Action(ActionKind.ANIMATION, this, null, null, repeatCount);
   }




   public void nextImage()
   {
      this.imageIndex = (this.imageIndex + 1) % this.images.size();
   }


   public static boolean adjacent(Point p1, Point p2)
   {
      return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) ||
              (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
   }

   public Action createActivityAction(WorldModel world,
                                             ImageStore imageStore)
   {
      return new Action(ActionKind.ACTIVITY, this, world, imageStore, 0);
   }



   //fix later
   public static void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.entities)
      {
         //Only start actions for entities that include action (not those with just animations)
         if (entity.actionPeriod > 0)
            entity.scheduleActions(scheduler, world, imageStore);
      }
   }
   public static void load(Scanner in, WorldModel world, ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), world, imageStore))
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

   public static boolean processLine(String line, WorldModel world,
                                     ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND.BGND_KEY:
               return parseBackground(properties, world, imageStore);
            case Octo.OCTO_KEY:
               return parseOcto(properties, world, imageStore);
            case Obstacle.OBSTACLE_KEY:
               return parseObstacle(properties, world, imageStore);
            case Fish.FISH_KEY:
               return parseFish(properties, world, imageStore);
            case Atlantis.ATLANTIS_KEY:
               return parseAtlantis(properties, world, imageStore);
            case SGrass.SGRASS_KEY:
               return parseSgrass(properties, world, imageStore);
         }
      }

      return false;
   }


   public static boolean parseBackground(String[] properties,
                                         WorldModel world, ImageStore imageStore)
   {
      if (properties.length == BGND.BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BGND.BGND_COL]),
                 Integer.parseInt(properties[BGND.BGND_ROW]));
         String id = properties[BGND.BGND_ID];
         world.setBackground(pt, new Background(id, imageStore.getImageList(id)));
      }

      return properties.length == BGND.BGND_NUM_PROPERTIES;
   }

   public static boolean parseOcto(String[] properties, WorldModel world,
                                   ImageStore imageStore)
   {
      if (properties.length == Octo.OCTO_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Octo.OCTO_COL]),
                 Integer.parseInt(properties[Octo.OCTO_ROW]));
         Entity entity = new Octo(EntityKind.OCTO_NOT_FULL, properties[Octo.OCTO_ID], pt,
                 imageStore.getImageList(Octo.OCTO_KEY),
                 Integer.parseInt(properties[Octo.OCTO_LIMIT]), 0,
                 Integer.parseInt(properties[Octo.OCTO_ACTION_PERIOD]),
                 Integer.parseInt(properties[Octo.OCTO_ANIMATION_PERIOD]));
         world.tryAddEntity(entity);
      }

      return properties.length == Octo.OCTO_NUM_PROPERTIES;
   }
   public static boolean parseObstacle(String[] properties, WorldModel world,
                                       ImageStore imageStore)
   {
      if (properties.length == Obstacle.OBSTACLE_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Obstacle.OBSTACLE_COL]),
                 Integer.parseInt(properties[Obstacle.OBSTACLE_ROW]));
         Entity entity = new Obstacle(EntityKind.OBSTACLE, properties[Obstacle.OBSTACLE_ID],
                 pt, imageStore.getImageList(Obstacle.OBSTACLE_KEY),
                 0, 0, 0, 0);
         world.tryAddEntity(entity);
      }

      return properties.length == Obstacle.OBSTACLE_NUM_PROPERTIES;
   }

   public static boolean parseFish(String[] properties, WorldModel world,
                                   ImageStore imageStore)
   {
      if (properties.length == Fish.FISH_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Fish.FISH_COL]),
                 Integer.parseInt(properties[Fish.FISH_ROW]));
         Entity entity = new Fish(EntityKind.FISH, properties[Fish.FISH_ID],
                 pt, imageStore.getImageList(Fish.FISH_KEY), 0, 0,
                 Integer.parseInt(properties[Fish.FISH_ACTION_PERIOD]), 0);
         world.tryAddEntity(entity);
      }

      return properties.length == Fish.FISH_NUM_PROPERTIES;
   }

   public static boolean parseAtlantis(String[] properties, WorldModel world,
                                       ImageStore imageStore)
   {
      if (properties.length == Atlantis.ATLANTIS_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Atlantis.ATLANTIS_COL]),
                 Integer.parseInt(properties[Atlantis.ATLANTIS_ROW]));
         Entity entity = new Atlantis(EntityKind.ATLANTIS, properties[Atlantis.ATLANTIS_ID],
                 pt, imageStore.getImageList(Atlantis.ATLANTIS_KEY), 0, 0, 0, 0);
         world.tryAddEntity(entity);
      }

      return properties.length == Atlantis.ATLANTIS_NUM_PROPERTIES;
   }

   public static boolean parseSgrass(String[] properties, WorldModel world,
                                     ImageStore imageStore)
   {
      if (properties.length == SGrass.SGRASS_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[SGrass.SGRASS_COL]),
                 Integer.parseInt(properties[SGrass.SGRASS_ROW]));
         Entity entity =
                 new SGrass(EntityKind.SGRASS, properties[SGrass.SGRASS_ID],
                 pt, imageStore.getImageList(SGrass.SGRASS_KEY), 0, 0,
                 Integer.parseInt(properties[SGrass.SGRASS_ACTION_PERIOD]), 0);
         //EntityKind.QUAKE, QUAKE_ID, position, images, 0, 0, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD
         world.tryAddEntity(entity);
      }

      return properties.length == SGrass.SGRASS_NUM_PROPERTIES;
   }
}
