import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class SGrass extends Fish{
    public static final String SGRASS_KEY = "seaGrass";
    public static final int SGRASS_NUM_PROPERTIES = 5;
    public static final int SGRASS_ID = 1;
    public static final int SGRASS_COL = 2;
    public static final int SGRASS_ROW = 3;
    public static final int SGRASS_ACTION_PERIOD = 4;

    public SGrass(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        super(EntityKind.SGRASS, id, position, images, 0, 0,
                actionPeriod, 0);
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(this.getPosition());

        if (openPt.isPresent())
        {
            Entity fish = new Fish(EntityKind.FISH, FISH_ID_PREFIX + this.getId(),
                    openPt.get(), imageStore.getImageList(FISH_KEY), 0, 0, FISH_CORRUPT_MIN +
                            rand.nextInt(FISH_CORRUPT_MAX - FISH_CORRUPT_MIN), 0);
            world.addEntity(fish);
            Entity.scheduleActions(world, scheduler, imageStore);
        }

        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore),
                this.getActionPeriod());
    }
}
