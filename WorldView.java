import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

/*
WorldView ideally mostly controls drawing the current part of the whole world
that we can see based on the viewport
*/

final class WorldView
{
   public PApplet screen;
   public WorldModel world;
   public int tileWidth;
   public int tileHeight;
   public Viewport viewport;

   public WorldView(int numRows, int numCols, PApplet screen, WorldModel world,
      int tileWidth, int tileHeight)
   {
      this.screen = screen;
      this.world = world;
      this.tileWidth = tileWidth;
      this.tileHeight = tileHeight;
      this.viewport = new Viewport(numRows, numCols);
   }

   public static void drawBackground(WorldView view)
   {
      for (int row = 0; row < view.viewport.numRows; row++)
      {
         for (int col = 0; col < view.viewport.numCols; col++)
         {
            Point worldPoint = view.viewport.viewportToWorld(col, row);
            Optional<PImage> image = view.world.getBackgroundImage(worldPoint);
            if (image.isPresent())
            {
               view.screen.image(image.get(), col * view.tileWidth,
                       row * view.tileHeight);
            }
         }
      }
   }

   public void shiftView(int colDelta, int rowDelta)
   {
      int newCol = clamp(this.viewport.col + colDelta, 0,
              this.world.numCols - this.viewport.numCols);
      int newRow = clamp(this.viewport.row + rowDelta, 0,
              this.world.numRows - this.viewport.numRows);

      this.viewport.shift(newCol, newRow);
   }


   public static int clamp(int value, int low, int high)
   {
      return Math.min(high, Math.max(value, low));
   }

   public static void drawEntities(WorldView view)
   {
      for (Entity entity : view.world.entities)
      {
         Point pos = entity.getPosition();

         if (view.viewport.contains(pos))
         {
            Point viewPoint = view.viewport.worldToViewport(pos.x, pos.y);
            view.screen.image(entity.getCurrentImage(entity),
                    viewPoint.x * view.tileWidth, viewPoint.y * view.tileHeight);
         }
//         PImage img = VirtualWorld.loadImage("images/player.bmp");
//         view.screen.image();
      }
   }

   public static void drawViewport(WorldView view)
   {
      drawBackground(view);
      drawEntities(view);
   }

   public void drawCharacter(WorldView view) {
   }

   public Viewport getViewport() {
      return viewport;
   }
}
