package io.nayuki.bmpio;

public class BlackAndWhiteBMPImage extends PalletedBMPImage {

    public BlackAndWhiteBMPImage(int width, int height) {
        super(width, height, getBlackAndWhitePallete());
        setBitsPerPixel(8);
    }

    private static int[] getBlackAndWhitePallete() {
        int[] bwPallete = new int[256];
        for (int i = 0; i < bwPallete.length; i++) {
            bwPallete[i] = i * 0x010101;
        }
        return bwPallete;
    }

}
