public class Data implements Comparable<Data>
{
  String word;
  int total;
  int[] arr;
  
  public Data(String word, int total, int[] arr){
    this.word = word;
    this.total = total;
    this.arr = arr;
  }    
  public int compareTo(Data w){
    if (this.total > w.total)
      return 1;
    else if (this.total < w.total)
      return -1;
    return 0;
  }
  public String toString() {
      return word + " " + total + " ";
  }
}