package cool.raptor;

import java.util.Map;

public interface Shamir<K> {
    Map<Integer, K> split(K secret);

    K join(Map<Integer, K> shades, int secretWidth, int secretHeight);
}
