package cool.raptor;

import java.util.*;

public class Polynomial {
    private int[] coefs;
    private int order;

    public Polynomial(int[] coefs) {
        this.coefs=coefs;
        this.order=coefs.length-1;
    }

    public static Polynomial add(Polynomial p1, Polynomial p2){
        int n = Math.max(p1.coefs.length, p2.coefs.length);
        int[] newCoefs = new int[n];
        for(int i = 0; i < p1.coefs.length; i++){
            newCoefs[i]+=p1.coefs[i];
        }
        for(int i = 0; i < p2.coefs.length; i++){
            newCoefs[i]+=p2.coefs[i];
        }
        return new Polynomial(newCoefs);
    }

    public static Polynomial minus(Polynomial p1, Polynomial p2){
        int[] newCoefs = new int[p2.coefs.length];
        for(int i = 0; i < p2.coefs.length; i++){
            newCoefs[i] = -p2.coefs[i];
        }
        return add(p1, new Polynomial(newCoefs));
    }

    public static Polynomial mult(Polynomial p1, Polynomial p2){
        int[] newCoefs = new int[p1.coefs.length + p2.coefs.length - 1];
        for(int i = 0; i < p1.coefs.length; i++){
            for(int j = 0; j < p2.coefs.length; j++){
                newCoefs[i+j] += p1.coefs[i] * p2.coefs[j];
            }
        }
        return new Polynomial(newCoefs);
    }

    public static Polynomial solve(Map<Integer, Integer> ints, int mod){
        List<Map.Entry<Integer,Integer>> list = new ArrayList<>();
        list.addAll(ints.entrySet());
        int[] zero = {0};
        Polynomial ans = new Polynomial(zero);
        for(int i = 0; i<list.size(); i++){
            int y = list.get(i).getValue();
            int x = list.get(i).getKey();
            int den = 1;
            int[] aux = {1};
            Polynomial num = new Polynomial(aux);
            for(int j = 0; j<list.size(); j++){
                if(j!=i){
                    den*=(x-list.get(j).getKey());
                    int[] curr = {-list.get(j).getKey(),1};
                    num = Polynomial.mult(num,new Polynomial(curr));
                }
            }
            int[] res = {fractionToIntMod(y,den,mod)};
            num = Polynomial.mult(num,new Polynomial(res));
            ans = Polynomial.add(ans,num);
        }
        for(int i = 0; i<ans.coefs.length; i++){
            ans.coefs[i] = ans.coefs[i]%mod;
            if(ans.coefs[i] < 0)
                ans.coefs[i] += mod;
        }
        return ans;
    }

    private static int fractionToIntMod(int num, int den, int mod) {
        if(den<0){
            num *= -1;
            den *= -1;
        }
        while(true){
            num+= mod;
            if(num%den == 0){
                return (num/den)%mod;
            }
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        Map<Integer, Integer> ints = new HashMap<>();
        /*ints.put(1,3);
        ints.put(2,5);
        ints.put(4,0);

        int mod = 7;*/
        int mod = 257;
        for(int i = 1; i<5; i++){
            ints.put(i,rand.nextInt(mod));
        }
        for(Map.Entry<Integer, Integer> i : ints.entrySet()){
            System.out.println("X:"+i.getKey() + " Y:"+i.getValue());
        }

        Polynomial ans = Polynomial.solve(ints, mod);
        System.out.println(ans);

        //CHECKING IF OK
        Map<Integer, Integer> ints2 = new HashMap<>();
        for(Map.Entry<Integer, Integer> i : ints.entrySet()){
            ints2.put(i.getKey(), ans.evaluate(i.getKey()));
        }
        for(Map.Entry<Integer, Integer> i : ints2.entrySet()){
            System.out.println("X:"+i.getKey() + " Y:"+i.getValue());
            int key = i.getKey();
            System.out.println((ints.get(key)%mod == ints2.get(key)%mod)?"OK":"UPS");
        }
    }

    public int[] getCoefs() {
        return coefs;
    }

    public int evaluate(int x) {
        int result = 0;
        for (int i = 0; i <= order; i++) {
            result += Math.pow(x,i) * coefs[i];
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("P(x)=");
        sb.append("("+coefs[0]+")");
        for(int i = 1; i < coefs.length; i++){
            sb.append(i>0?"+(":"(").append(coefs[i]).append(i>0?"x":"").append(i>1?i:"").append(")");
        }
        return sb.toString();
    }

}
