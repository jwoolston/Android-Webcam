package com.jwoolston.android.uvc.interfaces.streaming;

/**
 * @author Jared Woolston (Jared.Woolston@gmail.com)
 * @see UVC 1.5 Class Specification Table 3-19
 */
public class VideoColorMatchingDescriptor {

    private static final String TAG = "VideoColorMatchingDescriptor";

    private static final int LENGTH = 6;

    private static final int bColorPrimaries = 3;
    private static final int bTransferCharacteristics = 4;
    private static final int bMatrixCoefficients = 5;

    private final ColorPrimaries colorPrimaries;
    private final TransferCharacteristics transferCharacteristics;
    private final MatrixCoefficients matrixCoefficients;

    public VideoColorMatchingDescriptor(byte[] descriptor) throws IllegalArgumentException {
        if (descriptor.length < LENGTH) {
            throw new IllegalArgumentException("Provided descriptor is not long enough to be a Video Color Matching " +
                "Descriptor.");
        }
        colorPrimaries = ColorPrimaries.fromDescriptor(descriptor[bColorPrimaries] & 0xFF);
        transferCharacteristics = TransferCharacteristics.fromDescriptor(descriptor[bTransferCharacteristics] & 0xFF);
        matrixCoefficients = MatrixCoefficients.fromDescriptor(descriptor[bMatrixCoefficients] & 0xFF);
    }

    public ColorPrimaries getColorPrimaries() {
        return colorPrimaries;
    }

    public TransferCharacteristics getTransferCharacteristics() {
        return transferCharacteristics;
    }

    public MatrixCoefficients getMatrixCoefficients() {
        return matrixCoefficients;
    }

    @Override
    public String toString() {
        return "VideoColorMatchingDescriptor{" +
            "colorPrimaries=" + colorPrimaries +
            ", transferCharacteristics=" + transferCharacteristics +
            ", matrixCoefficients=" + matrixCoefficients +
            '}';
    }

    public static enum ColorPrimaries {
        UNSPECIFIED,
        BT_709,
        sRGB,
        BT_470_2M,
        BT_470_2BG,
        SMPTE_170M,
        SMPTE_240M;

        public static ColorPrimaries fromDescriptor(int id) {
            if (id >= 8) return sRGB;
            for (ColorPrimaries characteristics : ColorPrimaries.values()) {
                if (characteristics.ordinal() == id) {
                    return characteristics;
                }
            }
            return sRGB;
        }
    }

    public static enum TransferCharacteristics {
        UNSPECIFIED, BT_709, BT_470_2M, BT_470_2BG, SMPTE_170M, SMPTE_240M, LINEAR, sRGB;

        public static TransferCharacteristics fromDescriptor(int id) {
            if (id >= 8) return BT_709;
            for (TransferCharacteristics characteristics : TransferCharacteristics.values()) {
                if (characteristics.ordinal() == id) {
                    return characteristics;
                }
            }
            return BT_709;
        }
    }

    public static enum MatrixCoefficients {
        UNSPECIFIED,
        BT_709,
        FCC,
        BT_470_2BG,
        SMPTE_170M,
        SMPTE_240M;

        public static MatrixCoefficients fromDescriptor(int id) {
            if (id >= 8) return SMPTE_170M;
            for (MatrixCoefficients characteristics : MatrixCoefficients.values()) {
                if (characteristics.ordinal() == id) {
                    return characteristics;
                }
            }
            return SMPTE_170M;
        }
    }
}
