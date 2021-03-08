import processing.core.PImage;

import java.util.List;

public class Player extends Entity{

    public Player(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        super(kind, id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }

    public void changeX(int change)
    {
        Point p = new Point(this.getPosition().x+change, this.getPosition().y);
        this.setPosition(p);
    }

    public void changeY(int change)
    {
        Point p = new Point(this.getPosition().x, this.getPosition().y+change);
        this.setPosition(p);
    }


}
