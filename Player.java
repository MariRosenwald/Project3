import processing.core.PImage;

import java.util.List;

public class Player extends Entity{
    private boolean isAlive;

    public Player(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod, boolean isAlive) {
        super(kind, id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
        this.isAlive = isAlive;
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

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
