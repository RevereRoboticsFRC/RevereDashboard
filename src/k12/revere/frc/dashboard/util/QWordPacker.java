package k12.revere.frc.dashboard.util;

public class QWordPacker {
    
    private static final float[] PACK_L_OCTO_FLOAT_CACHE = new float[8];
    private static final float[] PACK_L_OCTO_UNSIGNED_FLOAT_CACHE = new float[8];

    public static long packLDualInts(int i1, int i2) {
        return (i1 << 8) | i2;
    }
    
    public static int unpackLDualIntsA(long l) {
        return (int)((l >> 8L) & 0xFFFFFFFFL);
    }
    
    public static int unpackLDualIntsB(long l) {
        return (int)(l & 0xFFFFFFFFL);
    }
    
    public static long packLDualFloats(float f1, float f2) {
        return packLDualInts(Float.floatToIntBits(f1), Float.floatToIntBits(f2));
    }
    
    public static float unpackLDualFloatsA(long l) {
        return Float.floatToIntBits(unpackLDualIntsA(l));
    }
    
    public static float unpackLDualFloatsB(long l) {
        return Float.floatToIntBits(unpackLDualIntsB(l));
    }
    
    public static long packLBitfield(boolean[] flags) {
        int numTerms = MathUtil.clamp(0, 64, flags.length);
        long result = 0;
        for(int i = 0; i < numTerms; i++) {
            result = setBitFlag(result, i, flags[i]);
        }
        return result;
    }
    
    public static boolean[] unpackLBitField(long l) {
        boolean[] result = new boolean[64];
        for(int i = 0; i < 64; i++) {
            result[i] = getBitFlag(l, i);
        }
        return result;
    }
    
    public static long setBitFlag(long base, int position, boolean flag) {
        if(position < 0 || position > 63) {
            return base;
        }
        long bitmask = ~(1L << position);
        long iflag = (flag ? 1 : 0);
        return (base & bitmask) | (iflag << position);
    }
    
    public static boolean getBitFlag(long flags, int position) {
        if(position < 0 || position > 63) {
            return false;
        }
        long bitmask = 1L << position;
        return (flags & bitmask) > 0;
    }
    
    public static long setByte(long base, int byteNum, int byteVal) {
        if(byteNum < 0 || byteNum > 7) {
            return base;
        }
        int bitoffset = byteNum * 8;
        long bitmask = ~(0xFF << bitoffset);
        long bflag = byteVal & 0xFF;
        return (base & bitmask) | (bflag << bitoffset);
    }
    
    public static int getByte(long base, int byteNum) {
        if(byteNum < 0 || byteNum > 7) {
            return 0;
        }
        int bitoffset = byteNum * 8;
        long bitmask = 0xFF << bitoffset;
        return 0xFF & (int)((base & bitmask) >> bitoffset);
    }
    
    public static long packLOctoFloat255(float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        PACK_L_OCTO_FLOAT_CACHE[0] = f1;
        PACK_L_OCTO_FLOAT_CACHE[1] = f2;
        PACK_L_OCTO_FLOAT_CACHE[2] = f3;
        PACK_L_OCTO_FLOAT_CACHE[3] = f4;
        PACK_L_OCTO_FLOAT_CACHE[4] = f5;
        PACK_L_OCTO_FLOAT_CACHE[5] = f6;
        PACK_L_OCTO_FLOAT_CACHE[6] = f7;
        PACK_L_OCTO_FLOAT_CACHE[7] = f8;
        return packLOctoFloat255(PACK_L_OCTO_FLOAT_CACHE);
    }
    
    public static long packLOctoFloat255(float[] floats) {
        int numFloats = MathUtil.clamp(0, 8, floats.length);
        long result = 0L;
        for(int i = 0; i < numFloats; i++) {
            result = setByte(result, i, normFloatToByte(floats[i]));
        }
        return result;
    }
    
    public static long packLOctoFloat255Unsigned(float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        PACK_L_OCTO_UNSIGNED_FLOAT_CACHE[0] = f1;
        PACK_L_OCTO_UNSIGNED_FLOAT_CACHE[1] = f2;
        PACK_L_OCTO_UNSIGNED_FLOAT_CACHE[2] = f3;
        PACK_L_OCTO_UNSIGNED_FLOAT_CACHE[3] = f4;
        PACK_L_OCTO_UNSIGNED_FLOAT_CACHE[4] = f5;
        PACK_L_OCTO_UNSIGNED_FLOAT_CACHE[5] = f6;
        PACK_L_OCTO_UNSIGNED_FLOAT_CACHE[6] = f7;
        PACK_L_OCTO_UNSIGNED_FLOAT_CACHE[7] = f8;
        return packLOctoFloat255(PACK_L_OCTO_UNSIGNED_FLOAT_CACHE);
    }
    
    public static long packLOctoFloat255Unsigned(float[] floats) {
        int numFloats = MathUtil.clamp(0, 8, floats.length);
        long result = 0L;
        for(int i = 0; i < numFloats; i++) {
            result = setByte(result, i, normUnsignedFloatToByte(floats[i]));
        }
        return result;
    }
    
    public static float unpackLOctoFloat255(long l, int floatNum) {
        if(floatNum < 0 || floatNum > 7) {
            return 0F;
        }
        return byteToNormFloat(getByte(l, floatNum));
    }
    
    public static float unpackLOctoFloat255Unsigned(long l, int floatNum) {
        if(floatNum < 0 || floatNum > 7) {
            return 0F;
        }
        return byteToNormUnsignedFloat(getByte(l, floatNum));
    }
    
    public static int normFloatToByte(float f) {
        return 0xFF & (int)((MathUtil.clamp(-1F, 1F, f) + 1F)/2F * 255F);
    }
    
    public static int normUnsignedFloatToByte(float f) {
        return 0xFF & (int)(MathUtil.clamp(0F, 1F, f) * 255F);
    }
    
    public static float byteToNormFloat(int i) {
        return ((i & 0xFF) - 127)/128F;
    }
    
    public static float byteToNormUnsignedFloat(int i) {
        return ((i & 0xFF)/255F);
    }
    
    public static double l2d(long l) {
        return Double.longBitsToDouble(l);
    }
    
    public static long d2l(double d) {
        return Double.doubleToLongBits(d);
    }
}
