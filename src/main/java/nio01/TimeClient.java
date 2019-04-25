package nio01;

public class TimeClient {
    public static void main(String[] args) {

        int port = 1234;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(100);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001")
                .start();
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001")
                .start();

        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001")
                .start();
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001")
                .start();
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001")
                .start();
    }
}
