package cool.raptor;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {

        Map<String, Object> arguments = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if ((i + 1) < args.length) {
                    if (args[i + 1].startsWith("-")) {
                        arguments.put(args[i].substring(1), Boolean.TRUE);
                    } else {
                        arguments.put(args[i].substring(1), args[++i]);
                    }
                } else {
                    arguments.put(args[i].substring(1), Boolean.TRUE);
                }
            } else {
                throw new IllegalArgumentException("Input error");
            }
        }

        if (!arguments.containsKey("d") && !arguments.containsKey("r")) throw new IllegalArgumentException("Undefined Distribution/Reception");
        if (arguments.containsKey("d") && arguments.containsKey("r")) throw new IllegalArgumentException("Undefined Distribution/Reception");
        if (!arguments.containsKey("secret")) throw new IllegalArgumentException("Undefined secret file name");
        if (!arguments.containsKey("k")) throw new IllegalArgumentException("Undefined number of shadows");

        String mode = arguments.containsKey("r") ? "r" : "d";
        String secret = (String) arguments.get("secret");
        Integer k = Integer.valueOf((String) arguments.get("k"));
        String dir = arguments.getOrDefault("dir", ".") + File.separator;
        List<File> files = Arrays.asList(new File(dir).listFiles()).stream().filter(file -> file.getName().endsWith(".bmp")).collect(Collectors.toList());
        Integer n = Integer.valueOf((String) arguments.getOrDefault("n", String.valueOf(files.size())));

        if (!secret.endsWith(".bmp")) throw new IllegalArgumentException("Invalid secret image");
        if (n < 2) throw new IllegalArgumentException("Invalid number of images to distribute to");
        if (k < 2 || k > n) throw new IllegalArgumentException("Invalid scheme(k,n)");

        Algorithm algorithm;

        if (mode.equals("r")) {
            algorithm = new Recovery(new File(secret), n, k, files);
        } else {
            algorithm = new Distribution(new File(secret), n, k, files);
        }

        if (algorithm.validate()) {
            algorithm.execute();
        } else {
            throw new IllegalArgumentException("Invalid image arrangement");
        }
    }
}
