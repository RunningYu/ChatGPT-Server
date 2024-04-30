package chatgptserver;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/30
 */
class ThreadTest {

    private static Object lock =  new Object();

    public static void main(String[] args) {
        MyThread thread1 = new MyThread('1');
        MyThread thread2 = new MyThread('A');
        thread1.start();
        thread2.start();
    }

    static class MyThread extends Thread {

        private char c;

        public MyThread(char c) {
            this.c = c;
        }

        public void run() {
            int count = 0;
            while (count < 3) {
                synchronized (lock) {
                    lock.notify();
                    System.out.print(c);
                    c ++;
                    count ++;
                    if (count < 3) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException();
                        }
                    }
                }
            }
        }

    }

}

