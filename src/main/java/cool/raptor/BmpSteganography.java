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

        // shadeWidth = width * 8
        // shadeHeight = height * 8
        int shadeWidth = shade.getWidth();
        int shadeHeight = shade.getHeight();

        System.out.println("Tamaño de la portadora: " + shadeWidth + "x" + shadeHeight);

        if (shadeHeight * shadeWidth < height * width * 8) {
            System.out.println("La imágen portadora es muy chica, sólo se puede esconder parte del secreto");
        }

        int bitGetter;
        int bitToHide;
        int shadeByte;
        int byteToHide;
        byte[] bitsToHide = new byte[height * width * 8];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                byteToHide = Byte.toUnsignedInt(secret.getPixel(i, j));
                /*if(i==25 && j ==35) {
                    System.out.println("Byte to hide: " + byteToHide);
                }*/
                bitGetter = 0x1;
                for (int k = 0; k < 8; k++) {
                    bitToHide = byteToHide & bitGetter;
                    bitGetter <<= 1;
                    bitToHide >>= k;
                    //System.out.println(i*height*8 + j*8 + k);
                    bitsToHide[i * height * 8 + j * 8 + k] = (byte) (bitToHide & 0xFF);
                }
            }
        }

        for (int i = 0; i < shadeWidth; i++) {
            for (int j = 0; j < shadeHeight; j++) {
                if (i * shadeWidth + j >= bitsToHide.length) {
                    //System.out.println("DONE");
                    return shade;
                }
                shadeByte = Byte.toUnsignedInt(shade.getPixel(i, j));
                bitToHide = bitsToHide[i * shadeHeight + j];
                if (bitToHide == 1) {
                    shade.setPixel(i, j, (byte) ((shadeByte | bitToHide) & 0xFF));
                } else {
                    shade.setPixel(i, j, (byte) (shadeByte & 0xFE));
                }
            }

        }

        return shade;
    }

    @Override
    public PalletedBMPImage recover(PalletedBMPImage shade, int width, int height) {
        System.out.println();
        System.out.println("RECUPERANDO IMÁGEN OCULTA");
        System.out.println("Tamaño de la imágen secreta: " + width + "x" + height);
        // shadeHeight = height * 8
        int shadeWidth = shade.getWidth();
        int shadeHeight = shade.getHeight();

        System.out.println("Tamaño de la portadora: " + shadeWidth + "x" + shadeHeight);
        if (shadeHeight * shadeWidth < height * width * 8) {
            System.out.println("La imágen portadora es muy chica, sólo se puede recuperar parte del secreto");
        }


        PalletedBMPImage secret = new BlackAndWhiteBMPImage(width, height);

        // shadeWidth = width * 8
        int bitGetter = 0x1;
        int[] hiddenBits = new int[width * height * 8];
        int hiddenPixel;
        int shadePixel;
        int hiddenBit;
        boolean stop = false;

        for (int i = 0; i < shadeWidth && !stop; i++) {
            for (int j = 0; j < shadeHeight; j++) {
                if ((i * shadeHeight + j) >= hiddenBits.length) {
                    //System.out.println("DONE");
                    stop = true;
                    break;
                }
                hiddenBits[i * shadeHeight + j] = Byte.toUnsignedInt(shade.getPixel(i, j)) & bitGetter;
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                hiddenPixel = 0;
                for (int k = 7; k >= 0; k--) {
                    hiddenPixel = (hiddenPixel << 1 | hiddenBits[i * height * 8 + j * 8 + k]);
                }
                /*if (i == 25 && j == 35) {
                    System.out.println("Hidden byte: " + hiddenPixel);
                }*/
                secret.setPixel(i, j, (byte) (hiddenPixel & 0xFF));
            }
        }

        return secret;
    }
}
