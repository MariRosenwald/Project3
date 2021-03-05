/*
Action: ideally what our various entities might do in our virutal world
 */

import java.util.Optional;

public class Action
{
   public WorldModel world;
   public ActionKind kind;
   public Entity entity;
   public ImageStore imageStore;
   public static int repeatCount;

   public Action(ActionKind kind, Entity entity, WorldModel world,
                 ImageStore imageStore, int repeatCount)
   {
      this.kind = kind;
      this.entity = entity;
      this.world = world;
      this.imageStore = imageStore;
      this.repeatCount = repeatCount;
   }

   public void executeAction(EventScheduler scheduler) {
      switch (this.kind) {
         case ACTIVITY:
            this.executeActivityAction(entity, scheduler);
            break;

         case ANIMATION:
            this.executeAnimationAction(scheduler);
            break;
      }
   }

      public void executeActivityAction(Entity e, EventScheduler scheduler)
      {
         try {
            e.executeActivity(this.world,this.imageStore,
                    scheduler);
         } catch(Exception p) {
            throw new UnsupportedOperationException(
                    String.format("executeActivityAction not supported for %s",
                            this.entity.kind));
         }
   }

   void executeAnimationAction(EventScheduler scheduler)
   {
      this.entity.nextImage();

      if (Action.repeatCount != 1)
      {
         scheduler.scheduleEvent(this.entity,
                 entity.createAnimationAction(Math.max(Action.repeatCount - 1, 0)),
                 this.entity.getAnimationPeriod());
      }
   }
}



