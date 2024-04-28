package com.dpline.common.util;


import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class DeflaterUtils {

    /**
     * 压缩
     */
    public static String zipString(String text) {
        /**
         * 0 ~ 9 压缩等级 低到高<br>
         * public static final int BEST_COMPRESSION = 9;            最佳压缩的压缩级别<br>
         * public static final int BEST_SPEED = 1;                  压缩级别最快的压缩<br>
         * public static final int DEFAULT_COMPRESSION = -1;        默认压缩级别<br>
         * public static final int DEFAULT_STRATEGY = 0;            默认压缩策略<br>
         * public static final int DEFLATED = 8;                    压缩算法的压缩方法(目前唯一支持的压缩方法)<br>
         * public static final int FILTERED = 1;                    压缩策略最适用于大部分数值较小且数据分布随机分布的数据<br>
         * public static final int FULL_FLUSH = 3;                  压缩刷新模式，用于清除所有待处理的输出并重置拆卸器<br>
         * public static final int HUFFMAN_ONLY = 2;                仅用于霍夫曼编码的压缩策略<br>
         * public static final int NO_COMPRESSION = 0;              不压缩的压缩级别<br>
         * public static final int NO_FLUSH = 0;                    用于实现最佳压缩结果的压缩刷新模式<br>
         * public static final int SYNC_FLUSH = 2;                  用于清除所有未决输出的压缩刷新模式; 可能会降低某些压缩算法的压缩率<br>
         */
        //使用指定的压缩级别创建一个新的压缩器。
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        // 设置压缩输入数据。
        deflater.setInput(text.getBytes());
        // 当被调用时，表示压缩应该以输入缓冲区的当前内容结束。
        deflater.finish();
        byte[] bytes = new byte[256];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
        while (!deflater.finished()) {
            int length = deflater.deflate(bytes);
            outputStream.write(bytes, 0, length);
        }
        deflater.end();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static String unzipString(String zipString){
        if(StringUtils.isBlank(zipString)){
            return zipString;
        }
        byte[] decode = Base64.getDecoder().decode(zipString);
        Inflater inflater = new Inflater();
        // 设置解压缩的输入数据。
        inflater.setInput(decode);
        byte[] bytes = new byte[256];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
        try {
            while (!inflater.finished()) {
                // 将字节解压缩到指定的缓冲区中。
                int length = inflater.inflate(bytes);
                outputStream.write(bytes, 0, length);
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        } finally {
            inflater.end();
        }

        return outputStream.toString();
    }

}
