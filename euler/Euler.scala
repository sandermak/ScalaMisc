package test

/**
 * Created by IntelliJ IDEA.
 * User: sanderma
 * Date: 1/13/11
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */

class Euler {

  def euler1() = (1 to 999 filter (x => x % 5 == 0 || x % 3 == 0)).sum

  def euler2() = {
    lazy val fib = Stream.cons(0, Stream.cons(1, fib.zip(fib.tail).map { case (l,r) => l + r }))
    fib.takeWhile(_ < 4e6).filter(_ % 2 == 0).sum
  }

  def euler125() = {
    def isPalindrome(i: BigInt) = {
       val string = i.toString
       var palindrome = true
         for(pos <- 0 to string.size / 2) {
           palindrome &= string(pos) == string(string.size - pos - 1)
         }
        palindrome
    }

    import util.control.Breaks._

    val limit = 100000000
    val sqrt_limit = math.sqrt(limit).toInt
    var result = Set[BigInt]()

    for (i <- (1: BigInt) to sqrt_limit) {
      var sos = i * i
      breakable {
        for (j <- i + 1 to sqrt_limit) {
          sos += j * j
          if (sos >= limit) break
          if (isPalindrome(sos)) result += sos
        }
      }
    }

    result.sum
  }

}
