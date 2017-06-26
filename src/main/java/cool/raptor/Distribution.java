package cool.raptor;

import io.nayuki.bmpio.BMPReader;
import io.nayuki.bmpio.BMPWriter;
import io.nayuki.bmpio.PalletedBMPImage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Distribution implements Algorithm {

    private final Integer M = 257;

    private Integer n;
    private Integer k;
    private PalletedBMPImage secret;
    private List<PalletedBMPImage> images = new ArrayList<>();

    public Distribution(final File secret, final Integer n, final Integer k, final List<File> images) {
        try {
            this.secret = BMPReader.readPalletedBMP(secret);
            for (File file : images) {
                this.images.add(BMPReader.readPalletedBMP(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.n = n;
        this.k = k;
    }

    @Override
    public Boolean validate() {
        if (images.size() < n) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean execute() {
        Integer randomSeed = 2;
        Shamir<PalletedBMPImage> shamir = new EfficientSecretSharing(randomSeed, k, n, M);
        if (secret.getHeight() % k != 0) {
            System.out.println();
            System.out.println("Número de sombras k: " + k + " inválido");
            return Boolean.FALSE;
        }
        Map<Integer, PalletedBMPImage> shadows = shamir.split(secret);
        Steganography<PalletedBMPImage> steganography = new BmpSteganography();
        List<Map.Entry<Integer, PalletedBMPImage>> shades = new ArrayList<>(shadows.entrySet());
        for (int i = 0; i < shades.size(); i++) {
            Map.Entry<Integer, PalletedBMPImage> shade = shades.get(i);
            PalletedBMPImage host = images.get(i);
            host = steganography.hide(shade.getValue(), host);
            host.setSecretWidth(shade.getValue().getWidth());
            host.setSecretHeight(shade.getValue().getHeight());
            host.setOrder(shade.getKey());
            host.setSeed(randomSeed);
            File f = new File("./dist/host" + shade.getKey() + ".bmp");
            try {
                BMPWriter.write(f, host);
            } catch (IOException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
