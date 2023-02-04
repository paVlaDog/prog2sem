package queue;

public class MyArrayQueueADTTest {
    public static void main(String[] args) {
        ArrayQueueADT queue1 = ArrayQueueADT.create();
        ArrayQueueADT queue2 = ArrayQueueADT.create();

        for (int i = 0; i < 5; i++) {
            ArrayQueueADT.enqueue(queue1, "El" + i);
        }
        while (!ArrayQueueADT.isEmpty(queue1)) {
            System.out.println(ArrayQueueADT.size(queue1) + " " + ArrayQueueADT.element(queue1));
            ArrayQueueADT.dequeue(queue1);
            if (ArrayQueueADT.size(queue1) == 1) {
                ArrayQueueADT.clear(queue1);
                System.out.println(ArrayQueueADT.size(queue1));
            }
        }

        for (int i = 0; i < 5; i++) {
            ArrayQueueADT.enqueue(queue1,"El" + i);
        }
        while (!ArrayQueueADT.isEmpty(queue1)) {
            System.out.println(ArrayQueueADT.size(queue1) + " " + ArrayQueueADT.element(queue1));
            ArrayQueueADT.dequeue(queue1);
        }

        for (int i = 0; i < 5; i++) {
            ArrayQueueADT.enqueue(queue2,"El" + i);
        }
        while (!ArrayQueueADT.isEmpty(queue2)) {
            System.out.println(ArrayQueueADT.size(queue2) + " " + ArrayQueueADT.element(queue2));
            ArrayQueueADT.dequeue(queue2);
        }
    }
}
