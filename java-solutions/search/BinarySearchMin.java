package search;


public class BinarySearchMin {
    // Pred - args[0..minInd] - strong decreasing, args[minInd + 1..end] - strong increasing && exist min
    // -> (For all i : 0 < i < endLeft  args[i] > args[i + 1]) && (For all i : endLeft + 1 <= i < end  args[i] < args[i + 1])
    // 1) if (args[endLeft] <= args[endLeft + 1]) 
    // -> ] minInd = endLeft
    // -> (For all i : 0 < i < minInd  args[i] > args[i + 1]) && (For all i : minInd + 1 <= i < end  args[i] < args[i + 1])
    // 2) if (args[endLeft] > args[endLeft + 1])
    // -> ] minInd = endLeft + 1
    // -> (For all i : 0 < i < minInd  args[i] > args[i + 1]) && (For all i : minInd + 1 <= i < end  args[i] < args[i + 1])
    // (For all i : 0 < i < endLeft  args[i] > args[i + 1], args[endLeft] > args[endLeft + 1], 
    // For all i : endLeft + 1 <= i < end  args[i] < args[i + 1])
    // -> For all i : args[minInd] <= args[i]
    // 1)if i < minInd -> args[i] > args[i + 1] > ... > args[minInd], 
    // 2)if i > minInd -> args[i] > args[i - 1] > ... > args[minInd + 1] >= args[minInd])
    // ] min(args) : For all i : args[i] >= min(args) (min exist)
    // -> args[minInd] = min(args) && (For all i : 0 < i < minInd  args[i] > args[i + 1]) 
    // && (For all i : minInd + 1 <= i < end  args[i] < args[i + 1])
    // Post - R = min(args)
    public static void main(String[] args) {
        // Pred - args[0..minInd] - strong decreasing, args[minInd + 1..end] - strong increasing
        // -> (For all i : 0 < i < minInd  args[i] > args[i + 1]) && (For all i : minInd + 1 <= i < end  args[i] < args[i + 1])
        int[] mas = new int[args.length];
        // (For all i : 0 < i < minInd  args[i] > args[i + 1]) && (For all i : minInd + 1 <= i < end  args[i] < args[i + 1]) 
        // && mas.length == args.length
        for (int i = 0; i < args.length; i++) {
            // (For all i : 0 < i < minInd  args[i] > args[i + 1]) && (For all i : minInd + 1 <= i < end  args[i] < args[i + 1]) 
            // && mas.length == args.length && For all i : mas[i] = args[i] && For all i : args[i] >= min(args) 
            // -> (For all i : 0 < i < minInd  mas[i] > mas[i + 1]) && (For all i : minInd + 1 <= i < end  mas[i] < mas[i + 1])
            // && For all i : mas[i] >= min(mas) && min(mas) == min(args) == args[minInd] == mas[minInd]
            mas[i] = Integer.parseInt(args[i]);
        }
        // (For all i : 0 < i < minInd  mas[i] > mas[i + 1]) && (For all i : minInd + 1 <= i < end  mas[i] < mas[i + 1])
        // && min(mas) = mas[minInd]
        System.out.println(iterativeBinSearchMin(mas));
        // R = min(mas) = min(args)
    }

    // Pred: (For all i : 0 < i < minInd  args[i] > args[i + 1]) && (For all i : minInd + 1 <= i < end  args[i] < args[i + 1])
    // && min(mas) = mas[minInd]
    // Post: R = min(mas)
    public static int iterativeBinSearchMin(int[] mas) {
        // 0 <= minInd < mas.length
        int l = 0;
        // l <= minInd < mas.length
        int r = mas.length;
        // l <= minInd < r
        while (l + 1 < r) {
            // l + 1 < r && l <= minInd < r
            int mid = (r + l) / 2;
            // l = l * 2 / 2 < (l + r) / 2 = mid < r * 2 / 2 < r
            // -> l + 1 < r && l <= minInd < r && l < mid < r 
            if (mas[mid - 1] > mas[mid]) {
                // l + 1 < r && l <= minInd < r && mas[mid - 1] > mas[mid]
                // -> mas[mid - 1 .. mid] - discreasing -> mid <= minInd < r
                // -> mid <= minInd < r
                l = mid;
                // l <= minInd < r
            } else {
                // l + 1 < r && l < minInd < r && mas[mid - 1] <= mas[mid]  
                // -> 1) mas[mid - 1] < mas[mid]
                // -> mas[mid - 1 .. mid] - increasing -> l <= minInd <= mid - 1 < mid
                // -> 2) mas[mid - 1] == mas[mid] && For all i != minInd : (args[i] > args[i + 1] || args[i] < args[i + 1])
                // -> mas[mid - 1] == mas[mid] == min -> minInd == mas[mid - 1] -> l <= minInd == mid - 1 < mid
                // -> l <= minInd < mid
                r = mid;
                // l <= minInd < r
            }
            // l <= minInd < r
        }
        // l <= minInd < r && l + 1 >= r
        // -> l = minInd
        return mas[l];
    }

    // Pred: (For all i : 0 < i < minInd  args[i] > args[i + 1]) && (For all i : minInd + 1 <= i < end  args[i] < args[i + 1])
    // && l <= minInd < r && min(mas) = mas[minInd]
    // Post: R = min(mas)
    public static int recursionBinSearchMin(int l, int r, int mas[]) {
        if (l + 1 >= r){
            // l <= minInd < r && l + 1 >= r && min - exist and single
            // -> l = minInd
            return mas[l];
        }
        // l <= minInd < r && l + 1 > r
        int mid = (r + l) / 2;
        // l = l * 2 / 2 < (l + r) / 2 = mid < r * 2 / 2 < r
        // -> l <= minInd < r && l + 1 > r && l < mid < r 
        if (mas[mid - 1] > mas[mid]) {
            // l + 1 < r && l <= minInd < r && mas[mid - 1] > mas[mid]
            // -> mas[mid - 1 .. mid] - discreasing -> mid <= minInd < r
            // -> mid <= minInd < r
            return recursionBinSearchMin(mid, r, mas);
        }
        else {
            // l + 1 < r && l < minInd < r && mas[mid - 1] <= mas[mid]  
            // -> 1) mas[mid - 1] < mas[mid]
            // -> mas[mid - 1 .. mid] - increasing -> l <= minInd <= mid - 1 < mid
            // -> 2) mas[mid - 1] == mas[mid] && For all i != minInd : (args[i] > args[i + 1] || args[i] < args[i + 1])
            // -> mas[mid - 1] == mas[mid] == min -> minInd == mas[mid - 1] -> l <= minInd == mid - 1 < mid
            // -> l <= minInd < mid
            return recursionBinSearchMin(l, mid, mas);
        }
    }
    // R = minInd
}
