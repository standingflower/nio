package socketchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 服务端采用NIO
 */
public class ServerNIO {
    private static final int BUF_SIZE = 1024;
    private static final int PORT = 8080;
    private static final int TIMEOUT = 3000;

    public static void main(String[] args) {
        selector();
    }



    public static void selector() {
        Selector selector = null;
        /**
         * ServerSocketChannel 指服务器端连接
         * SocketChannel 指客户端链接
         */
        ServerSocketChannel ssc = null;
        try {
            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(PORT));
            /**
             * 设置为非阻塞
             * 与Selector一起使用时，Channel必须处于非阻塞模式下，意味着不能将FileChannel与Selector一起使用，
             * 因为FileChannel不能切换到非阻塞模式。而套接字通道都可以。
             *
             */
            ssc.configureBlocking(false);
            /**
             * Channel和Selector进行绑定
             * SelectionKey是一个interest集合，指定Selector监听Channel的类型：
             * OP_ACCEPT：服务端接收客户端的连接事件(刚开始是ACCEPT)
             * OP_CONNECT：客户端连接服务端事件
             * OP_WRITE：
             * OP_READ：
             */
            SelectionKey register = ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                if (selector.select(TIMEOUT) == 0) {
                    System.out.println("==");
                    continue;
                }
                /**
                 * 获取准备就绪的Key，SelectionKey包含的属性有：
                 *  interest集合
                 *  ready集合：key.readOps()获取准备继续集合数量
                 *  Channel
                 *  Selector
                 *  附加的对象（可选）：
                 *    可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的Channel。
                 *    将Selector和Channel绑定后，获取到Key然后执行 ---
                 *      selectionKey.attach(theObject)
                 *      也可以直接绑定 channel.register(selector, SelectionKey.OP_READ, theObject);
                 *    获取到Key时可以执行 --- selectionKey.attachment();
                 * */
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                /**
                 * Selector获取Channel：
                 *  selector.selectedKeys() --- 获取就绪的Key
                 *  int select() --- 获取继续Key的数量
                 *  int select(long timeout) --- 阻塞timeout毫秒后获取
                 *  int selectNow() --- 立即获取，可能为0
                 */
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    if (key.isWritable() && key.isValid()) {
                        handleWrite(key);
                    }
                    if (key.isConnectable()) {
                        System.out.println("isConnectable = true");
                    }
                    /**
                     * 以处理的Key需移出，不然下次仍会处理
                     */
                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (selector != null) {
                    System.out.println();
                    selector.close();
                }
                if (ssc != null) {
                    ssc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);
        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
    }

    public static void handleRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        long bytesRead = sc.read(buf);
        while (bytesRead > 0) {
            buf.flip();
            while (buf.hasRemaining()) {
                System.out.print((char) buf.get());
            }
            System.out.println();
            buf.clear();
            bytesRead = sc.read(buf);
        }
        if (bytesRead == -1) {
            sc.close();
        }
    }

    public static void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.flip();
        SocketChannel sc = (SocketChannel) key.channel();
        while (buf.hasRemaining()) {
            sc.write(buf);
        }
        buf.compact();
    }
}