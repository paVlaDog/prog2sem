package queue;

public class MyArrayQueueModuleTest {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            ArrayQueueModule.enqueue("El" + i);
        }
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(ArrayQueueModule.size() + " " + ArrayQueueModule.element());
            ArrayQueueModule.dequeue();
            if (ArrayQueueModule.size() == 1) {
                ArrayQueueModule.clear();
                System.out.println(ArrayQueueModule.size());
            }
        }
        for (int i = 0; i < 5; i++) {
            ArrayQueueModule.enqueue("El" + i);
        }
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(ArrayQueueModule.size() + " " + ArrayQueueModule.element());
            ArrayQueueModule.dequeue();
        }
    }
}
