import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import processing.core.*;

/*
VirtualWorld is our main wrapper
It keeps track of data necessary to use Processing for drawing but also keeps track of the necessary
components to make our world run (eventScheduler), the data in our world (WorldModel) and our
current view (think virtual camera) into that world (WorldView)
 */

public final class VirtualWorld
   extends PApplet
{
   public static final int TIMER_ACTION_PERIOD = 100;

   public static final int VIEW_WIDTH = 640;
   public static final int VIEW_HEIGHT = 480;
   public static final int TILE_WIDTH = 32;
   public static final int TILE_HEIGHT = 32;
   //og : 2
   public static final int WORLD_WIDTH_SCALE = 4;
   //og: 2
   public static final int WORLD_HEIGHT_SCALE = 1;

   public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   public static final String IMAGE_LIST_FILE_NAME = "imagelist";
   public static final String DEFAULT_IMAGE_NAME = "background_default";
   public static final int DEFAULT_IMAGE_COLOR = 0x808080;

   public static final String LOAD_FILE_NAME = "world.sav";

   public static final String FAST_FLAG = "-fast";
   public static final String FASTER_FLAG = "-faster";
   public static final String FASTEST_FLAG = "-fastest";
   public static final double FAST_SCALE = 0.5;
   public static final double FASTER_SCALE = 0.25;
   public static final double FASTEST_SCALE = 0.10;

   public static double timeScale = 1.0;
   private boolean start= true;
   private boolean redraw=false;
   public boolean alive = true;
   private boolean Idied = false;

   private boolean WON = false;

   public ImageStore imageStore;
   public WorldModel world;
   public WorldView view;
   public EventScheduler scheduler;

   public long next_time;

   PImage img = this.loadImage("images/player1.bmp");
   PImage img2 = this.loadImage("images/player2.bmp");
   PImage img3 = this.loadImage("images/player3.bmp");
   PImage img4 = this.loadImage("images/player4.bmp");
   boolean pastFirstCheckpoint = false;
   boolean pastSecondCheckpoint = false;

   List<PImage> playerImages = Arrays.asList(img, img2, img3, img4);
   Player player = new Player(EntityKind.PLAYER,"player",new Point(4,9), playerImages, 0,0,0,4, alive);

   PImage chaserImg = this.loadImage("images/s1.bmp");
   List<PImage> chaserImages = Arrays.asList(chaserImg);
   PImage chaserImgL = this.loadImage("images/s2.bmp");
   List<PImage> chaserImagesL = Arrays.asList(chaserImgL);
   chasers chaser1 = new chasers(EntityKind.CHASERS,"chaser1",new Point(7,0), chaserImages, 0,0,0,0);
   chasers chaser2 = new chasers(EntityKind.CHASERS,"chaser2",new Point(0,0), chaserImages, 0,0,0,0);
   chasers chaser3 = new chasers(EntityKind.CHASERS,"chaser3",new Point(31,14), chaserImages, 0,0,0,0);
   LeadChaser chaser4 = new LeadChaser(EntityKind.CHASERS,"chaser4",new Point(50,14), chaserImagesL, 0,0,0,0);

   PImage goalImg = this.loadImage("images/atlantis0.bmp");
   List<PImage> goalImgs = Arrays.asList(goalImg);
   Goal goal = new Goal(EntityKind.GOAL,"goal",new Point(79,4), goalImgs, 0,0,0,0);



   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   public void setup()
   {
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);

      Entity.scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void draw()
   {
      if (start)
      {
         background(249, 220, 120);
         textSize(40);
         fill(0, 0, 0);
         text("WELCOME TO BEACH RUN", 70, 100);
         textSize(25);
         fill(0, 0, 0);
         text("Pick up the beach ball and bring it home!", 75, 175);
         fill(0, 0, 0);
         text("...but don't let the boys catch you...", 100, 210);
         fill(0, 0, 0);
         text("Click here to start", 190, 420);
         fill(0, 0, 0);
         text("Use arrow keys to move left/right/up/down", 50, 290);
         fill(0, 0, 0);
         text("Use 'A' and 'D' to move your view" , 110, 325);
         fill(0, 102, 153, 51);
         start= false;
      }
      if(WON){
         redraw = false;
         background(0, 250, 154);
         textSize(40);
         fill(0, 0, 0);
         text("CONGRATULATIONS!!!!", 80, 140);
         textSize(30);
         text("YOU'VE FOUND THE BEACH BALL", 80, 260);
         fill(0, 0, 0);
         text("Click to restart the game!", 120, 400);
         fill(0, 102, 153, 51);
         pastFirstCheckpoint = false;
         pastSecondCheckpoint = false;
         if (mousePressed){
            restart();
            start = true;
            WON = false;

         }
      }
//      System.out.println(player.isAlive());
      if(!player.isAlive()){
         redraw = false;
         background(240, 50, 26);
         textSize(40);
         fill(0, 0, 0);
         text("...The boys caught you...", 70, 210);
         fill(0, 0, 0);
         text("Click to restart the game!", 70, 300);
         fill(0, 102, 153, 51);
         if (mousePressed){
            start = true;
            Idied = true;
            restart();
         }
      }

      if(redraw && player.isAlive()) {
         long time = System.currentTimeMillis();
         if (time >= next_time) {
            this.scheduler.updateOnTime(time);
            next_time = time + TIMER_ACTION_PERIOD;
         }
         view.drawViewport(view);
         view.drawCharacter(view);
         if(Idied){
            restart();
         }
         loadEntity("",this,world, player);
         loadEntity("", this, world, chaser1);
         loadEntity("", this, world, chaser2);
         loadEntity("", this, world, chaser3);
         loadEntity("", this, world, chaser4);
         loadEntity("", this, world, goal);
         if(player.getPosition().x > 10)
            pastFirstCheckpoint = true;

         if(player.getPosition().x > 22)
            pastSecondCheckpoint = true;

         Idied = false;
         center();
      }


   }

   public void restart(){
      world.removeEntity(player);
      world.removeEntity(chaser1);
      world.removeEntity(chaser2);
      world.removeEntity(chaser3);
      world.removeEntity(chaser4);

      loadEntity("",this,world, player);
      loadEntity("", this, world, chaser1);
      loadEntity("", this, world, chaser2);
      loadEntity("", this, world, chaser3);
      loadEntity("", this, world, chaser4);
      loadEntity("", this, world, goal);

      player.setPosition(new Point(4, 9));
      chaser1.setPosition(new Point(7, 0));
      chaser2.setPosition(new Point(0, 0));
      chaser3.setPosition(new Point(31, 14));
      chaser4.setPosition(new Point(50, 14));

      pastFirstCheckpoint = false;
      pastSecondCheckpoint = false;
      player.setAlive(true);
      view.shiftView(-1000,0);
   }

   public void mousePressed()
   {
      if (start==false){
         redraw=true;
      }
   }

   public void center()
   {
//      System.out.println(view.getViewport().col);
      if(view.getViewport().col == player.getPosition().x)
      {
         view.shiftView(-3,0);
      }
      if(view.getViewport().col+19 == player.getPosition().x)
      {
         view.shiftView(3,0);
      }

   }

   public void keyPressed()
   {

      int change = 1;
      if(keyCode == 'A')
         view.shiftView(-10,-10);
      if(keyCode =='D')
         view.shiftView(10,10);

      if (key == CODED)
      {
         if(pastFirstCheckpoint)
            this.moveChaser(world, player, chaser1);
         if(pastSecondCheckpoint)
            this.moveChaser(world,player,chaser3);

         this.moveChaser(world, player, chaser2);
         this.moveLChaser(world, player, chaser4);

         int dx = 0;
         int dy = 0;
         Point p;
         switch (keyCode)
         {
            case UP:
               p = new Point(player.getPosition().x, player.getPosition().y-change);
               if(world.withinBounds(p) && world.getOccupancyCell(p)!=null) {
                  if (world.getOccupancyCell(p).getClass() == goal.getClass()) {
                     WON = true;
                  }
               }
               if(!world.isOccupied(p)) {
                  world.moveEntity(player, p);
                  dy = -1;
               }
               break;
            case DOWN:
               p = new Point(player.getPosition().x, player.getPosition().y+change);
               if(world.withinBounds(p) && world.getOccupancyCell(p)!=null) {
                  if (world.getOccupancyCell(p).getClass() == goal.getClass()) {
                     WON = true;
                  }
               }
               if(!world.isOccupied(p)) {
                  world.moveEntity(player, p);
                  dy = 1;
               }
               break;
            case LEFT:
               p = new Point(player.getPosition().x-change, player.getPosition().y);
               if(world.withinBounds(p) && world.getOccupancyCell(p)!=null) {
                  if (world.getOccupancyCell(p).getClass() == goal.getClass()) {
                     WON = true;
                  }
               }
               if(!world.isOccupied(p)) {
                  world.moveEntity(player, p);
                  dx = -1;
               }
               break;
            case RIGHT:
               p = new Point(player.getPosition().x+change, player.getPosition().y);
               if(world.withinBounds(p) && world.getOccupancyCell(p)!=null) {
                  if (world.getOccupancyCell(p).getClass() == goal.getClass()) {
                     WON = true;
                  }
               }
               if(!world.isOccupied(p)) {
                  world.moveEntity(player, p);
                  dx = 1;
               }
               break;
         }
         view.shiftView(dx,dy);
      }

   }

   public static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
         imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   public static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private static void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void loadEntity(String filename, PApplet screen, WorldModel world, Entity p)
   {
      try{
         //below line is never used fyi
         Scanner in = new Scanner((new File("images/player1.bmp")));

         world.addEntity(p);

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

   }

   private void moveChaser(WorldModel world, Entity p, chasers chaser){
      Point next = world.nextPoint(chaser, player, Idied);
      world.moveEntity(chaser, next);
   }

   private void moveLChaser(WorldModel world, Entity p, LeadChaser chaser){
      Point po = chaser.getPosition();
      Point next = world.nextPointL(chaser, player, Idied);
      System.out.println(next);
      world.moveEntity(chaser, next);
      world.removeEntityAt(po);
   }

   public static void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         Entity.load(in, world, imageStore);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
