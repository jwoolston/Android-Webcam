package com.jwoolston.android.uvc.streaming;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class FrameSampleFactory implements VideoSampleFactory {

    public FrameSampleFactory(int maxPayload, int maxFrameSize) {

    }

    @Nullable
    @Override
    public VideoSample addPayload(@NotNull Payload payload) {
        return null;
    }
}
