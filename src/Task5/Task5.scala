import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


object Task5 {

  def time[T](f: => T): Unit = {
    val start = System.nanoTime()
    val fTime = f
    println("Czas "+ (System.nanoTime() - start))
  }


  //1)
  def fib(n: Int): Int =
    n match
      case 0 => 0
      case 1 => 1
      case _ => fib(n - 1) + fib(n - 2)

  def fibTail(n: Int): Int =
    @tailrec
    def fibIn(n: Int, a: Int, b: Int): Int =
      n match
        case 0 => a
        case 1 => b
        case _ => fibIn(n - 1, b, a + b)

    fibIn(n, 0, 1)

  def fibPar(n: Int): Int = {
    n match
      case 0 => 0
      case 1 => 1
      case _ =>
        val f1 = Future(fib(n - 1))
        val f2 = Future(fib(n - 2))
        val f1Result = Await.result(f1, Duration.Inf)
        val f2Result = Await.result(f2, Duration.Inf)
        f1Result + f2Result
  }


  //2)
  def mergeSort(list: List[Int]): List[Int] =
    list match {
      case Nil => Nil
      case head :: Nil => List(head)
      case _ =>
        val (left, right) = list splitAt list.length / 2
        merge(mergeSort(left), mergeSort(right))
    }

  def mergeSortPar(list: List[Int]): List[Int] =
    list match {
      case Nil => Nil
      case head :: Nil => List(head)
      case _ =>
        val (left, right) = list splitAt list.length / 2
        val leftF = Future(mergeSort(left))
        val rightF = Future(mergeSort(right))
        val resultLeft = Await.result(leftF, Duration.Inf)
        val resultRight = Await.result(rightF, Duration.Inf)
        merge(resultLeft, resultRight)
    }

  def merge(list1: List[Int], list2: List[Int]): List[Int] =
    (list1, list2) match {
      case (Nil, _) => list2
      case (_, Nil) => list1
      case (head1 :: tail1, head2 :: tail2) =>
        if (head1 < head2) head1 :: merge(tail1, list2)
        else head2 :: merge(list1, tail2)
    }


  def main(args: Array[String]): Unit = {
    println("Fibonacci \n4: ")

    print("Bez zr??wnoleglenia: ")
    time(fib(4))

    print("Bez zr??wnoleglenia (ogonowa): ")
    time(fibTail(4))

    print("Zr??wnoleglenie: ")
    time(fibPar(4))

    println("\n20: ")

    print("Bez zr??wnoleglenia: ")
    time(fib(20))

    print("Bez zr??wnoleglenia (ogonowa): ")
    time(fibTail(20))

    print("Zr??wnoleglenie: ")
    time(fibPar(20))

    println("\n40: ")
    print("Bez zr??wnoleglenia: ")
    time(fib(40))

    print("Bez zr??wnoleglenia (ogonowa): ")
    time(fibTail(40))

    print("Zr??wnoleglenie: ")
    time(fibPar(40))

    //Dla mniejszych liczb lepsze jest rozwi??zanie jednow??tkowe ni?? wielow??tkowe.
    //Przy wi??kszych liczbach wielow??tkowo???? wypada lepiej
    //We wszystkich wypadkach u??ycie rekursji ogonowej wypada najlepiej pod wzgl??dem czasu
    //4:
    //bez zr??wnoleglenia: 20 000- 30 000   ogonowa: 7 000-10 000   zr??wnoleglenie: ponad 70 000 000
    //20:
    //bez zr??wnoleglenia: 250 000- 500 000   ogonowa: 5 000-8 000   zr??wnoleglenie: 1 700 000-1 800 000
    //40:
    //bez zr??wnoleglenia: oko??o 530 000 000   ogonowa: 7 000-8 000   zr??wnoleglenie: oko??o 320 000 000



    print("\nMergesort \n100 element??w")
    val list = List.fill(100)(1000).map(scala.util.Random.nextInt)
    print("\nBez zr??wnoleglenia: ")
    time(mergeSort(list))
    print("Zr??wnoleglenie: ")
    time(mergeSortPar(list))

    print("\n50 element??w")
    val list2 = List.fill(50)(1000).map(scala.util.Random.nextInt)
    print("\nBez zr??wnoleglenia: ")
    time(mergeSort(list2))
    print("Zr??wnoleglenie: ")
    time(mergeSortPar(list2))

    print("\n1 000 element??w")
    val list1k = List.fill(1000)(1000).map(scala.util.Random.nextInt)
    print("\nBez zr??wnoleglenia: ")
    time(mergeSort(list1k))
    print("Zr??wnoleglenie: ")
    time(mergeSortPar(list1k))

    print("\n10 000 element??w")
    val list10k = List.fill(10000)(1000).map(scala.util.Random.nextInt)
    print("\nBez zr??wnoleglenia: ")
    time(mergeSort(list10k))
    print("Zr??wnoleglenie: ")
    time(mergeSortPar(list10k))

    //Przewaga zr??wnoleglenia dla du??ych danych ju?? od 10 tys. element??w (niemal 2 razy kr??tszy czas)
    //Dla ma??ych danych np 50 element??w lepsze rozwi??zanie jednow??tkowe
    //50 element??w:
    //bez zr??wnoleglenia: 200 000- 400 000   zr??wnoleglenie: 300 000- 400 000
    //100 element??w:
    //bez zr??wnoleglenia: oko??o 2 000 000   zr??wnoleglenie: oko??o 1 000 000
    //1000 element??w:
    //bez zr??wnoleglenia: ponad 2 000 000  zr??wnoleglenie:  2 000 000 - 5 000 000
    //10 000 element??w:
    //bez zr??wnoleglenia:  8 000 000- 10 000 000  zr??wnoleglenie: 5 000 000 -6 000 000

  }
}
