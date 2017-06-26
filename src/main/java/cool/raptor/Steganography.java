package cool.raptor;

public interface Steganography<K> {
    K hide(K secret, K shade);

    K recover(K shade, int width, int height);
}
