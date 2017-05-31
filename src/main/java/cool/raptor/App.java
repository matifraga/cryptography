package cool.raptor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class App {
	public static void main(String[] args) throws IOException {
		
		Map<String, Object> arguments = new HashMap<>();

		for (int i = 0; i < args.length; i++) {
			String item = args[i];

			if (item.charAt(0) != '-')
				throw new IOException("Input error");

			if ((i + 1) < args.length) { // not the last item
				String next = args[i + 1];
				if (next.charAt(0) == '-')
					arguments.put(item, true);
				else {
					arguments.put(item, next);
					i += 1;
				}
			}
		}

		if ((arguments.get("-d") == null && arguments.get("-r") == null) || (arguments.get("-d") != null && arguments.get("-r") != null))
			throw new IOException("Undifined Distribution/Reception");
		
		boolean isRecovery = arguments.getOrDefault("-r", false) == Boolean.TRUE;
	
		StringBuilder fileName = new StringBuilder();
		StringBuilder path = new StringBuilder();
		File files[] = null;
		
		if(arguments.get("-dir") != null) {
			path.append((String) arguments.get("-dir"));
			path.append(File.separator);
			
			files = new File((String) arguments.get("-dir")).listFiles();
		} else {	
			files = new File(".").listFiles();
		}
		
		if(files == null || files.length == 0)
			throw new IOException("Invalid directory");
				
		if (arguments.get("-secret") == null) 
            throw new IOException("Undefined file name");
        else {
            fileName.append((String) arguments.get("-secret"));
            if (!fileName.toString().endsWith(".bmp")) 
                throw new IOException("Invalid file name");
        }
		
		if(arguments.get("-k") == null) 
			throw new IOException("Undefined number of shadows");

		int k = Integer.parseInt(((String) arguments.get("-k")));
		int n = 0;
		
		if(k<2)
			throw new IOException("Invalid number of shadows");
		
		for(File f: files) {
			if(!f.toString().endsWith(".bmp"))
				throw new IOException("Invalid file extension");
		}
	
		if(arguments.containsKey("-n")) {
			n = (Integer) arguments.get("-n");
		} else {
			n = files.length;
		}
		
		if(isRecovery) {
			
		} else {
			if(n<2 || k>n)
				throw new IOException("Invvalid scheme(k,n)");
			
			
		}
	}
}
