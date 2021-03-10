import processing.core.PImage;

import java.util.List;

public class BGND extends Entity{
    public static final String BGND_KEY = "background";
    public static final int BGND_NUM_PROPERTIES = 4;
    public static final int BGND_ID = 1;
    public static final int BGND_COL = 2;
    public static final int BGND_ROW = 3;

    public BGND(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        super(kind, id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }

}
