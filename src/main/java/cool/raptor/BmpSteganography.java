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
            byteToHide = Byte.toUnsignedInt(secret.getPixel(i));
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

            shadeByte = Byte.toUnsignedInt(shade.getPixel(i));
            bitToHide = bitsToHide[i];
            if (bitToHide == 1) {
                shade.setPixel(i, (byte) ((shadeByte | bitToHide) & 0xFF));
            } else {
                shade.setPixel(i, (byte) (shadeByte & 0xFE));
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

            hiddenBits[i] = Byte.toUnsignedInt(shade.getPixel(i)) & bitGetter;
        }

        for (int i = 0; i < width * height; i++) {
            hiddenPixel = 0;
            for (int k = 7; k >= 0; k--) {
                hiddenPixel = (hiddenPixel << 1 | hiddenBits[i * 8 + k]);
            }
            secret.setPixel(i, (byte) (hiddenPixel & 0xFF));
        }

        return secret;
    }
}
