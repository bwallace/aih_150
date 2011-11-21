package ie.ner;

import ie.UtilityIE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import nlp.annotation.AnnotatedSentence;

import org.xml.sax.InputSource;

import text.output.ProcessText;

public class ProcessNER implements ProcessText {

	protected PipelineNER ner;
	
	public ProcessNER(PipelineNER ner) {
		this.ner = ner;
	}

	public ProcessText newInstance() {
		return this;
	}

	public void process(InputSource data, PrintWriter out) {
		BufferedReader input = new BufferedReader(data.getCharacterStream());
		String s = null;
		try {
			s = input.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		} 
			//new String("Ted Pedersen is a professor at the University of Minnesota in Duluth .");
		/*
		for (int i = 0; i < args.length; i++)
			s += args[i] + " ";
		*/
		AnnotatedSentence sentence = new AnnotatedSentence(s);
		//out.println(UtilityIE.printNER(sentence));
		ner.annotate(sentence);
		//System.out.println(sentence);
		out.println(UtilityIE.printNER(sentence));

	}
}
