package cool.raptor;

import io.nayuki.bmpio.BMPReader;
import io.nayuki.bmpio.BMPWriter;
import io.nayuki.bmpio.BlackAndWhiteBMPImage;
import io.nayuki.bmpio.PalletedBMPImage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class BmpShamir257 implements Shamir<PalletedBMPImage> {

    private int n;
    private int k;

    public BmpShamir257(int k, int n) {
        if (k > n)
            throw new IllegalArgumentException();

        this.k = k;
        this.n = n;
    }

    @Override
    public Map<Integer, PalletedBMPImage> split(PalletedBMPImage secretImage) {

        int width = secretImage.getWidth();
        int height = secretImage.getHeight();

        Map<Integer, PalletedBMPImage> shades = new HashMap<>();
        for (int s = 1; s <= n; s++) {
            shades.put(s, new BlackAndWhiteBMPImage(width, height));
        }

        Random rand = new Random();
        List<Integer> xValues = shades.keySet().stream().collect(Collectors.toList());
        System.out.println(xValues);
        int[] coefs = new int[k];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //System.out.println("x: " + i + "y: " + j);
                int secretByte = secretImage.getPixel(i, j);
                //secretByte >>= 8;
                coefs[0] = secretByte;
                boolean overflow;
                do {
                    overflow = false;
                    for (int p = 1; p < k; p++) {
                        coefs[p] = rand.nextInt(256) + 1;
                    }
                    Polynomial poly = new Polynomial(coefs);
                    for (int x : xValues) {
                        int shadeByte = Math.floorMod(poly.evaluate(x), 257);
                        if (shadeByte == 256) {
                            overflow = true;
                            //System.out.println("Overflow");
                            break;
                        }
                        //System.out.println(shadeByte);
                        shades.get(x).setPixel(i, j, (byte)shadeByte);
                        if(i==1&& j==1) {
                            System.out.println("X: " + x + " shadeByte: " + shadeByte);
                        }
                    }
                } while (overflow);

                if(i==1 && j==1) {
                    System.out.println("secreto: " + secretByte);
                }
            }
        }
        return shades;
    }

    @Override
    public PalletedBMPImage join(Map<Integer, PalletedBMPImage> shades) {
        int width = shades.entrySet().iterator().next().getValue().getWidth();
        int height = shades.entrySet().iterator().next().getValue().getHeight();
        PalletedBMPImage secret = new BlackAndWhiteBMPImage(width, height);

        System.out.println("width: " + width + " height: " + height);
        List<Integer> xValues = shades.keySet().stream().collect(Collectors.toList());
        System.out.println(xValues);
        Map<Integer, Integer> points = new HashMap<>();
        int secretByte;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int s: shades.keySet()) {
                    points.put(s, (int) shades.get(s).getPixel(i,j));
                }
                if(i==1 && j==1) {
                    points.entrySet()
                        .forEach(entry -> System.out.println("X: " + entry.getKey() + " shadeByte: " + entry.getValue()));
                }
                secretByte = getSecret(points);
                if(i==1 && j==1) {
                    System.out.println("secreto: " + secretByte);
                }
                secret.setPixel(i, j, (byte)secretByte);
            }
        }
        return secret;
    }

    private int getSecret(Map<Integer, Integer> points) {
        int x;
        int div;
        int accum;
        int res = 0;
        for (int s: points.keySet()) {
            accum = points.get(s);
            x = s;
            div = 1;
            for (int p: points.keySet()) {
                if (s != p) {
                    accum *= -p;
                    div *= (x - p);
                }
            }
            accum = Math.floorMod(accum, 257);
            div = Math.floorMod(div, 257);
            res += Math.floorMod(accum * getInv(div), 257);
        }
        return Math.floorMod(res, 257);
    }

    private static int getInv(int a) {
        int m = 257;
        int m0 = m, t, q;
        int x0 = 0, x1 = 1;

        if (m == 1) {
            return 0;
        }

        while (a > 1) {
            // q is quotient
            q = a / m;
            t = m;

            // m is remainder now, process same as
            // Euclid's algo
            m = a % m;
            a = t;
            t = x0;
            x0 = x1 - q * x0;
            x1 = t;
        }

        // Make x1 positive
        if (x1 < 0) {
            x1 += m0;
        }

        return x1;
    }

    public static void main(String[] args) throws IOException{
        PalletedBMPImage secret = BMPReader.readPalletedBMP(new File("./images/Alfred.bmp"));
        BmpShamir257 shamir = new BmpShamir257(2,4);
        Map<Integer, PalletedBMPImage> shadows = shamir.split(secret);
        System.out.println(shadows.keySet());
        File f = new File("./images/Alfred2.bmp");
        System.out.println(shadows);
        PalletedBMPImage secret2 = shamir.join(shadows);
        if(!f.exists() && !f.isDirectory())
        {
            f.createNewFile();
        }
        BMPWriter.write(f, secret2);

        /*PalletedBMPImage secret3 = new BlackAndWhiteBMPImage(secret.getWidth(), secret.getHeight());
        for (int i = 0; i < secret.getWidth(); i++) {
            for (int j = 0; j < secret.getHeight(); j++) {
                secret3.setPixel(i, j, secret.getPixel(i, j));
            }
        }
        File f = new File("./images/Alfred3.bmp");
        BMPWriter.write(f, secret3);*/
    }
}
