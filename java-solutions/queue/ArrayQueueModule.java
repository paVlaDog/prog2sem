package queue;

import java.util.Arrays;
import java.util.Objects;

/*
    Model: a[1]..a[n]
    Invariant: n >= 0 && for i=1..n: a[i] != null

    Let immutable(n): for i=1..n a'[i] = a[i]
    Let immutableShift(n) for i=1..n a'[i + 1] = a[i]
    Let immutableShiftBack(n) for i=1..n a'[i] = a[i + 1]

    Pred: el != null
    Post: n' = n + 1 && a[1] == el && immutableShift(n)
        push - добавить элемент в начало очереди;

    Pred: el != null
    Post: n' = n + 1 && a[n'] == el && immutable(n)
        enqueue – добавить элемент в очередь;

    Pred: n >= 1
    Post: R = a[1] && immutable(n) && n' = n
        element – первый элемент в очереди;

    Pred: n >= 1
    Post: R = a[n] && immutable(n) && n' = n
         peek – вернуть последний элемент в очереди;

    Pred: n >= 1
    Post: R = a[1] && immutableShiftBack(n') && n' = n - 1
        dequeue – удалить и вернуть первый элемент в очереди;

    Pred: n >= 1
    Post: R = a[n] && immutable(n') && n' = n - 1
        remove – вернуть и удалить последний элемент из очереди;

    Pred: true
    Post:  R == n && immutable(n) && n' = n
        size – текущий размер очереди;

    Pred: true
    Post: R == (n == 0) && immutable(n) && n' = n
        isEmpty – является ли очередь пустой;

    Pred: true
    Post: n = 0
        clear – удалить все элементы из очереди.

    Pred: el != null
    Post: n = n' && immutable(n)
    && R = sum(For i=1..n: if(a[n] == el)i=1 else i=0)
        count - количество вхождений элемента в очередь.
*/
public class ArrayQueueModule {
    private static Object[] elements = new Object[2];
    private static int head = 0;
    private static int tail = 0;
    private static int size = 0;

    public static void enqueue(final Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(size);
        size++;
        elements[head] = element;
        head = (head + 1) % elements.length;
    }

    public static void push(final Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(size);
        size++;
        tail = (tail - 1 + elements.length) % elements.length;
        elements[tail] = element;
    }

    private static void ensureCapacity(int oldSize) {
        if (oldSize >= elements.length) {
            elements = Arrays.copyOf(elements, oldSize * 2 + 1);
            if (tail >= head){
                System.arraycopy(elements, tail, elements, elements.length - oldSize + tail, oldSize - tail);
                tail = elements.length - oldSize + tail;
            }
        }
    }

    public static Object element () {
        assert size != 0;
        return elements[tail];
    }

    public static Object peek() {
        assert size != 0;
        return elements[(head - 1 + elements.length) % elements.length];
    }


    public static Object dequeue () {
        assert size != 0;
        size--;
        Object result = elements[tail];
        elements[tail] = null;
        tail = (tail + 1) % elements.length;
        return result;
    }

    public static Object remove() {
        assert size != 0;
        size--;
        head = (head - 1 + elements.length) % elements.length;
        Object result = elements[head];
        elements[head] = null;
        return result;
    }

    public static int count(final Object element) {
        Objects.requireNonNull(element);
        int ans = 0;
        for (int i = 0; i < size; i++) {
            if (elements[(tail + i) % elements.length].equals(element)){
                ans++;
            }
        }
        return ans;
    }

    public static int size() {
        return size;
    }

    public static boolean isEmpty() {
        return size == 0;
    }

    public static void clear() {
        for (int i = 0; i < size; i++) {
            elements[(tail + i) % elements.length] = null;
        }
        head = 0;
        tail = 0;
        size = 0;
    }


}
