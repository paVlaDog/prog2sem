package queue;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.Math.max;
import static java.lang.Math.min;

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

    Pred: true
    Post: n = n' && For i=1..n: a[i]' = func(a[i])
        map(function) – создать очередь, содержащую результаты применения функции

    Pred: true
    Post:
    For i=1..n
        if(pred(x[i]) = true) a' Э x[i]
        else a' !Э x[i]
    For all a[i], a[j] : pred(x1) = pred(x2) = true && i < j; index(a[i]) in a' < index(a[j]) in a'
        filter(predicate) – создать очередь, содержащую элементы, удовлетворяющие предикату
*/

public class ArrayQueue extends AbstractQueue implements Queue{
    private Object[] elements;
    private int head;
    private int tail;

    public ArrayQueue() {
        elements = new Object[2];
        head = 0;
        tail = 0;
        size = 0;
    }

    public Queue create() {
        return new ArrayQueue();
    }

    protected void enqueueImpl(Object element){
        ensureCapacity(size);
        elements[head] = element;
        head = (head + 1) % elements.length;
    }

    public void push(final Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(size);
        size++;
        tail = (tail - 1 + elements.length) % elements.length;
        elements[tail] = element;
    }

    private void ensureCapacity(int oldSize) {
        if (oldSize >= elements.length) {
            elements = Arrays.copyOf(elements, oldSize * 2 + 1);
            if (tail >= head) {
                System.arraycopy(elements, tail, elements, elements.length - oldSize + tail, oldSize - tail);
                tail = elements.length - oldSize + tail;
            }
        }
    }

    protected Object elementImpl(){
        return elements[tail];
    }

    public Object peek() {
        assert size != 0;
        return elements[(head - 1 + elements.length) % elements.length];
    }

    protected Object dequeueImpl(){
        Object result = elements[tail];
        elements[tail] = null;
        tail = (tail + 1) % elements.length;
        return result;
    }

    public Object remove() {
        assert size != 0;
        size--;
        head = (head - 1 + elements.length) % elements.length;
        Object result = elements[head];
        elements[head] = null;
        return result;
    }

    public int count(final Object element) {
        Objects.requireNonNull(element);
        int ans = 0;
        for (int i = 0; i < size; i++) {
            if (elements[(tail + i) % elements.length].equals(element)){
                ans++;
            }
        }
        return ans;
    }

    protected void clearImpl(){
        for (int i = 0; i < size; i++) {
            elements[(tail + i) % elements.length] = null;
        }
        head = 0;
        tail = 0;
    }

    public Queue mapImpl(Queue newQueue, Function<Object, Object> function){
        for (int i = 0; i < size; i++){
            newQueue.enqueue(function.apply(elements[(tail + i) % elements.length]));
        }
        return newQueue;
    }

    public Queue filterImpl(Queue newQueue, Predicate<Object> predicate){
        for (int i = 0; i < size; i++){
            if (predicate.test(elements[(tail + i) % elements.length])) {
                newQueue.enqueue(elements[(tail + i) % elements.length]);
            }
        }
        return newQueue;
    }
}
