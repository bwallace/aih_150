package text.output;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;


public class XML_Writer {
	
	public PrintXML printer;
	
	public XML_Writer(PrintXML printer) {
		this.printer = printer;
	}

	public void xml(PrintWriter out) {
		printer.xml(out);
	}
	
	public void xml(OutputStream out) {
		xml(new PrintWriter(out, true));
	}
	
	public String xml() {
		StringWriter writer = new StringWriter();
		xml(new PrintWriter(writer));
		return writer.toString().trim();
	}
}
