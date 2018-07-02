package com.jwoolston.android.uvc.streaming;

import android.support.annotation.NonNull;
import java.nio.ByteBuffer;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class VideoSample {

    private final ByteBuffer buffer;

    VideoSample(@NonNull ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @NonNull
    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("VideoSample{");
        sb.append("buffer=").append(buffer);
        sb.append('}');
        return sb.toString();
    }
}
