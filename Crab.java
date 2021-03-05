import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Crab extends Entity {
    public static final String CRAB_KEY = "crab";
    public static final String CRAB_ID_SUFFIX = " -- crab";
    public static final int CRAB_PERIOD_SCALE = 4;
    public static final int CRAB_ANIMATION_MIN = 50;
    public static final int CRAB_ANIMATION_MAX = 150;

    public Crab(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        super(EntityKind.CRAB, id, position, images,
                0, 0, actionPeriod, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> crabTarget = world.findNearest(this.getPosition(), EntityKind.SGRASS);
        long nextPeriod = this.getActionPeriod();

        if (crabTarget.isPresent())
        {
            Point tgtPos = crabTarget.get().getPosition();

            if (this.moveToCrab(world, crabTarget.get(), scheduler))
            {
                Entity quake = new Quake(EntityKind.QUAKE, Quake.QUAKE_ID, tgtPos,
                        imageStore.getImageList(Quake.QUAKE_KEY), 0, 0, Quake.QUAKE_ACTION_PERIOD, Quake.QUAKE_ANIMATION_PERIOD);

                //EntityKind.QUAKE, QUAKE_ID, position, images, 0, 0, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                this.scheduleActions(world, scheduler, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                nextPeriod);
    }


}
