package Snake

import scala.swing._
import util.Random.nextInt
import java.awt.{Dimension, Graphics2D, Color, Font}
  
object App extends SimpleSwingApplication {
   
   import java.awt.event.{ActionEvent}
   import javax.swing.{Timer, AbstractAction}
   import scala.swing.event._
   import scala.swing.event.Key._
   
   var game = new Game()

   override def top = new MainFrame {
      title      = "Scala Snake"
      background = Color.lightGray
      
      contents = new Panel() {
         focusable     = true
         preferredSize = new Dimension(400, 430)

         listenTo(keys)

         override def paint(g: Graphics2D) = game.draw(g)
         
         reactions += {
            case KeyPressed(_, Left,  _, _) => game.direction = West
            case KeyPressed(_, Right, _, _) => game.direction = East
            case KeyPressed(_, Up,    _, _) => game.direction = North
            case KeyPressed(_, Down,  _, _) => game.direction = South
         }
      }

      new Timer(200, new AbstractAction() {
         override def actionPerformed(e: ActionEvent) {
            if (game.active) game.tick
            repaint()
         }
      }).start
   }
} 

class Game {
   private var _direction: Direction = East
   def direction = _direction
   def direction_=(newDirection: Direction) {
       if(_direction.opposite != newDirection)
         _direction = newDirection
   }

   var food: List[Point] = List(Point(5, 5))
   var snake   = Snake.initial
   var counter = 0
   var active  = true
   
   val offset     = Point(10, 30) // position top-left cell
   val cellSize   = 15 // width and height of a single cell
   val bordWidth  = 25
   val bordHeight = 25
   
   def dim = Point(bordWidth, bordHeight) * cellSize
   
   def cell(p: Point): Point = offset + p * cellSize

   def tick() {
      val p = snake.nextPoint(direction)
      if (p.x < 0 || p.y < 0 || p.x >= bordWidth || p.y >= bordHeight )
         active = false
      else if (food contains p) {
         snake = snake.grow(direction)
         food  = food filterNot (_ == p)
      } else
         snake = snake.move(direction)

      if (food.isEmpty || (nextInt(30) == 0 && food.length < 3)) newFood()
      counter += 1
   }
   
   def newFood() {
      food = Point(nextInt(bordWidth), nextInt(bordHeight)) +: food
   }
   
   def draw(g: Graphics2D) {
      // draw cell area
      g.setColor(Color.white)
      g.fillRect(offset.x, offset.y, dim.x, dim.y)
      g.setColor(Color.black)
      g.drawRect(offset.x, offset.y, dim.x, dim.y)
      // draw objects
      drawFood(g)
      drawSnake(g)
      // draw status
      g.setColor(Color.black)
      g.drawString("Score: " + snake.length, 50, 20)
      g.drawString("Time: " + counter, 200, 20)
      g.setFont(new Font("sansserif", Font.BOLD, 50))
      if (!active) g.drawString("Game over", 100, 200)
   }
   
   def drawFood(g: Graphics2D) {
      def drawFoodBlock(p: Point) =
         g.fillOval(p.x, p.y, cellSize, cellSize)
   
      g.setColor(Color.green)
      food.foreach(p => drawFoodBlock(cell(p)))
   }
   
   def drawSnake(g: Graphics2D) {
      def drawSnakeBlock(p: Point) = 
         g.fillRect(p.x, p.y, cellSize, cellSize)
      
      g.setColor(Color.red)
      snake.positions.foreach(p => drawSnakeBlock(cell(p)))
   }
}

class Snake(val positions: List[Point]) {  
   def length:Int = positions.length
   def head:Point = positions.head
   
   def move(d: Direction): Snake =
      new Snake(nextPoint(d) +: positions.init)
   
   def grow(d: Direction): Snake = 
      new Snake(nextPoint(d) +: positions)
   
   def nextPoint(d: Direction): Point = head + d.unit
}

object Snake {
   def initial: Snake = 
      new Snake(for (i <- List.range(0, 10)) yield Point(10-i, 10))
}

abstract sealed class Direction(val unit: Point) {
  val opposite: Direction
  def nextPoint(point: Point) = point + unit
}
case object North extends Direction(Point(0, -1)) { val opposite = South }
case object East  extends Direction(Point(1, 0))  { val opposite = West  }
case object South extends Direction(Point(0, 1))  { val opposite = North }
case object West  extends Direction(Point(-1, 0)) { val opposite = East  }

case class Point(x: Int, y: Int) {
   def + (that: Point): Point = 
      Point(x + that.x, y + that.y)
   
   def * (n: Int): Point = 
      Point(x*n, y*n)
}
