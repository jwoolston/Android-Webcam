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

    private final COLOR_PRIMARIES          mColorPrimaries;
    private final TRANSFER_CHARACTERISTICS mTransferCharacteristics;
    private final MATRIX_COEFFICIENTS mMatrixCoefficients;

    public VideoColorMatchingDescriptor(byte[] descriptor) throws IllegalArgumentException {
        if (descriptor.length < LENGTH) throw new IllegalArgumentException("Provided descriptor is not long enough to be a Video Color Matching Descriptor.");
        mColorPrimaries = COLOR_PRIMARIES.fromDescriptor(descriptor[bColorPrimaries] & 0xFF);
        mTransferCharacteristics = TRANSFER_CHARACTERISTICS.fromDescriptor(descriptor[bTransferCharacteristics] & 0xFF);
        mMatrixCoefficients = MATRIX_COEFFICIENTS.fromDescriptor(descriptor[bMatrixCoefficients] & 0xFF);
    }

    public COLOR_PRIMARIES getColorPrimaries() {
        return mColorPrimaries;
    }

    public TRANSFER_CHARACTERISTICS getTransferCharacteristics() {
        return mTransferCharacteristics;
    }

    public MATRIX_COEFFICIENTS getMatrixCoefficients() {
        return mMatrixCoefficients;
    }

    @Override
    public String toString() {
        return "VideoColorMatchingDescriptor{" +
                "mColorPrimaries=" + mColorPrimaries +
                ", mTransferCharacteristics=" + mTransferCharacteristics +
                ", mMatrixCoefficients=" + mMatrixCoefficients +
                '}';
    }

    public static enum COLOR_PRIMARIES {
        UNSPECIFIED,
        BT_709,
        sRGB,
        BT_470_2M,
        BT_470_2BG,
        SMPTE_170M,
        SMPTE_240M;

        public static COLOR_PRIMARIES fromDescriptor(int id) {
            if (id >= 8) return sRGB;
            for (COLOR_PRIMARIES characteristics : COLOR_PRIMARIES.values()) {
                if (characteristics.ordinal() == id) {
                    return characteristics;
                }
            }
            return sRGB;
        }
    }
    public static enum TRANSFER_CHARACTERISTICS {
        UNSPECIFIED, BT_709, BT_470_2M, BT_470_2BG, SMPTE_170M, SMPTE_240M, LINEAR, sRGB;

        public static TRANSFER_CHARACTERISTICS fromDescriptor(int id) {
            if (id >= 8) return BT_709;
            for (TRANSFER_CHARACTERISTICS characteristics : TRANSFER_CHARACTERISTICS.values()) {
                if (characteristics.ordinal() == id) {
                    return characteristics;
                }
            }
            return BT_709;
        }
    }

    public static enum MATRIX_COEFFICIENTS {
        UNSPECIFIED,
        BT_709,
        FCC,
        BT_470_2BG,
        SMPTE_170M,
        SMPTE_240M;

        public static MATRIX_COEFFICIENTS fromDescriptor(int id) {
            if (id >= 8) return SMPTE_170M;
            for (MATRIX_COEFFICIENTS characteristics : MATRIX_COEFFICIENTS.values()) {
                if (characteristics.ordinal() == id) {
                    return characteristics;
                }
            }
            return SMPTE_170M;
        }
    }
}
