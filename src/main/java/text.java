
public class text {
    public static void main (String args[]) {
    }
    /**
     * Linux环境下，JavaNIO使用的是Epoll
     * windows环境下，JavaNIO使用是Poll
     *
     * NIO与IO的区别：
     *  缓冲，非阻塞，选择器
     * IO每次请求创建线程
     * NIO单线程，阻塞于Selector
     *
     * NIO的本质是延迟IO操作到真正发生IO的时候(等待数据就绪),而不是以前的只要IO流打开了就一直等待IO操作
     */
}
