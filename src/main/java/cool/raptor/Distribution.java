package cool.raptor;

import io.nayuki.bmpio.BmpImage;
import io.nayuki.bmpio.BmpReader;
import io.nayuki.bmpio.BmpWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Distribution implements Algorithm {

    private Integer n;
    private Integer k;
    private BmpImage secret;

	public Distribution(final File secret, final Integer n, final Integer k, final List<File> images) {
        try {
            this.secret = BmpReader.read(secret);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.n = n;
        this.k = k;
	}

	@Override
	public Boolean validate() {
		return null;
	}

	@Override
	public Boolean execute() {

        Random rnd = new Random();
        rnd.setSeed(10);

        for (int i = 0; i < 10000; i++) {
            int x = rnd.nextInt(500);
            int y = rnd.nextInt(500);
            int color = 0xFF0000;
            secret.image.setRgb888Pixel(x, y, color);
        }

        try {
            BmpWriter.write(new File("./images/tomi.bmp"), secret);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("%x", secret.image.getRgb888Pixel(250, 250));

        return null;
	}
}
