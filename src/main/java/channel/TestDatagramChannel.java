package channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class TestDatagramChannel {

    /**
     * Java NIO中的DatagramChannel是一个能收发UDP包的通道。
     * 因为UDP是无连接的网络协议，所以不能像其它通道那样读取和写入。
     * 它发送和接收的是数据包。
     */
    public static void reveive() {
        DatagramChannel channel = null;
        try {
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(8888));
            ByteBuffer buf = ByteBuffer.allocate(1024);
            buf.clear();
            /**
             * 将接收到的数据包内容复制到指定的Buffer.
             */
            channel.receive(buf);
            buf.flip();
            while (buf.hasRemaining()) {
                System.out.print((char) buf.get());
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void send() {
        DatagramChannel channel = null;
        try {
            channel = DatagramChannel.open();
            String info = "I'm the Sender!";
            ByteBuffer buf = ByteBuffer.allocate(1024);
            buf.clear();
            buf.put(info.getBytes());
            buf.flip();
            /**
             * 直接发送，不用建立连接，UDP不可靠
             */
            int bytesSent = channel.send(buf, new InetSocketAddress("10.10.195.115", 8888));
            System.out.println(bytesSent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
