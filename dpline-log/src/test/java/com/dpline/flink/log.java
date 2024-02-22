package com.dpline.flink;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class log {


    /**
     * @param args
     * @throws Exception
     */
    private static Charset charset = Charset.forName("UTF8");// 创建GBK字符集
    public static void main(String[] args) throws Exception {
        SocketChannel channel= SocketChannel.open(new InetSocketAddress("www.itbuluoge.com",80));
        String line="GET / HTTP/1.1 \r\n";
        line+="HOST:www.itbuluoge.com\r\n";
        line+="\r\n";
        channel.write(charset.encode(line));
        ByteBuffer buffer = ByteBuffer.allocate(1024);// 创建1024字节的缓冲
        int size=channel.read(buffer);
        while(size!=-1)
        {
            buffer.flip();
            if(buffer.hasRemaining())
            {
                System.out.print(charset.decode(buffer));
            }
            buffer.clear();
            size = channel.read(buffer);
        }
    }
}
