package cool.raptor;

public class Polynomial {
    private int[] coefs;
    private int order;

    public Polynomial(int[] coefs) {
        this.coefs=coefs;
        this.order=coefs.length-1;
    }

    public int evaluate(int x) {
        int result = 0;
        for (int i = 0; i <= order; i++) {
            result += Math.pow(x,i) * coefs[i];
        }
        return result;
    }
}
