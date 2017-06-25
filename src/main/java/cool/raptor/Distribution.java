package cool.raptor;

import io.nayuki.bmpio.BMPImage;
import io.nayuki.bmpio.BMPReader;
import io.nayuki.bmpio.BMPWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Distribution implements Algorithm {

    private Integer n;
    private Integer k;
    private BMPImage secret;

	public Distribution(final File secret, final Integer n, final Integer k, final List<File> images) {
        try {
            this.secret = BMPReader.read(secret);
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

        return null;
	}
}
