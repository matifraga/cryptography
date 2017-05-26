package cool.raptor;

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
		
		boolean isReception = arguments.getOrDefault("-r", false) == Boolean.TRUE;
	
		StringBuilder fileName = new StringBuilder();
				
		if(arguments.get("-dir") != null) {
			fileName.append("\\");
			fileName.append((String) arguments.get("-dir"));
		}
		fileName.append("\\");
		
		if (arguments.get("-secret") == null) 
            throw new IOException("Undefined file name");
        else {
            fileName.append((String) arguments.get("-secret"));
            if (!fileName.toString().endsWith(".bmp")) 
                throw new IOException("Invalid file name");
        }
		
		if(arguments.get("-k") == null) 
			throw new IOException("Undefined number of shadows");

		int k = (Integer) arguments.get("-k");
		int n;
		
		if(k<2)
			throw new IOException("Invalid number of shadows");
		
		if(arguments.containsKey("-n")) {
			n = (Integer) arguments.get("-n");
		} else {
			//n = #Imagenes del directorio
		}
		
		if(isReception) {

		} else {
			
		}
		
		
		
//		if (arguments.get("-s") == null)
//			throw new IOException("Missing Search Strategy");
//		else {
//			String searchStrategy = (String) arguments.get("-s");
//			if (!SearchStrategy.contains(searchStrategy))
//				throw new IOException("Invalid strategy.");
//
//			strategy = SearchStrategy.valueOf(searchStrategy);
//		}
//
//		if (arguments.get("-c") == null)
//			throw new IOException("Missing cut condition");
//		else
//			cut = new Integer((String) arguments.get("-c"));
//
//		if (arguments.get("-h") == null)
//			throw new IOException("Missing heuristic");
//		else
//			hCode = new Integer((String) arguments.get("-h"));
//
	}
}
