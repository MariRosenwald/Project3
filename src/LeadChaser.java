import processing.core.PImage;

import java.util.List;

public class LeadChaser extends Entity{
    private PathingStrategy strategy = new NewPath();
    //private Player player;

    public LeadChaser(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        //this.player = player;
        super(kind, id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }
}
