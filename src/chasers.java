import processing.core.PImage;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class chasers  extends Entity {
    private PathingStrategy strategy = new AStarPathingStrategy();
    //private Player player;

    public chasers(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        //this.player = player;
        super(kind, id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }

//    public void changeX(int change) {
//        Point p = new Point(this.getPosition().x + change, this.getPosition().y);
//        this.setPosition(p);
//    }
//
//    public void changeY(int change) {
//        Point p = new Point(this.getPosition().x, this.getPosition().y + change);
//        this.setPosition(p);
//    }




}
