package winnie

import scala.swing._
import util.Random.nextInt
import java.awt.{Dimension, Graphics2D, Color, Image, Toolkit}
  
object VoorRuben extends SimpleSwingApplication {
   
   import java.awt.event.{ActionEvent}
   import javax.swing.{Timer, AbstractAction}
   import scala.swing.event._
   import scala.swing.event.Key._
   
   var game = new Game()

   override def top = new MainFrame {
      title      = "Ruben en Winnie"
      background = Color.lightGray
      
      contents = new Panel() {
         focusable     = true
         preferredSize = new Dimension(400, 430)

         listenTo(keys)

         override def paint(g: Graphics2D) = game.draw(g)
         
         reactions += {
            case KeyPressed(_, Left,  _, _) => step(West);
            case KeyPressed(_, Right, _, _) => step(East);
            case KeyPressed(_, Up,    _, _) => step(North);
            case KeyPressed(_, Down,  _, _) => step(South);
         }
      }

     def step(dir: Direction) {
            if (game.active)
              game.doMove(dir)
            repaint()
     }

   }
} 

class Game {
   var winnie   = Winnie.initialPos
   var active  = true
   
   val offset     = Point(10, 30) // position top-left cell
   val cellSize   = 15 // width and height of a single cell
   val bordWidth  = 25
   val bordHeight = 25
   
   def dim = Point(bordWidth, bordHeight) * cellSize
   
   def cell(p: Point): Point = offset + p * cellSize

   def doMove(direction: Direction) {
      val p = direction.nextPoint(winnie)
      if (!(p.x < 0 || p.y < 0 || (p.x + Winnie.img.getWidth(null)) >= bordWidth || (p.y + Winnie.img.getHeight(null)) >= bordHeight))
         winnie = p
   }

   def draw(g: Graphics2D) {
      // draw cell area
      g.setColor(Color.white)
      g.fillRect(offset.x, offset.y, dim.x, dim.y)
      g.setColor(Color.black)
      g.drawRect(offset.x, offset.y, dim.x, dim.y)
      // draw objects
      drawWinnie(g)
   }
   
   def drawWinnie(g: Graphics2D) {
      def drawImage(p: Point) = {
         g.drawImage(Winnie.img, p.x, p.y, null)
      }
   
      drawImage(cell(winnie))
   }
   
}


object Winnie {
   private val toolkit = Toolkit.getDefaultToolkit()
   val img = toolkit.getImage("winnie.jpg")

   def initialPos: Point =
      Point(5,5)
}

abstract sealed class Direction(val unit: Point) {
  def nextPoint(point: Point) = point + unit
}
case object North extends Direction(Point(0, -1))
case object East  extends Direction(Point(1, 0))
case object South extends Direction(Point(0, 1))
case object West  extends Direction(Point(-1, 0))

case class Point(x: Int, y: Int) {
   def + (that: Point): Point = 
      Point(x + that.x, y + that.y)
   
   def * (n: Int): Point = 
      Point(x*n, y*n)
}
