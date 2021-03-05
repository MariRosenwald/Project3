/*
Viewport ideally helps control what part of the world we are looking at for drawing only what we see
Includes helpful helper functions to map between the viewport and the real world
 */


import processing.core.PApplet;
import processing.core.PImage;

import java.util.List;
import java.util.Map;

final class Viewport
{
   public int row;
   public int col;
   public int numRows;
   public int numCols;

   public static final int COLOR_MASK = 0xffffff;


   public Viewport(int numRows, int numCols)
   {
      this.numRows = numRows;
      this.numCols = numCols;
   }


   public boolean contains(Point p)
   {
      return p.y >= this.row && p.y < this.row + this.numRows &&
              p.x >= this.col && p.x < this.col + this.numCols;
   }



   public Point viewportToWorld(int col, int row)
   {
      return new Point(col + this.col, row + this.row);
   }

   public Point worldToViewport(int col, int row)
   {
      return new Point(col - this.col, row - this.row);
   }


   public static void setAlpha(PImage img, int maskColor, int alpha)
   {
      int alphaValue = alpha << 24;
      int nonAlpha = maskColor & COLOR_MASK;
      img.format = PApplet.ARGB;
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         if ((img.pixels[i] & COLOR_MASK) == nonAlpha)
         {
            img.pixels[i] = alphaValue | nonAlpha;
         }
      }
      img.updatePixels();
   }

   public void shift(int col, int row)
   {
      this.col = col;
      this.row = row;
   }

}
