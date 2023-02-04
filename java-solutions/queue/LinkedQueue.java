package queue;

import java.util.Arrays;
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

public class LinkedQueue extends AbstractQueue implements Queue {
    private Node head;
    private Node tail;

    public Queue create() {
        return new LinkedQueue();
    }

    protected void enqueueImpl(Object element){
        Node QueueNode = head;
        head = new Node(element, null);
        if (size == 0) {
            tail = head;
        } else {
            QueueNode.next = head;
        }
    }

    protected Object elementImpl(){
        return tail.value;
    }

    protected Object dequeueImpl(){
        Object result = tail.value;
        tail = tail.next;
        return result;
    }

    protected void clearImpl(){
        if (tail == null) return;
        while (tail.next != null){
            Node buffer = tail.next;
            tail.next = null;
            tail = buffer;
        }
    }

    public Queue mapImpl(Queue newQueue, Function<Object, Object> function){
        Node point = tail;
        if (size == 0) {
            return newQueue;
        }
        newQueue.enqueue(function.apply(point.value));
        while (point.next != null){
            point = point.next;
            newQueue.enqueue(function.apply(point.value));
        }
        return newQueue;
    }

    public Queue filterImpl(Queue newQueue, Predicate<Object> predicate){
        Node point = tail;
        if (size == 0) {
            return newQueue;
        }
        if (predicate.test(point.value)) {
            newQueue.enqueue(point.value);
        }
        while (point.next != null){
            point = point.next;
            if (predicate.test(point.value)) {
                newQueue.enqueue(point.value);
            }
        }
        return newQueue;
    }

    private class Node {
        private Object value;
        private Node next;

        public Node(Object value, Node next) {
            assert value != null;

            this.value = value;
            this.next = next;
        }
    }
}
