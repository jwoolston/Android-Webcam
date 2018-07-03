package com.jwoolston.android.uvc.streaming;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class FrameSampleFactory implements VideoSampleFactory {

    private final int maxPayload;
    private final int maxFrameSize;

    private ByteBuffer currentBuffer;
    private Boolean currentFrameFlag;

    public FrameSampleFactory(int maxPayload, int maxFrameSize) {
        this.maxPayload = maxPayload;
        this.maxFrameSize = maxFrameSize;
        currentBuffer = ByteBuffer.wrap(new byte[maxFrameSize]);
    }

    @Nullable
    @Override
    public VideoSample addPayload(@NotNull Payload payload) throws IOException {
        VideoSample retval = null;
        if (payload.isEndOfFrame()) { //(currentFrameFlag == null || currentFrameFlag != payload.isFrameIdSet()) {
            // This is the start of a new frame, prepare to dump the old
            currentBuffer.flip();
            retval = new VideoSample(currentBuffer);
            currentFrameFlag = payload.isFrameIdSet();
            currentBuffer = ByteBuffer.wrap(new byte[maxFrameSize]);
        }
        try {
            payload.dumpToBuffer(currentBuffer);
        } catch (BufferUnderflowException | BufferOverflowException e) {
            throw new IOException(e);
        }

        return retval;
    }
}
