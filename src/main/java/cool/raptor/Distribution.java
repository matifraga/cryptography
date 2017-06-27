package cool.raptor;

import io.nayuki.bmpio.BMPReader;
import io.nayuki.bmpio.BMPWriter;
import io.nayuki.bmpio.PalletedBMPImage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
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
//        if (secret.getHeight() % k != 0) {
//            return Boolean.FALSE;
//        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean execute() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomSeed = secureRandom.generateSeed(2);
        Shamir<PalletedBMPImage> shamir = new EfficientSecretSharing(randomSeed, k, n, M);
        Map<Integer, PalletedBMPImage> shadows = shamir.split(secret);
        Steganography<PalletedBMPImage> steganography = new BmpSteganography();
        List<Map.Entry<Integer, PalletedBMPImage>> shades = new ArrayList<>(shadows.entrySet());
        try {
            File dir = new File("./dist/");
            if(dir.exists() && dir.isDirectory()) {
                FileUtils.cleanDirectory(dir);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < shades.size(); i++) {
            Map.Entry<Integer, PalletedBMPImage> shade = shades.get(i);
            PalletedBMPImage host = images.get(i);
            host = steganography.hide(shade.getValue(), host);
            host.setSecretWidth(secret.getWidth());
            host.setSecretHeight(secret.getHeight());
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
