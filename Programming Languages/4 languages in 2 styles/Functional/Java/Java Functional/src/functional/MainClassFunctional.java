
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainClassFunctional {

	private static Stream<String> readFromFile(String fileName) {
		Path path = Paths.get(fileName);
		Stream<String> returnStream;
		try {
			returnStream = Files.lines(path, Charset.forName("Cp1252"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem with reading from the file", e);
		}
		return returnStream;
	}

	private static void printStreamResult(Stream<?> resultStream) {
		resultStream.forEachOrdered(line -> System.out.println(line));
	}
	
	
	public static void main(String[] args) {
		
		Function <Stream<String>, Stream<String>> caseConverter = (strStream -> strStream.map(str -> str.toLowerCase()));
		
		Function <Stream<String>, Stream<String>> wordFinder = (strStream -> strStream.map(str -> str.split(" ")).flatMap(Arrays::stream));
		
		Function <Stream<String>, Stream<String>> nonABCFilterer = (strStream -> strStream.map(str -> str.replaceAll("[^A-Za-z0-9]+", "")));
		
		Function <Stream<String>, Stream<Map.Entry<String,Integer>>> wordCounter = (strStream -> strStream.collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e->1))).entrySet().stream());

		if (Arrays.stream(args).anyMatch(str -> str.equals("grep") || str.equals("wc"))) {
			if (Arrays.stream(args).anyMatch(str -> str.equals("grep"))) {
				// this means that grep is used
				if (Arrays.stream(args).count() == 3) {
					// only grep is used, not wc
					printStreamResult(readFromFile(args[2]).filter(line -> line.toLowerCase().contains(args[1].toLowerCase())));
					return;
				}
				else {
					// both grep and wc are called
					printStreamResult(
							caseConverter.andThen(wordFinder).
									andThen(nonABCFilterer).andThen(wordCounter).
										apply(readFromFile(args[2]).
												filter(line -> line.toLowerCase().contains(args[1].toLowerCase())))
					);
					
				}
			}
			
			if (Arrays.stream(args).anyMatch(str -> str.equals("wc"))) {
				// this means that wc is used
				if (Arrays.stream(args).count() == 2) {
					// only wc was used
					printStreamResult(
							caseConverter.andThen(wordFinder).
									andThen(nonABCFilterer).andThen(wordCounter).
										apply(readFromFile(args[1]))
					);
				}
				
				
			}
		}
		else {
			throw new IllegalArgumentException();
		}

	}
	

}
