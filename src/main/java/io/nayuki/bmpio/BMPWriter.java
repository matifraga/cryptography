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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class BMPWriter {

    public static void write(File file, PalletedBMPImage image) throws IOException {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        } else if (!file.exists()){
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        writePalletedImage(outputStream, image);
        outputStream.close();
    }

    private static void writePalletedImage(OutputStream out, PalletedBMPImage image) throws IOException {
        LittleEndianDataOutput out1 = new LittleEndianDataOutput(out);

        int width = image.getWidth();
        int height = image.getHeight();
        int rowSize = (width * (image.getBitsPerPixel() / 8) + 3) / 4 * 4;  // 3 bytes per pixel in RGB888, round up to multiple of 4
        int imageSize = rowSize * height;

        // BITMAPFILEHEADER
        out1.writeBytes(new byte[]{'B', 'M'});  // FileType
        out1.writeInt32(14 + 40 + imageSize);   // FileSize
        byte[] seed = image.getSeed();
        out1.writeByte(seed[0]);                     // Reserved1
        out1.writeByte(seed[1]);
        out1.writeInt16(image.getOrder());                     // Reserved2
        out1.writeInt32((int) (14 + 40 + Math.pow(2, image.getBitsPerPixel()) * 4));               // BitmapOffset

        // BITMAPINFOHEADER
        out1.writeInt32(40);                        // Size
        out1.writeInt32(image.getWidth());                     // Width
        out1.writeInt32(image.getHeight());                    // Height
        out1.writeInt16(1);                         // Planes
        out1.writeInt16(image.getBitsPerPixel());                        // BitsPerPixel
        out1.writeInt32(0);                         // Compression
        out1.writeInt32(imageSize);                 // SizeOfBitmap
        out1.writeInt32(image.getHorizontalResolution());  // HorzResolution
        out1.writeInt32(image.getVerticalResolution());    // VertResolution
        out1.writeInt32(image.getColorsUsed());                         // ColorsUsed
        out1.writeInt16(image.getSecretWidth());                         // ColorsImportant
        out1.writeInt16(image.getSecretHeight());

        int[] pallete = image.getPalette();

        for (int i = 0; i < pallete.length; i++) {
            out1.writeInt32(pallete[i]);
        }

        // Image data
        byte[] row = new byte[rowSize];
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                byte index = image.getPixel(x, y);
                out1.writeByte(index);
            }
        }

        out1.flush();
    }

    // Not instantiable
    private BMPWriter() {
    }

}
