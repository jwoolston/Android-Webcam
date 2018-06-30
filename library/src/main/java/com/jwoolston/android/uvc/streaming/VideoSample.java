package com.jwoolston.android.uvc.streaming;

import android.support.annotation.NonNull;
import java.nio.ByteBuffer;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoSample {

    private final ByteBuffer buffer;

    VideoSample(int maxSize) {
        buffer = ByteBuffer.wrap(new byte[maxSize]);
    }

    @NonNull
    public ByteBuffer getBuffer() {
        return buffer;
    }
}
