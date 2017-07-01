package cool.raptor;

import io.nayuki.bmpio.BMPReader;
import io.nayuki.bmpio.BMPWriter;
import io.nayuki.bmpio.PalletedBMPImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recovery implements Algorithm {

    private final Integer M = 257;

    private Integer n;
    private Integer k;
    private File secretPath;
    private List<PalletedBMPImage> images = new ArrayList<>();

    public Recovery(final File secret, final Integer n, final Integer k, final List<File> images) {
        this.n = n;
        this.k = k;
        this.secretPath = secret;
        try {
            for (File file : images) {
                this.images.add(BMPReader.readPalletedBMP(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean validate() {
        if (images.size() < k) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean execute() {
        Steganography<PalletedBMPImage> steganography = new BmpSteganography();
        Map<Integer, PalletedBMPImage> shadows = new HashMap<>();
        Integer width = 0;
        Integer height = 0;
        for (PalletedBMPImage image : images) {
            height = (k == 8) ? image.getHeight() : image.getSecretHeight();
            width = (k == 8) ? image.getWidth() : image.getSecretWidth();
            int shadowWidth = (k == 8) ? width : (int) Math.ceil((1.0*width)/(1.0*k));
            shadows.put(image.getOrder(), steganography.recover(image, shadowWidth, height));
        }
        Shamir<PalletedBMPImage> shamir = new EfficientSecretSharing(images.get(0).getSeed(), k, n, M);
        PalletedBMPImage secret = shamir.join(shadows, width, height);
        try {
            BMPWriter.write(secretPath, secret);
        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
