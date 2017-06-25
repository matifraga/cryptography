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


public abstract class BMPImage {

	protected int width;
    protected int height;

    protected int horizontalResolution = 3780;
    protected int verticalResolution = 3780;

    protected int bitsPerPixel;
    protected int colorsUsed;

    protected int[] pixels;

    public BMPImage(int width, int height) {
	    this.width = width;
	    this.height = height;
        if (width > Integer.MAX_VALUE / height)
            throw new IllegalArgumentException("Image dimensions too large");
        pixels = new int[width * height];
    }

    public int getPixel(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeigth())
            throw new IndexOutOfBoundsException();
        return pixels[y * getWidth() + x];
    }

    public void setPixel(int x, int y, int content) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeigth())
            throw new IndexOutOfBoundsException();
        pixels[y * getWidth() + x] = content;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public void setBitsPerPixel(int bitsPerPixel) {
        this.bitsPerPixel = bitsPerPixel;
    }

    public int getColorsUsed() {
        return colorsUsed;
    }

    public void setColorsUsed(int colorsUsed) {
        this.colorsUsed = colorsUsed;
    }

    public int getHorizontalResolution() {
        return horizontalResolution;
    }

    public void setHorizontalResolution(int horizontalResolution) {
        this.horizontalResolution = horizontalResolution;
    }

    public int getVerticalResolution() {
        return verticalResolution;
    }

    public void setVerticalResolution(int verticalResolution) {
        this.verticalResolution = verticalResolution;
    }

    public int getWidth() {
	    return width;
    }

    public int getHeigth() {
	    return height;
    }
}
