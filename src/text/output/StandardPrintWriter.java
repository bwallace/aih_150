package text.output;

import java.io.PrintStream;
import java.io.PrintWriter;

// TODO -- probably rename
public class StandardPrintWriter extends PrintWriter implements GeneralPrintWriter {

	public StandardPrintWriter(PrintStream stream) {
		super(stream, true);
	}
	
	public void print(String s) {
		super.print(s);
	}

	public void println(String s) {
		super.println(s);
	}

}
