package channel;

public class text {
    /**
     * Channel：channel与文件描述符或者socket是一一对应
     * FileChannel：
     *  FileChannel用于本地读取文件
     *  FileChannel总是阻塞式的，因此不能被置于非阻塞模式。
     *  transferFrom与transferTo实现Zero-Copy
     * ServerSocketChannel/SocketChannel：
     *  ServerSocketChannel用于服务端Socket连接
     *  SocketChannel用于客户端Socket连接
     * DatagramChannel：
     *  面向UDP
     *
     * Channel对应文件或者Socket
     *
     */
}
