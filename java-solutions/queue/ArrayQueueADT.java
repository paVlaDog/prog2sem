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
    Post: n' = n - 1 && immutableShiftBack(n') && R = a[1]
        dequeue – удалить и вернуть первый элемент в очереди;

    Pred: n >= 1
    Post: n' = n - 1 && immutable(n') && R = a[n]
        remove – вернуть и удалить последний элемент из очереди;

    Pred: true
    Post: immutable(n) && n' = n && R == n
        size – текущий размер очереди;

    Pred: true
    Post: R == !(n > 0) && immutable(n) && n' = n
        isEmpty – является ли очередь пустой;

    Pred: true
    Post: n = 0
        clear – удалить все элементы из очереди.

    Pred: el != null
    Post: n = n' && immutable(n)
    && R = sum(For i=1..n: if(a[n] == el)i=1 else i=0)
        count - количество вхождений элемента в очередь.
*/
public class ArrayQueueADT {
    private Object[] elements = new Object[2];
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    public static ArrayQueueADT create() {
        ArrayQueueADT queue = new ArrayQueueADT();
        queue.elements = new Object[2];
        return queue;
    }

    private static void ensureCapacity(ArrayQueueADT queue, int oldSize) {
        if (oldSize >= queue.elements.length) {
            queue.elements = Arrays.copyOf(queue.elements, oldSize * 2 + 1);
            if (queue.tail >= queue.head){
                int newTail = queue.elements.length - oldSize + queue.tail;
                System.arraycopy(queue.elements, queue.tail, queue.elements, newTail, queue.elements.length - newTail);
                queue.tail = newTail;
            }
        }
    }

    public static void enqueue(ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(element);
        queue.ensureCapacity(queue, queue.size);
        queue.size++;
        queue.elements[queue.head] = element;
        queue.head = (queue.head + 1) % queue.elements.length;
    }

    public static void push(ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(element);
        queue.ensureCapacity(queue, queue.size);
        queue.size++;
        queue.tail = (queue.tail - 1 + queue.elements.length) % queue.elements.length;
        queue.elements[queue.tail] = element;
    }

    public static Object element (ArrayQueueADT queue) {
        assert queue.size != 0;
        return queue.elements[queue.tail];
    }

    public static Object peek(ArrayQueueADT queue) {
        assert queue.size != 0;
        return queue.elements[(queue.head - 1 + queue.elements.length) % queue.elements.length];
    }

    public static Object dequeue (ArrayQueueADT queue) {
        assert queue.size-- != 0;
        Object result = queue.elements[queue.tail];
        queue.elements[queue.tail] = null;
        queue.tail = (queue.tail + 1) % queue.elements.length;
        return result;
    }

    public static Object remove(ArrayQueueADT queue) {
        assert queue.size-- != 0;
        queue.head = (queue.head - 1 + queue.elements.length) % queue.elements.length;
        Object result = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        return result;
    }

    public static int count(ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(element);
        int ans = 0;
        for (int i = 0; i < queue.size; i++) {
            if (queue.elements[(queue.tail + i) % queue.elements.length].equals(element)){
                ans++;
            }
        }
        return ans;
    }

    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    public static void clear(ArrayQueueADT queue) {
        for (int i = 0; i < queue.size; i++) {
            queue.elements[(queue.tail + i) % queue.elements.length] = null;
        }
        queue.head = 0;
        queue.tail = 0;
        queue.size = 0;
    }


}
