package channel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestFileChannel {

    public static void main(String[] args) {
        nio();
    }

    public static void io() {
        InputStream in;
        in = null;
        try {
            in = new BufferedInputStream(new FileInputStream("src/test.txt"));
            byte[] buf = new byte[1024];
            int bytesRead = in.read(buf);
            while (bytesRead != -1) {
                for (int i = 0; i < bytesRead; i++)
                    System.out.print((char) buf[i]);
                bytesRead = in.read(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void nio() {
        RandomAccessFile aFile = null;
        try {
            aFile = new RandomAccessFile("src/test.txt", "rw");
            FileChannel fileChannel = aFile.getChannel();
            /**
             * 缓冲区，实际上是一个容器，一个连续数组。
             * Channel提供从文件、网络读取数据的渠道，但是读写的数据都必须经过Buffer。
             */
            /**
             * ByteBuffer.allocateDirect(1024)：直接缓冲区
             *
             * allocate是基于HeapByteBuffer，是写在jvm堆上面的一个buffer，底层的本质是一个数组
             * allocateDirect是基于DirectByteBuffer，底层的数据其实是维护在操作系统的内存中，实现零拷贝
             * HeapByteBuffer与JVM打交道，DirectByteBuffer与外设大交道
             *
             *
             */
            ByteBuffer buf = ByteBuffer.allocate(1024);
            /**
             * 将Channel中的属性写入Buffer
             * 或者调用Buffer.put(字节)写入数据
             *
             *
             * fileChannel.truncate(1024)：截取指定文件长度，后面的将不会读取
             * fileChannel.force()方法将通道里尚未写入磁盘的数据强制写到磁盘上。
             */
            int bytesRead = fileChannel.read(buf);
            System.out.println(bytesRead);
            while (bytesRead != -1) {
                /**
                 * 看下flip和hasRemaining源码就知道了
                 */
                buf.flip();
                while (buf.hasRemaining()) {
                    /**
                     * 从Buffer中读数据
                     * 获取调用channel.write(buf)，将buf中的数据写入Channel
                     */
                    System.out.print((char) buf.get());
                }
                buf.compact();
                bytesRead = fileChannel.read(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (aFile != null) {
                    aFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /**
         * Buffer是用来存储元素的列表
         * 通过这几个变量来保存这个数据的当前位置状态：
         * capacity：缓存区数据长度
         * position：下一个要操作的数据元素的位置
         * limit：缓冲区数组中不可操作的下一个元素的位置：limit<=capacity
         * mark：用于记录当前position的前一个位置或者默认是-1
         *
         * ByteBuffer.allocate(1024)：创建指定大小的缓存区(byte)
         *
         * ByteBuffer.flip():设置position，limit，mark
         *  limit = position;
         *  position = 0;
         *  mark = -1;
         *
         * 读取完调用clear方法，情况缓存区
         * clear:
         *  position = 0;
         *  limit = capacity;
         *  mark = -1;
         *  但实际上Buffer并未清空，只是修改了记录值，因为在读取时是根据记录值来读取的
         *  下次写入数据时，自动覆盖
         * 如果只是想读写buffer中部分数据，下次再读取后续数据(边缘触发)
         * 读取完数据后，可以调用compact()方法，clear清除所有数据
         * compact只会清除读取的数据，将position设到最后一个未读元素正后面，其他不变
         *
         * buf.mark()：将当前位置标记到mark中
         *  mark = position
         * buf.reset()：将post设置为mark，之前标记的
         *  position = mark;
         * buf.rewind()：将position设置为原始位置，这样就可以再次读取
         *  position = 0;
         *  mark = -1;
         *
         */
    }

    /**
     * Zero-Copy
     * FileChannel的transferFrom()方法可以将数据从其他Channel传输到FileChannel中
     */
    public static void transferFrom() {
        RandomAccessFile fromFile = null;
        RandomAccessFile toFile = null;
        try {
            fromFile = new RandomAccessFile("src/fromFile.xml", "rw");
            FileChannel fromChannel = fromFile.getChannel();
            toFile = new RandomAccessFile("src/toFile.txt", "rw");
            FileChannel toChannel = toFile.getChannel();
            long position = 0;
            long count = fromChannel.size();
            System.out.println(count);
            toChannel.transferFrom(fromChannel, position, count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fromFile != null) {
                    fromFile.close();
                }
                if (toFile != null) {
                    toFile.close();
                    System.out.println();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Zero-Copy
     * transferTo()方法将数据从FileChannel传输到其他的channel中。
     */
    public static void transferTo() {
        RandomAccessFile fromFile = null;
        RandomAccessFile toFile = null;
        try {
            fromFile = new RandomAccessFile("src/fromFile.txt", "rw");
            FileChannel fromChannel = fromFile.getChannel();
            toFile = new RandomAccessFile("src/toFile.txt", "rw");
            FileChannel toChannel = toFile.getChannel();
            long position = 0;
            long count = fromChannel.size();
            System.out.println(count);
            fromChannel.transferTo(position, count, toChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fromFile != null) {
                    System.out.println("");
                    fromFile.close();
                }
                if (toFile != null) {
                    toFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
