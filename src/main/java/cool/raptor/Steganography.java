package cool.raptor;

import java.io.File;
import java.io.IOException;

public interface Steganography {
    File hide(File secret) throws IOException;
    File recover(File shade) throws IOException;
}
