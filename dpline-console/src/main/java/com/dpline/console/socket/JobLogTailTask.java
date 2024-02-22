package com.dpline.console.socket;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import java.io.*;
import java.nio.charset.StandardCharsets;


/**
 * 重写 Tailer
 */
public class JobLogTailTask extends Tailer {
    private final byte[] inbuf;
    private byte[] outbuf;
    private final int bufSize;
    private final File file;
    private final long delayMillis;
    private final boolean end;
    private final TailerListener listener;
    private final boolean reOpen;
    private volatile boolean run;


    public JobLogTailTask(File file, TailerListener listener, long delayMillis) {
        this(file, listener, delayMillis, false);
    }

    public JobLogTailTask(File file, TailerListener listener, long delayMillis, boolean end) {
        this(file, listener, delayMillis, end, 4096);
    }

    public JobLogTailTask(File file, TailerListener listener, long delayMillis, boolean end, int bufSize) {
        this(file, listener, delayMillis, end, false, bufSize);
    }

    public JobLogTailTask(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen, int bufSize) {
        super(file, listener, delayMillis, end, reOpen, bufSize);
        this.run = true;
        this.file = file;
        this.delayMillis = delayMillis;
        this.end = end;
        this.bufSize = bufSize;
        this.inbuf = new byte[bufSize];
        this.outbuf = new byte[bufSize];
        this.listener = listener;
        listener.init(this);
        this.reOpen = reOpen;
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, int bufSize) {
        Tailer tailer = new Tailer(file, listener, delayMillis, end, bufSize);
        Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen, int bufSize) {
        Tailer tailer = new Tailer(file, listener, delayMillis, end, reOpen, bufSize);
        Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end) {
        return create(file, listener, delayMillis, end, 4096);
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen) {
        return create(file, listener, delayMillis, end, reOpen, 4096);
    }

    public static Tailer create(File file, TailerListener listener, long delayMillis) {
        return create(file, listener, delayMillis, false);
    }

    public static Tailer create(File file, TailerListener listener) {
        return create(file, listener, 1000L, false);
    }

    public File getFile() {
        return this.file;
    }

    public long getDelay() {
        return this.delayMillis;
    }

    @Override
    public void run() {
        RandomAccessFile reader = null;
        try {
            long last = 0L;
            long position = 0L;

            while(this.run && reader == null) {
                try {
                    reader = new RandomAccessFile(this.file, "r");
                } catch (FileNotFoundException var20) {
                    this.listener.fileNotFound();
                }

                if (reader == null) {
                     Thread.sleep(this.delayMillis);
                } else {
                    position = this.end ? this.file.length() : 0L;
                    last = System.currentTimeMillis();
                    reader.seek(position);
                }
            }

            while(this.run) {
                boolean newer = FileUtils.isFileNewer(this.file, last);
                long length = this.file.length();
                if (length < position) {
                    // 长度小于 偏移量，文件已经发生了改变，或者被删除掉了数据，reader 重新定义
                    this.listener.fileRotated();

                    try {
                        RandomAccessFile save = reader;
                        reader = new RandomAccessFile(this.file, "r");
                        position = 0L;
                        IOUtils.closeQuietly(save);
                    } catch (FileNotFoundException var18) {
                        this.listener.fileNotFound();
                    }
                } else {
                    if (length > position) {
                        position = this.readLines(reader);
                        last = System.currentTimeMillis();
                    } else if (newer) {
                        position = 0L;
                        reader.seek(position);
                        position = this.readLines(reader);
                        last = System.currentTimeMillis();
                    }

                    if (this.reOpen) {
                        IOUtils.closeQuietly(reader);
                    }

                    Thread.sleep(this.delayMillis);

                    if (this.run && this.reOpen) {
                        reader = new RandomAccessFile(this.file, "r");
                        reader.seek(position);
                    }
                }
            }
        } catch (Exception var21) {
            this.listener.handle(var21);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public void stop() {
        this.run = false;
        this.listener.handle(FileDataListener.EOF_FLAG);
    }

    private long readLines(RandomAccessFile reader) throws IOException {
        long pos = reader.getFilePointer();
        long rePos = pos;

        int num;
        boolean seenCR = false;
        while(this.run && (num = reader.read(this.inbuf)) != -1) {
            int j = 0;
            for(int i = 0; i < num; ++i) {
                byte ch = this.inbuf[i];
                switch(ch) {
                    case 10:
                        seenCR = false;
                        listener.handle(byteBufferToString(this.outbuf));
                        j = 0;
                        this.outbuf = new byte[this.bufSize];
                        rePos = pos + (long)i + 1L;
                        break;
                    case 13:
                        if (seenCR) {
                            this.outbuf[j] = '\r';
                            j++;
                        }
                        seenCR = true;
                        break;
                    default:
                        if (seenCR) {
                            seenCR = false;
                            listener.handle(byteBufferToString(this.outbuf));
                            j = 0;
                            this.outbuf = new byte[this.bufSize];
                            rePos = pos + (long)i + 1L;
                        }
                        this.outbuf[j] = ch;
                        j++;
                }
            }
            pos = reader.getFilePointer();
        }

        reader.seek(rePos);
        return rePos;
    }
    private static String byteBufferToString(byte[] buffer) {
        try {
            return new String(buffer,StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
