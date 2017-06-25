package cool.raptor;

import java.io.File;
import java.io.IOException;

public interface Steganography<K> {
    K hide(K secret, K shade);
    K recover(K shade, int width, int height);
}
