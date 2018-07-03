package com.jwoolston.android.uvc.webserver;


import android.content.Context;
import android.support.annotation.NonNull;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import java.io.IOException;
import java.io.InputStream;
import timber.log.Timber;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 */
public class MjpegServer extends NanoHTTPD {

    private static final String boundaryWord = "MJPEGBOUNDARY";

    private static final String header = "--" + boundaryWord + "\r\n" +
                                         "Content-Type: image/jpeg\r\n" +
                                         "Content-Length:%d\n\r" +
                                         "X-Timestamp: 0.000000\r\n" +
                                         "\r\n";

    private final Context context;
    private final ImageStream stream;

    public MjpegServer(@NonNull Context context, int port) throws IOException {
        super(port);
        this.context = context;
        stream = new ImageStream(context);
        start();

        Timber.d("Running. Point browsers to http://localhost:%d", port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response response = newChunkedResponse(Status.OK, null, stream);
        response.addHeader("multipart/x-mixed-replace;boundary", boundaryWord);
        return response;
    }

    final class ImageStream extends InputStream {

        private final Context     context;
        private       InputStream currentStream;
        private int counter = 0;

        private final byte[] rawHeader = header.getBytes();
        int headerIndex = 0;

        public ImageStream(@NonNull Context context) {
            this.context = context;
            currentStream = context.getResources().openRawResource(R.raw.a);
            changer.start();
        }

        private final Thread changer = new Thread(new Runnable() {
            @Override public void run() {
                Timber.d("Changer thread started.");
                while (true) {
                    for (int i = 0; i < 2; ++i) {
                        try {
                            Thread.sleep(2000);
                            synchronized (currentStream) {
                                ++counter;
                                Timber.d("Changing stream to %d", counter);
                                switch (counter) {
                                    case 1:
                                        currentStream = context.getResources().openRawResource(R.raw.b);
                                        headerIndex = 0;
                                        break;
                                    case 2:
                                        currentStream = context.getResources().openRawResource(R.raw.c);
                                        headerIndex = 0;
                                        break;
                                    default:
                                        currentStream = context.getResources().openRawResource(R.raw.a);
                                        counter = 0;
                                        headerIndex = 0;
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        @Override
        public int read() throws IOException {
            /*if (headerIndex < rawHeader.length) {
                return rawHeader[headerIndex++];
            }*/
            synchronized (currentStream) {
                return currentStream.read();
            }
        }
    }
}
