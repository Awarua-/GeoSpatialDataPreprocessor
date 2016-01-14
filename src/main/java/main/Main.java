package main;

import controller.Controller;

public class Main {

    private static Controller controller;
    public static final int numWorkerThreads = 10;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String filepath = Main.class.getResource("../user_update.csv").getPath();
        String filepath2 = Main.class.getResource("../user_event.csv").getPath();

//        controller = new Controller(Arrays.copyOfRange(args, 1, args.length));
        controller = new Controller(new String[]{filepath, filepath2});
        controller.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            long endTime = System.currentTimeMillis();

            System.out.println(((endTime - startTime) / 1000));
        }));
    }
}
