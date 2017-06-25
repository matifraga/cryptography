/*
 * BMP I/O library (Java)
 *
 * Copyright (c) Project Nayuki
 * https://www.nayuki.io/page/bmp-io-library-java
 *
 * (MIT License)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express or
 *   implied, including but not limited to the warranties of merchantability,
 *   fitness for a particular purpose and noninfringement. In no event shall the
 *   authors or copyright holders be liable for any claim, damages or other
 *   liability, whether in an action of contract, tort or otherwise, arising from,
 *   out of or in connection with the Software or the use or other dealings in the
 *   Software.
 */

package io.nayuki.bmpio;

public class PalletedBMPImage extends BMPImage {

	private int[] palette;
	private byte[] pixels;

	public PalletedBMPImage(int width, int height, int[] palette) {
		super(width, height);
		if (width > Integer.MAX_VALUE / height) {
            throw new IllegalArgumentException("Image dimensions too large");
        }
        this.pixels = new byte[width * height];
		this.palette = palette.clone();
	}

    public int getColor(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            throw new IndexOutOfBoundsException();
        }
        return palette[pixels[y * getWidth() + x] & 0xFF];
    }

    public byte getPixel(int x, int y) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            throw new IndexOutOfBoundsException();
        }
		return pixels[y * getWidth() + x];
	}

    public void setPixel(int x, int y, byte content) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight() || (content & 0xFF) >= palette.length) {
            throw new IndexOutOfBoundsException();
        }
		pixels[y * getWidth() + x] = content;
	}

	public int[] getPalette() {
	    return palette;
    }

}
