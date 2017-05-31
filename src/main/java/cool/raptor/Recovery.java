package cool.raptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Recovery {

	private int k;
	private int n;
	private List<Image> hosts;
	
	public Recovery(final int n, final int k, final File[] hosts) {
		this.k = k;
		this.n = n;
		this.hosts = new ArrayList<>(hosts.length);
		for (File file : hosts) {
			this.hosts.add(new Image(file));
		}
	}
	
	public Image recover() {
		return n==k? hosts.get(n):hosts.get(k);
	}
}
