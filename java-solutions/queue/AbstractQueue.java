package queue;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/*
        Model: a[1]..a[n]
        Invariant: n >= 0 && for i=1..n: a[i] != null

        Let immutable(n): for i=1..n a'[i] = a[i]
        Let immutableShiftBack(n) for i=1..n a'[i] = a[i + 1]

        Pred: el != null
        Post: n' = n + 1 && a[n'] == el && immutable(n)
            enqueue – добавить элемент в очередь;

        Pred: n >= 1
        Post: R = a[1] && immutable(n) && n' = n
            element – первый элемент в очереди;

        Pred: n >= 1
        Post: n' = n - 1 && immutableShiftBack(n') && R = a[1]
            dequeue – удалить и вернуть первый элемент в очереди;

        Pred: true
        Post: immutable(n) && n' = n && R == n
            size – текущий размер очереди;

        Pred: true
        Post: R == !(n > 0) && immutable(n) && n' = n
            isEmpty – является ли очередь пустой;

        Pred: true
        Post: n = 0
            clear – удалить все элементы из очереди.

        Pred: true
        Post: n = n' && immutable(n) && R = (For i=1..n: a[i] = function(a[i])
        map(function) – создать очередь, содержащую результаты применения функции

        Pred: true
        Post: n = n' && immutable(n)
        && R = queue(a[i] in R if predicate(a[i]))
        filter(predicate) – создать очередь, содержащую элементы, удовлетворяющие предикату
*/

public abstract class AbstractQueue {
    protected int size;

    public void enqueue(final Object element) {
        Objects.requireNonNull(element);
        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);

    public Object element() {
        assert size != 0;
        return elementImpl();
    }

    protected abstract Object elementImpl();

    public Object dequeue() {
        assert size != 0;
        size--;
        return dequeueImpl();
    }

    protected abstract Object dequeueImpl();

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        clearImpl();
        size = 0;
    }

    protected abstract void clearImpl();

    public Queue map(Function<Object, Object> function) {
        Queue queue = create();
        return mapImpl(queue, function);
    }

    protected abstract Queue mapImpl(Queue queue, Function<Object, Object> function);

    public Queue filter(Predicate<Object> predicate) {
        Queue queue = create();
        return filterImpl(queue, predicate);
    }

    protected abstract Queue filterImpl(Queue queue, Predicate<Object> predicate);

    protected abstract Queue create();
}
