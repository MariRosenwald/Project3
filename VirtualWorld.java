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

   List<PImage> playerImages = Arrays.asList(img, img2, img3, img4);
   Player player = new Player(EntityKind.PLAYER,"player",new Point(4,9), playerImages, 0,0,0,4, alive);

   PImage chaserImg = this.loadImage("images/s1.bmp");
   List<PImage> chaserImages = Arrays.asList(chaserImg);
   chasers chaser1 = new chasers(EntityKind.CHASERS,"chaser1",new Point(7,0), chaserImages, 0,0,0,0);
   chasers chaser2 = new chasers(EntityKind.CHASERS,"chaser2",new Point(0,0), chaserImages, 0,0,0,0);

   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   public void setup()
   {
//      addTiles();
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);
//      loadCharacter("images/player.bmp", this, world);

      Entity.scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   private void addTiles() {
//         SINGLE USE ONLY DON'T RUN AGAIN
      try {
         FileWriter fw = new FileWriter(LOAD_FILE_NAME, true);
         for (int t = 41; t < 80; t++) {
            for (int i = 0; i < 15; i++) {
               fw.write("\nbackground sea " + t + " " + i);
            }
         }
         fw.close();

      }
      catch(IOException e) {
      }
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
      System.out.println(player.isAlive());
      if(!player.isAlive()){
         redraw = false;
         background(240, 50, 26);
         textSize(40);
         fill(0, 0, 0);
         text("...The boys caught you...", 70, 210);
         fill(0, 102, 153, 51);
         if (mousePressed){
            start = true;
//            player.setPosition(new Point(4, 9));
//            chaser1.setPosition(new Point(7, 0));
//            chaser2.setPosition(new Point(0, 0));
//            pastFirstCheckpoint = false;
            Idied = true;
            player.setAlive(true);
         }
      }

      if(redraw && player.isAlive()) {
         long time = System.currentTimeMillis();
         if (time >= next_time) {
            this.scheduler.updateOnTime(time);
            next_time = time + TIMER_ACTION_PERIOD;
         }
//
         view.drawViewport(view);
         view.drawCharacter(view);
         if(Idied){
            player.setPosition(new Point(4, 9));
            chaser1.setPosition(new Point(7, 0));
            chaser2.setPosition(new Point(0, 0));
            pastFirstCheckpoint = false;
         }
         loadCharacter("",this,world, player);
         loadChaser("", this, world, chaser1);
         loadChaser("", this, world, chaser2);
         if(player.getPosition().x > 10)
            pastFirstCheckpoint = true;
         Idied = false;
         center();
      }


   }

//   public void mousePressed()
//   {
//      redraw= true;
//
//   }
   public void mousePressed()
   {
      if (start==false){
         redraw=true;
      }
      int section = 0;
      Point pressed1 = mouseToPoint(mouseX, mouseY);
      Point pressed = new Point(pressed1.x+section, pressed1.y);
      writeWalls(pressed);


   }


   public void center()
   {
      System.out.println(view.getViewport().col);
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
      if(pastFirstCheckpoint)
         this.moveChaser(world, player, chaser1);

      this.moveChaser(world, player, chaser2);

      int change = 1;
      if(keyCode == 'A')
         view.shiftView(-10,-10);
      if(keyCode =='D')
         view.shiftView(10,10);

      if (key == CODED)
      {
         int dx = 0;
         int dy = 0;
         Point p;
         switch (keyCode)
         {
            case UP:
               p = new Point(player.getPosition().x, player.getPosition().y-change);
               if(!world.isOccupied(p)) {
                  world.moveEntity(player, p);
                  dy = -1;
               }
               break;
            case DOWN:
               p = new Point(player.getPosition().x, player.getPosition().y+change);
               if(!world.isOccupied(p)) {
                  world.moveEntity(player, p);
                  dy = 1;
               }
               break;
            case LEFT:
               p = new Point(player.getPosition().x-change, player.getPosition().y);
               if(!world.isOccupied(p)) {
                  world.moveEntity(player, p);
                  dx = -1;
               }
               break;
            case RIGHT:
               p = new Point(player.getPosition().x+change, player.getPosition().y);
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

   // come back
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

   private static void loadCharacter(String filename, PApplet screen, WorldModel world, Entity p)
   {
      try{
         //below line is never used fyi
         Scanner in = new Scanner((new File("images/player1.bmp")));

         world.addEntity(p);

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

   }

   private static void loadChaser(String filename, PApplet screen, WorldModel world, Entity p)
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

   private Point mouseToPoint(int x, int y)
   {
      return new Point(mouseX/32, mouseY/32);
   }



   private void writeWalls(Point p) {
//         SINGLE USE ONLY DON'T RUN AGAIN
      try {
         FileWriter fw = new FileWriter("james_world.sav", true);
         String msg = "obstacle obstacle_" + p.x + "_" + p.y + " " + p.x +" " + p.y + "\n";
         System.out.println(msg);
         fw.write(msg);
         fw.close();

      }
      catch(IOException ignored) {
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
