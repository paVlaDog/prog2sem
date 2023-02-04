package queue;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Queue {
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

        public void enqueue(final Object element);

        public Object element();

        public Object dequeue();

        public int size();

        public boolean isEmpty();

        public void clear();

        public Queue map(Function<Object, Object> function);

        public Queue filter(Predicate<Object> predicate);

}
