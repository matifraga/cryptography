package cool.raptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Distribution {

	private int k;
	private int n;
	private Image image;
	private List<Image> hosts;
	
	public Distribution(final int n, final int k, final File secret, final File[] hosts) {
		this.k = k;
		this.n = n;
		
		image = new Image(secret);
		this.hosts = new ArrayList<>(hosts.length);
		for (File file : hosts) {
			this.hosts.add(new Image(file));
		}
	}
	
	public boolean distribute() {
		return image==null || n==k;
	}
}
