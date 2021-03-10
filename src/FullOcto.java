import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class FullOcto extends Octo{
    public FullOcto(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        super(EntityKind.OCTO_FULL, id, position, images, resourceLimit, resourceLimit, actionPeriod, animationPeriod);
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> fullTarget = world.findNearest(this.getPosition(),
                EntityKind.ATLANTIS);

        if (fullTarget.isPresent() &&
                this.moveToFull(world, fullTarget.get(), scheduler))
        {
            //at atlantis trigger animation
            this.scheduleActions(world, scheduler, imageStore);

            //transform to unfull
            this.transformFull(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(new FullOcto(kind, this.getId(), this.getPosition(), this.getImages(), this.getResourceLimit(), this.getResourceCount(), this.getActionPeriod(), this.getAnimationPeriod()),
                    createActivityAction(world, imageStore),
                    this.getActionPeriod());
        }
    }
}
