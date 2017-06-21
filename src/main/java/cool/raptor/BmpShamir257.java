package cool.raptor;

import io.nayuki.bmpio.BmpImage;
import io.nayuki.bmpio.BmpWriter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BmpShamir257 implements Shamir<BmpImage> {

    private int n;
    private int k;

    public BmpShamir257(int k, int n) {
        if (k > n)
            throw new IllegalArgumentException();

        this.k=k;
        this.n=n;
    }

    @Override
    public Map<Integer, BmpImage> split(BmpImage secretImage) {

        int width = secretImage.horizontalResolution;
        int height = secretImage.verticalResolution;

        Map<Integer, BmpImage> shades = new HashMap<>();
        for (int s = 1; s < k; s++) {
            shades.put(s, new BmpImage());
        }

        Random rand = new Random();
        List<Integer> xValues = shades.keySet().stream().collect(Collectors.toList());
        int[] coefs = new int[k];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int secretByte = secretImage.image.getRgb888Pixel(i, j);
                coefs[0] = secretByte;
                for (int p = 1; p < k; p++) {
                    coefs[p] = rand.nextInt(256) + 1;
                }
                Polynomial poly = new Polynomial(coefs);
                for (int x: xValues) {
                    int shadeByte = poly.evaluate(x)%257;
                    if(shadeByte == 256) {
                        //PANIC
                    }
                    shades.get(x).image.setRgb888Pixel(i,j,shadeByte);
                }
            }
        }
        return shades;
    }

    @Override
    public BmpImage join(Map<Integer, BmpImage> shades) {
        BmpImage secret = new BmpImage();
        int width = shades.get(1).horizontalResolution;
        int height = shades.get(1).verticalResolution;

        List<Integer> xValues = shades.keySet().stream().collect(Collectors.toList());
        int[] coefs = new int[k];
        Map<Integer, Integer> points = new HashMap<>();
        int secretByte;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int s: shades.keySet()) {
                    points.put(s,shades.get(s).image.getRgb888Pixel(i,j));
                }
                secretByte = getSecret(points);
                secret.image.setRgb888Pixel(i,j,secretByte);
            }
        }
        return secret;
    }

    private int getSecret(Map<Integer, Integer> points) {
        int result = 0;
        int x;
        int div;
        int accum = 1;
        for (int s: points.keySet()) {
            accum = points.get(s);
            x = s;
            div = 1;
            for (int p: points.keySet()) {
                if (s != p) {
                    accum *= -s;
                    div *= (x-points.get(s));
                }
            }
        }
        return 0;
    }
}
