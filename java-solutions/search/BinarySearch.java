package search;

public class BinarySearch {
    // Pred - args[1..end] - decreasing throughout the function
    // min - exist and single
    public static void main(String[] args) {
        // Pred - args[1..end] - decreasing throughout the function
        // => For all i : 1 < i < args.length =} args[i] >= args[i + 1]
        int num = Integer.parseInt(args[0]);
        // For all i : 1 < i < args.length =} args[i] >= args[i + 1]
        int[] mas = new int[args.length - 1];
        // For all i : 1 < i < args.length =} args[i] >= args[i + 1] && mas.length = args.length - 1
        for (int i = 1; i < args.length; i++) {
            // For all 1 < i < args.length =} args[i] >= args[i + 1] && mas.length = args.length - 1
            // && mas[i - 1] = args[i] && (if i < args.length - 1) mas[i] = args[i + 1]
            // => mas[i - 1] >= mas[i]
            mas[i - 1] = Integer.parseInt(args[i]);
        }
        // For all i : 1 <= i < args.length - 1 =} mas[i - 1] >= mas[i] && mas.length = args.length - 1
        // For all i : 0 < i < mas.length =} mas[i - 1] >= mas[i]
        // => mas - decreasing throughout the function
        System.out.println(IterativeBinSearch(num, mas));
        // R = minIndex(num)
    }

    //minIndex(num) : i from -1 to mas.length - 1 : (i == 0 && a[0] <= x) || mas[i - 1] > num 

    // mas - decreasing throughout the function
    public static int IterativeBinSearch(int num, int[] mas) {
        // -1 < minIndex(num) && minIndex(num) <= mas.length
        int lBound = -1;
        // lBound < minIndex(num) && minIndex(num) <= mas.length
        int rBound = mas.length;
        // lBound < minIndex(num) && minIndex(num) <= rBound
        while (lBound + 1 < rBound) {
            // lbound + 1 < rBound && lBound < minIndex(num) && minIndex(num) <= rBound
            int mid = (lBound + rBound) / 2;
            // mid = (lBound + rBound) / 2 > (2 * lBound + 1)/2 = lBound + 0.5 > lBound
            // Для rBound - аналогично
            // => lBound < mid < rBound && lBound <  minIndex(num) <= rBound
            if (mas[mid] <= num) {
                // lBound < mid < rBound && lBound <  minIndex(num) <= rBound && mas[mid] <= num
                rBound = mid;
                // mas[mid] <= num && mas - decrease
                // => minIndex(num) <= mid
                // => lBound < minIndex(num) <= rBound
            } else {
                // lBound < mid < rBound && lBound <  minIndex(num) <= rBound && mas[mid] > num
                lBound = mid;
                // mas[mid] > num && mas - decrease
                // => minIndex(num) > mid
                // => lBound < minIndex(num) <= rBound
            }
            // lBound < minIndex(num) <= rBound
        }
        // lBound < minIndex(num) && minIndex(num) <= rBound && lBound + 1 >= rBound
        // => rBound = minIndex(num)
        return rBound;
    }
    // R = minIndex(num)

    // mas - decreasing throughout the function
    // low < minIndex(num) <= high
    public static int RecursionBinSearch(int num, int low, int high, int mas[]) {
        if (low + 1 == high){
            // low < minIndex(num) <= high && low + 1 == high
            // => high = minIndex(num)
            return high;
        }
        // low < minIndex(num) <= high && low + 1 != high
        // => low + 1 < high
        int mid = (low + high) / 2;
        // mid = (low + high) / 2 > (2 * low + 1)/2 = low + 0.5 > low
        // Для high - аналогично
        // => low < mid < high && low <  minIndex(num) <= high
        if (mas[mid] <= num) {
            // low < mid < high && low <  minIndex(num) <= high && mas[mid] <= num
            // mas[mid] <= num && mas - decrease
            // => minIndex(num) <= mid
            // => low < minIndex(num) <= mid
            return RecursionBinSearch(num, low, mid, mas);
        }
        else {
            // low < mid < high && low <  minIndex(num) <= high && mas[mid] > num
            // mas[mid] > num && mas - decrease
            // => minIndex(num) > mid
            // => mid < minIndex(num) <= high
            return RecursionBinSearch(num, mid, high, mas);
        }
    }
    // R = minIndex(num)
}
