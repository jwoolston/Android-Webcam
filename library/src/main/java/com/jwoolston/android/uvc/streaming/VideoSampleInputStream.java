package com.jwoolston.android.uvc.streaming;

import android.support.annotation.NonNull;

import com.jwoolston.android.uvc.util.CircularArray;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoSampleInputStream extends InputStream {

    public static final int DEFAULT_SAMPLE_BUFFER_SIZE = 8;

    private final Object lock = new Object();

    private final CircularArray<VideoSample> sampleBuffer;

    private ByteBuffer currentData;

    private boolean isClosed;

    public VideoSampleInputStream() {
        this(DEFAULT_SAMPLE_BUFFER_SIZE);
    }

    public VideoSampleInputStream(int bufferSize) {
        sampleBuffer = new CircularArray<>(bufferSize);
        isClosed = false;
    }

    private int verifyCurrentData() throws IOException {
        if (currentData == null || !currentData.hasRemaining()) {
            synchronized (lock) {
                if (sampleBuffer.getSize() == 0) {
                    try {
                        sampleBuffer.wait();
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                }
                currentData = sampleBuffer.get(0).getBuffer();
            }
        }
        return currentData.remaining();
    }

    void addNewSample(@NonNull VideoSample sample) {
        synchronized (lock) {
            sampleBuffer.add(sample);
            sampleBuffer.notifyAll();
        }
    }

    @Override
    public int read() throws IOException {
        if (isClosed) {
            return -1;
        }
        verifyCurrentData();
        return currentData.get();
    }

    @Override
    public int read(@NonNull byte[] data) throws IOException {
        if (isClosed) {
            return -1;
        }
        int available = verifyCurrentData();
        if (available < data.length) {
            // TODO: This is inefficient if the available amount is just the trailing few bytes of a buffer
            currentData.get(data, 0, available);
            return available;
        } else {
            currentData.get(data);
            return data.length;
        }
    }

    @Override
    public int read(@NonNull byte[] data, int offset, int length) throws IOException {
        if (isClosed) {
            return -1;
        }
        int available = verifyCurrentData();
        if (available < length) {
            // TODO: This is inefficient if the available amount is just the trailing few bytes of a buffer
            currentData.get(data, offset, available);
            return available;
        } else {
            currentData.get(data, offset, length);
            return length;
        }
    }

    @Override
    public int available() throws IOException {
        if (isClosed) {
            throw new IOException("Stream is closed!");
        } else {
            return verifyCurrentData();
        }
    }

    @Override
    public void close() throws IOException {
        isClosed = true;
        super.close();
    }
}
