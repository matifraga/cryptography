package cool.raptor;

import io.nayuki.bmpio.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EfficientSecretSharing implements Shamir<PalletedBMPImage>{

    private int randomSeed;
    private int k;
    private int n;
    private int m=257;

    public EfficientSecretSharing(int randomSeed, int k, int n, int m) {
        this.randomSeed = randomSeed;
        this.k = k;
        this.n = n;
        this.m = m;
    }

    @Override
    public Map<Integer, PalletedBMPImage> split(PalletedBMPImage secret) {

        int width = secret.getWidth();
        int height = secret.getHeight();
        int shadeHeight = (int) Math.ceil(height * 1/k);
        int shadeWidth = width;
        int[] permutationTable = new int[width*height];

        System.out.println("WIDTH: " + width + " HEIGHT: " + height);
        System.out.println("SHADE_WIDTH: " + shadeWidth + " SHADE_HEIGHT: " + shadeHeight);
        Random random = new Random();
        random.setSeed(randomSeed);
        for (int r = 0; r < width * height; r++) {
            permutationTable[r] = random.nextInt(256);
        }

        Map<Integer, PalletedBMPImage> shades = new HashMap<>();
        for (int s = 1; s <= n; s++) {
            shades.put(s, new BlackAndWhiteBMPImage(shadeWidth, shadeHeight));
        }

        int x = 0;
        int y = 0;
        int shadeX = 0;
        int shadeY = 0;
        int[] coefs = new int[k];
        int[] points = new int[n];
        int i;

        do {
            for (int r = 0; r < k; r++) {
                if(x == 10 && y == 25) {
                    System.out.println(secret.getPixel(x,y));
                }
                coefs[r] = Byte.toUnsignedInt(secret.getPixel(x, y)) ^ permutationTable[x*height + y];
                //System.out.println(coefs[r]);
                y++;
                if (y >= height) {
                    y = 0;
                    x++;
                }
            }
            Polynomial poly = new Polynomial(coefs);

            i = 1;
            do {
                points[i - 1] = Math.floorMod(poly.evaluate(i), m);
                //System.out.println(points[i-1]);
                if (points[i - 1] == 256) {
                    coefs[0] = coefs[0] - 1;
                    i = 1;
                }
                else {
                    i++;
                }
            } while (i <= n);

            for (Integer key: shades.keySet()) {
                //System.out.println((byte) (points[key - 1] & 0xFF));
                shades.get(key).setPixel(shadeX, shadeY, (byte) points[key - 1] );
            }
            shadeY++;
            if (shadeY == shadeHeight) {
                shadeY = 0;
                shadeX++;
            }
        }while(x < width);


        return shades;
    }

    @Override
    public PalletedBMPImage join(Map<Integer, PalletedBMPImage> shades) {
        int shadeHeight = 0;
        int shadeWidth = 0;

        //TODO:CHECK HEIGHT AND WIDTH
        for (Integer key: shades.keySet()) {
            shadeHeight = shades.get(key).getHeight();
            shadeWidth = shades.get(key).getWidth();
        }

        int height = (int) Math.ceil(shadeHeight * k);
        int width = shadeWidth;

        System.out.println("WIDTH: " + width + " HEIGHT: " + height);
        System.out.println("SHADE_WIDTH: " + shadeWidth + " SHADE_HEIGHT: " + shadeHeight);

        PalletedBMPImage secret = new BlackAndWhiteBMPImage(width,height);

        int[] permutationTable = new int[width*height];
        System.out.println(width*height);
        Random random = new Random();
        random.setSeed(randomSeed);

        for (int r = 0; r < width * height; r++) {
            permutationTable[r] = random.nextInt(256);
        }
        Map<Integer, Integer> points = new HashMap<>();

        int secretX = 0;
        int secretY = 0;
        int[] secretBytes = new int[k];

        for (int i = 0; i < shadeWidth; i++) {
            for (int j = 0; j < shadeHeight; j++) {
                for (Map.Entry<Integer, PalletedBMPImage> entry: shades.entrySet()) {
                    points.put(entry.getKey(), Byte.toUnsignedInt(entry.getValue().getPixel(i,j)));
                }

                for (int l = 0; l < k; l++) {
                    secretBytes[l] = getSecret(points/*,l*/);
                    secret.setPixel(secretX, secretY, (byte)(secretBytes[l] ^ permutationTable[secretX * height + secretY]));
                    if(secretX == 10 && secretY == 25) {
                        System.out.println(secret.getPixel(secretX,secretY));
                    }
                    secretY++;
                    if (secretY >= height) {
                        secretY = 0;
                        secretX++;
                    }
                    if (secretX >= width) {
                        return secret;
                    }
                }

            }
        }
        return secret;
    }

    private int getSecret(Map<Integer, Integer> points) {
        //TODO:MAGIG
        return 0;
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

    public static void main(String[] args) throws IOException {
        PalletedBMPImage secret = BMPReader.readPalletedBMP(new File("./images/AlfredSmall.bmp"));
        Shamir<PalletedBMPImage> shamir = new EfficientSecretSharing(2,2,4,257);
        Map<Integer, PalletedBMPImage> shadows = shamir.split(secret);
        System.out.println(shadows.keySet());
        File f = new File("./images/AlfredSmall3.bmp");
        System.out.println(shadows);
        int i=0;
        for (PalletedBMPImage shadow: shadows.values()) {
            i++;
            File f2 = new File("./images/shadow" + i);
            if(!f2.exists() && !f2.isDirectory())
            {
                f2.createNewFile();
            }
            BMPWriter.write(f2, shadow);
        }
        PalletedBMPImage secret2 = shamir.join(shadows);
        if(!f.exists() && !f.isDirectory())
        {
            f.createNewFile();
        }
        BMPWriter.write(f, secret2);
    }
}

