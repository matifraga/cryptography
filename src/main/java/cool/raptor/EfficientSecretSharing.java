package cool.raptor;

import io.nayuki.bmpio.BlackAndWhiteBMPImage;
import io.nayuki.bmpio.PalletedBMPImage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EfficientSecretSharing implements Shamir<PalletedBMPImage> {

    private int randomSeed;
    private int k;
    private int n;
    private int m = 257;

    public EfficientSecretSharing(int randomSeed, int k, int n, int m) {
        this.randomSeed = randomSeed;
        this.k = k;
        this.n = n;
        this.m = m;
    }

    @Override
    public Map<Integer, PalletedBMPImage> split(PalletedBMPImage secret) {
        System.out.println();
        System.out.println("GENERANDO SOMBRAS");
        int width = secret.getWidth();
        int height = secret.getHeight();
        System.out.println("Tamaño de la imágen secreta: " + width + "x" + height);
        int shadeHeight = height;
        int shadeWidth = (int) Math.ceil((1.0 * width) / (1.0 * k));
        System.out.println("Tamaño de las sombras: " + shadeWidth + "x" + shadeHeight);
        int[] permutationTable = new int[width * height];
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
                if (x >= width) {
                    coefs[r] = 0;
                }
                else {
                    coefs[r] = Byte.toUnsignedInt(secret.getPixel(x, y)) ^ permutationTable[x * height + y];
                    y++;
                    if (y >= height) {
                        y = 0;
                        x++;
                    }
                }
            }
            Polynomial poly = new Polynomial(coefs);

            i = 1;
            do {
                points[i - 1] = Math.floorMod(poly.evaluate(i), m);
                if (points[i - 1] == 256) {
                    coefs[0] = coefs[0] - 1;
                    i = 1;
                } else {
                    i++;
                }
            } while (i <= n);

            for (Integer key : shades.keySet()) {
                shades.get(key).setPixel(shadeX, shadeY, (byte) points[key - 1]);
            }
            shadeY++;
            if (shadeY == shadeHeight) {
                shadeY = 0;
                shadeX++;
            }
        } while (x * y < width * height && shadeX < shadeWidth);

        return shades;
    }

    @Override
    public PalletedBMPImage join(Map<Integer, PalletedBMPImage> shades, int width, int height) {
        System.out.println();
        System.out.println("RECUPERANDO EL SECRETO");

        int shadeHeight = 0;
        int shadeWidth = 0;

        for (Integer key : shades.keySet()) {
            shadeHeight = shades.get(key).getHeight();
            shadeWidth = shades.get(key).getWidth();
            break;
        }

        System.out.println("Tamaño de las sombras: " + shadeWidth + "x" + shadeHeight);

        System.out.println("Tamaño de la imágen secreta: " + width + "x" + height);

        PalletedBMPImage secret = new BlackAndWhiteBMPImage(width, height);

        int[] permutationTable = new int[width * height];
        Random random = new Random();
        random.setSeed(randomSeed);

        for (int r = 0; r < width * height; r++) {
            permutationTable[r] = random.nextInt(256);
        }
        Map<Integer, Integer> points = new HashMap<>();

        int secretX = 0;
        int secretY = 0;
        int[] secretBytes;

        for (int i = 0; i < shadeWidth; i++) {
            for (int j = 0; j < shadeHeight; j++) {
                for (Map.Entry<Integer, PalletedBMPImage> entry : shades.entrySet()) {
                    points.put(entry.getKey(), Byte.toUnsignedInt(entry.getValue().getPixel(i, j)));
                }

                secretBytes = getSecret(points);
                for (int l = 0; l < k; l++) {
                    secret.setPixel(secretX, secretY, (byte) (secretBytes[l] ^ permutationTable[secretX * height + secretY]));
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

    private int[] getSecret(Map<Integer, Integer> points) {
        return Polynomial.solve(points, m).getCoefs();
    }
}

