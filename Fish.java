import processing.core.PImage;

import java.util.List;

public class Fish extends Crab{
    public static final String FISH_KEY = "fish";
    public static final int FISH_NUM_PROPERTIES = 5;
    public static final int FISH_ID = 1;
    public static final int FISH_COL = 2;
    public static final int FISH_ROW = 3;
    public static final int FISH_ACTION_PERIOD = 4;

    public static final String FISH_ID_PREFIX = "fish -- ";
    public static final int FISH_CORRUPT_MIN = 20000;
    public static final int FISH_CORRUPT_MAX = 30000;
    public static final int FISH_REACH = 1;

    public Fish(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        super(EntityKind.FISH, id, position, images, 0, 0, actionPeriod, 0);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Point pos;  // store current position before removing
        pos = this.getPosition();

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        Entity crab = new Crab(EntityKind.CRAB, this.getId() + CRAB_ID_SUFFIX,
                pos, imageStore.getImageList(CRAB_KEY),0, 0,
                this.getActionPeriod() / CRAB_PERIOD_SCALE,
                CRAB_ANIMATION_MIN +
                        rand.nextInt(CRAB_ANIMATION_MAX - CRAB_ANIMATION_MIN));

        world.addEntity(crab);
        Entity.scheduleActions(world, scheduler, imageStore);
    }

}
