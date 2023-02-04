package queue;

public class MyArrayQueueTest {
    public static void main(String[] args) {
        ArrayQueue queue1 = new ArrayQueue();
        ArrayQueue queue2 = new ArrayQueue();

        for (int i = 0; i < 5; i++) {
            queue1.enqueue("El" + i);
        }
        while (!queue1.isEmpty()) {
            System.out.println(queue1.size() + " " + queue1.element());
            queue1.dequeue();
            if (queue1.size() == 1) {
                queue1.clear();
                System.out.println(queue1.size());
            }
        }

        for (int i = 0; i < 5; i++) {
            queue1.enqueue("El" + i);
        }
        while (!queue1.isEmpty()) {
            System.out.println(queue1.size() + " " + queue1.element());
            queue1.dequeue();
        }

        for (int i = 0; i < 5; i++) {
            queue2.enqueue("El" + i);
        }
        while (!queue2.isEmpty()) {
            System.out.println(queue2.size() + " " + queue2.element());
            queue2.dequeue();
        }
    }
}
