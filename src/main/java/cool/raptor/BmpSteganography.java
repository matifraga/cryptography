package cool.raptor;


import io.nayuki.bmpio.*;

public class BmpSteganography implements Steganography<PalletedBMPImage> {
    @Override
    public PalletedBMPImage hide(PalletedBMPImage secret, PalletedBMPImage shade) {
        System.out.println();
        System.out.println("OCULTANDO IMÁGEN");

        int width = secret.getWidth();
        int height = secret.getHeight();

        System.out.println("Tamaño de la imágen secreta: " + width + "x" + height);

        int shadeWidth = shade.getWidth();
        int shadeHeight = shade.getHeight();

        System.out.println("Tamaño de la portadora: " + shadeWidth + "x" + shadeHeight);

        int bitGetter;
        int bitToHide;
        int shadeByte;
        int byteToHide;
        byte[] bitsToHide = new byte[height * width * 8];

        for(int i = 0; i < width * height; i++) {
            int y = Math.floorDiv(i, width);
            int x = i - (width * y);
            byteToHide = Byte.toUnsignedInt(secret.getPixel(x, y));
            bitGetter = 0x1;
            for (int k = 0; k < 8; k++) {
                bitToHide = byteToHide & bitGetter;
                bitGetter <<= 1;
                bitToHide >>= k;

                bitsToHide[i * 8 + k] = (byte) (bitToHide & 0xFF);
            }
        }

        for (int i = 0; i < shadeWidth * shadeHeight; i++) {
            if (i >= bitsToHide.length) {
                return shade;
            }
            int y = Math.floorDiv(i, shadeWidth);
            int x = i - (shadeWidth * y);

            shadeByte = Byte.toUnsignedInt(shade.getPixel(x, y));
            try {
                bitToHide = bitsToHide[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(i);
                System.out.println("(" + x + ", " + y + ")");
                throw new RuntimeException();
            }
            if (bitToHide == 1) {
                shade.setPixel(x, y, (byte) ((shadeByte | bitToHide) & 0xFF));
            } else {
                shade.setPixel(x, y, (byte) (shadeByte & 0xFE));
            }
        }

        return shade;
    }

    @Override
    public PalletedBMPImage recover(PalletedBMPImage shade, int width, int height) {
        System.out.println();
        System.out.println("RECUPERANDO IMÁGEN OCULTA");
        System.out.println("Tamaño de la imágen secreta: " + width + "x" + height);

        int shadeWidth = shade.getWidth();
        int shadeHeight = shade.getHeight();

        System.out.println("Tamaño de la portadora: " + shadeWidth + "x" + shadeHeight);

        PalletedBMPImage secret = new BlackAndWhiteBMPImage(width, height);

        int bitGetter = 0x1;
        int[] hiddenBits = new int[width * height * 8];
        int hiddenPixel;

        for (int i = 0; i < shadeWidth * shadeHeight; i++) {
            if (i >= hiddenBits.length) {
                break;
            }

            int y = Math.floorDiv(i, shadeWidth);
            int x = i - (shadeWidth * y);
            hiddenBits[i] = Byte.toUnsignedInt(shade.getPixel(x, y)) & bitGetter;
        }

        for (int i = 0; i < width * height; i++) {
            hiddenPixel = 0;
            for (int k = 7; k >= 0; k--) {
                hiddenPixel = (hiddenPixel << 1 | hiddenBits[i * 8 + k]);
            }
            int y = Math.floorDiv(i, width);
            int x = i - (width * y);
            secret.setPixel(x, y, (byte) (hiddenPixel & 0xFF));
        }

        return secret;
    }
}
