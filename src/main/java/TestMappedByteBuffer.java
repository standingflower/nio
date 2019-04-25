import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 处理大文件时，可以使用BufferedReader,BufferedInputStream这类带缓冲的IO类
 * 如果文件过大，可以使用MappedByteBuffer
 *
 * 内存：物理内除，虚拟内存
 *  物理内存就是实际大小
 *  虚拟内存：物理内存只有1G，当时程序运行需要2G，将程序中暂时不使用的数据持久化到
 *           硬盘上，这样保证程序正确运行，这个过程叫 页面切换
 *
 * MappedByteBuffer就是将文件映射到虚拟内存中
 * MappedByteBuffer是DirectByteBuffer(堆外内存)，实现的Zero-Copy
 * 传统IO采用JVM内存
 */
public class TestMappedByteBuffer {

    public static void main(String[] args) {

    }

    public static void byteBuffer(){
        RandomAccessFile aFile = null;
        FileChannel fc = null;
        try{
            aFile = new RandomAccessFile("src/1.ppt","rw");
            fc = aFile.getChannel();
            long timeBegin = System.currentTimeMillis();
            ByteBuffer buff = ByteBuffer.allocate((int) aFile.length());
            buff.clear();
            fc.read(buff);
            long timeEnd = System.currentTimeMillis();
            System.out.println("Read time: "+(timeEnd-timeBegin)+"ms");
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(aFile!=null){
                    aFile.close();
                }
                if(fc!=null){
                    System.out.println();
                    fc.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void mappedByteBuffer(){
        RandomAccessFile aFile = null;
        FileChannel fc = null;
        try{
            aFile = new RandomAccessFile("src/1.ppt","rw");
            fc = aFile.getChannel();
            long timeBegin = System.currentTimeMillis();
            /**
             * FileChannel提供了map方法来把文件影射为内存映像文件
             * 把文件的从position开始的size大小的区域映射为内存映像文件，mode指出了 可访问该内存映像文件的方式：
             * READ_ONLY,（只读）： 试图修改得到的缓冲区将导致抛出 ReadOnlyBufferException.(MapMode.READ_ONLY)
             * READ_WRITE（读/写）：对缓存区的更改最终将传播到文件
             * PRIVATE（专用）：对缓存区的更改不会传播到文件
             *
             * MapperByteBuffer是ByteBuffer的子类
             *  force()：缓冲区是READ_WRITE模式下，此方法对缓冲区内容的修改强行写入文件；
             *  load()：将缓冲区的内容载入内存，并返回该缓冲区的引用；
             *  isLoaded()：如果缓冲区的内容在物理内存中，则返回真，否则返回假；
             */
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, aFile.length());
            long timeEnd = System.currentTimeMillis();
            System.out.println("Read time: "+(timeEnd-timeBegin)+"ms");
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(aFile!=null){
                    aFile.close();
                    System.out.println();
                }
                if(fc!=null){
                    fc.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }


}
