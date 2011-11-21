package ie;

import java.io.PrintWriter;
import java.util.ArrayList;

import nlp.annotation.AnnotatedSentence;
import nlp.core.Entity;
import nlp.core.Token;

public class UtilityIE {

	public static String printNER(AnnotatedSentence sentence) {
		String result = new String();
		Token[] tokens = sentence.getTokens();
		ArrayList<nlp.core.Element> entities = sentence.getAnnotation("predictedEntity");
		String[] prefix = new String[tokens.length];
		String[] suffix = new String[tokens.length];
		for (int i = 0; i < prefix.length; i++) {
			prefix[i] = "";
			suffix[i] = "";
		}
		if (entities != null) {
			for (nlp.core.Element entity : entities) {
				Entity e = (Entity) entity;
				prefix[Integer.parseInt(e.getProperty("start"))] = "[";
				suffix[Integer.parseInt(e.getProperty("end"))] = "]_" + e.getLabel(); 
					//+ "(" + e.getID() + ")";
			}
		}
		for (int i = 0; i < tokens.length; i++) {
			result += prefix[i] + tokens[i].getText() + suffix[i] + " ";
		}
		return result;
	}
	
	
	public static double F1(double p, double r, boolean print) {
		double f1 = (2 * p * r) / (p + r);
		if (print)
			System.out.println("p:" + p + " r:" + r + " f1:" + f1);
		return f1;		
	}
	
	public static double F1(double p, double r, PrintWriter out) {
		double f1 = (2 * p * r) / (p + r);
		out.println("p:" + p + " r:" + r + " f1:" + f1);
		return f1;		
	}	
}
