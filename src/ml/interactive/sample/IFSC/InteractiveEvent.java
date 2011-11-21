package ml.interactive.sample.IFSC;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ml.extraction.ExplanationLexicon;
import ml.instance.Instance;
import ml.instance.structured.StructuredInstance;
import ml.sample.Event;
import nlp.annotation.AnnotatedSentence;
import nlp.annotation.Annotator;
import nlp.core.Element;
import nlp.core.Entity;
import nlp.core.Relation;
import nlp.core.Token;
import nlp.semantics.SemanticDictionary;
import nlp.semantics.SemanticDictionary.SetPointer;
import text.output.PrintXML;
import text.output.XML_Writer;

public class InteractiveEvent extends Event<Instance> implements PrintXML {

	public double score;
	public int status;
	public ExplanationLexicon lexicon;
	public Annotator<AnnotatedSentence> annotator;
	public SemanticDictionary dictionary;
	
	public static int NONE = 0;
	public static int MARKED = 1;
	public static int SELECTED = 2;
	public static int DONE = 3;
	
	private static String COLOR_PEOP = "CC00CC"; // purple
	private static String COLOR_LOC = "3333FF"; // blue
	private static String COLOR_ORG = "FF6633"; // orange
	private static String COLOR_MISTAKE = "FF0000"; // red

	protected static int left = 2;
	protected static int right = 2;
	
	public XML_Writer writer;

	public AnnotatedSentence sentence;
	public Token[] tokens;

	public ArrayList<Element> goldEntities;
	public ArrayList<Element> predictedEntities;
	
	public ArrayList<Element> predictedRelations;
	public ArrayList<Element> goldRelations;

	//public ArrayList<Token> candidates;
	public ArrayList<String> candidates;
	
	public InteractiveEvent(Instance outcome, double weight, boolean isProbability, double score, int status) {
		super(outcome, weight, isProbability);
		this.score = score;
		this.status = status;
		writer = new XML_Writer(this);
	}

	public InteractiveEvent(Instance outcome) {
		this(outcome, 1.0, false, 0.0, NONE);
	}

	public InteractiveEvent(Instance outcome, int status) {
		this(outcome, 1.0, false, 0.0, status);
	}
	
	public void setSystem(Annotator<AnnotatedSentence> annotator, SemanticDictionary dictionary,
			ExplanationLexicon lexicon) {
		this.annotator = annotator;
		this.lexicon = lexicon;
		this.dictionary = dictionary;
		analyze();
	}
	
	// TODO -- finish this
	/*
	public void xml(PrintWriter out) {
		StructuredInstance instance = (StructuredInstance) outcome;
		AnnotatedSentence sentence = (AnnotatedSentence) instance.label;
		out.println(sentence.getTokenString());
		for (Instance i : instance) {
			SingleInstance i2 = (SingleInstance) i;
			for (Feature f : i2.features) {
				out.println(lexicon.getExplanation(f.identifier()));
			}
		}
	}
	*/

	public void analyze() {
		sentence = (AnnotatedSentence) ((StructuredInstance) outcome).label;
		//sentence.removeAnnotation("predictedEntity");
		//sentence.removeAnnotation("predictedRelation");
		annotator.annotate(sentence);
		
		tokens = sentence.getTokens();

		goldEntities = sentence.getAnnotation("entity");
		predictedEntities = sentence.getAnnotation("predictedEntity");
		
		predictedRelations = sentence.getAnnotation("predictedRelation");
		goldRelations = sentence.getAnnotation("relation");
		
		candidates = getCandidates(sentence, goldEntities, predictedEntities, 3);		
	}
	
	// TODO - also add relations
	public void xml(PrintWriter out) {

		int[][] relationCorrect = evaluateRelations(goldRelations, predictedRelations);
		boolean[][] entityCorrect = evaluateEntities(goldEntities, predictedEntities);
				
		String result = new String();
		/*
		result += "<tr><td>";
		result += sentence.getTokenString();
		result += "</td></tr>";
		*/
		result += "<tr>";
		result += "<td rowspan=\"2\" width=\"100\">GOLD</td>";
		result += "<td>" + entityPrint(tokens, goldEntities, entityCorrect[0]) + "</td>";
		result += "</tr><tr>";
		result += "<td>" + relationsPrint(goldRelations, relationCorrect[0]) + "</td>";
		result += "</tr>";
		
		result += "<tr>";
		result += "<td rowspan=\"2\" width=\"100\">PREDICTED</td>";
		result += "<td>" + entityPrint(tokens, predictedEntities, entityCorrect[1]) + "</td>";
		result += "</tr><tr>";
		result += "<td>" + relationsPrint(predictedRelations, relationCorrect[1]) + "</td>";
		result += "</tr>";
		
		/*
		result += "<tr><td colspan=\"2\">";
		result += ((StructuredInstance) outcome).identifier() + ": "; 
		for (int i = 0; i < candidates.size(); i++)
			//result += candidates.get(i).getText() + " ";
			result += candidates.get(i) + " ";
		result += "</td></tr>";
		*/
		
		/*
		result += "<tr><td>";
		result += "gold: ";
		result += entityPrint(tokens, goldEntities, entityCorrect[0]);
		result += "</td></tr>";
		result += "<tr><td>";
		result += "predicted: ";
		result += entityPrint(tokens, predictedEntities, entityCorrect[1]);
		result += "</td></tr>";
		result += "<tr><td>";
		result += "gold: ";
		result += relationsPrint(goldRelations, relationCorrect[0]);
		result += "</td></tr>";
		result += "<tr><td>";
		result += "predicted: ";
		result += relationsPrint(predictedRelations, relationCorrect[1]);
		result += "</td></tr>";
		*/
		/*
		result += "<tr><td>";
		result += "gold: " + goldEntities;
		result += "</td></tr>";
		result += "<tr><td>";
		result += "predicted: " + predictedEntities;
		result += "</td></tr>";			
		*/	
		// also generates global variable with strings to create buttons
		//boolean[] candidates = getCandidates(tokens, goldEntities, si);
		/*
		for (int i = 0; i < candidates.length; i++)
			System.out.print(candidates[i] + " ");
		System.out.println();
		*/
		
		/* this is where everything actually starts
		HashSet<Integer> incorrect = new HashSet<Integer>();
		System.out.println(sentence);
		System.out.println("gold:" + goldEntities);
		System.out.println("predicted:" + predictedEntities);
		for (int i = 0; i < goldEntities.size(); i++) {
			if (!goldEntities.get(i).getLabel().equals(predictedEntities.get(i).getLabel()))
				incorrect.add(i);
		}
		
		String[] goldPrefix = new String[tokens.length];
		String[] goldSuffix = new String[tokens.length];
		for (int i = 0; i < goldPrefix.length; i++) {
			goldPrefix[i] = "";
			goldSuffix[i] = "";
		}
		int counter = 0;
		for (Element entity : goldEntities) {
			Entity e = (Entity) entity;
			if (incorrect.contains(counter)) {
				goldPrefix[Integer.parseInt(e.getProperty("start"))] = mistakeColor("[") + entityColor(e.getLabel());
				goldSuffix[Integer.parseInt(e.getProperty("end"))] = "</font>" + mistakeColor("]<sub>" + e.getLabel() + 
					"</sub>");
			}
			else {
				goldPrefix[Integer.parseInt(e.getProperty("start"))] = "[" + entityColor(e.getLabel());
				goldSuffix[Integer.parseInt(e.getProperty("end"))] = "</font>]<sub>" + e.getLabel() + 
				//"(" + e.getID() + ")</sub>";
					"</sub>";
			}
			goldEntityID.put(e.getID(), e.getTokenString());
			counter++;
		}

		String[] predictedPrefix = new String[tokens.length];
		String[] predictedSuffix = new String[tokens.length];
		for (int i = 0; i < predictedPrefix.length; i++) {
			predictedPrefix[i] = "";
			predictedSuffix[i] = "";
		}
		counter = 0;
		for (Element entity : predictedEntities) {
			Entity e = (Entity) entity;
			if (incorrect.contains(counter)) {
				predictedPrefix[Integer.parseInt(e.getProperty("start"))] = mistakeColor("[") + entityColor(e.getLabel());
				predictedSuffix[Integer.parseInt(e.getProperty("end"))] = "</font>" + mistakeColor("]<sub>" + e.getLabel() + 
					"</sub>");
			}
			else {
				predictedPrefix[Integer.parseInt(e.getProperty("start"))] = "[" + entityColor(e.getLabel());
				predictedSuffix[Integer.parseInt(e.getProperty("end"))] = "</font>]<sub>" + e.getLabel() + 
					//"(" + e.getID() + ")</sub>";
					"</sub>";
			}
			predictedEntityID.put(e.getID(), e.getTokenString());
			counter++;
		}
		
		//String result = new String("<table border=\"1\" width=\"" + width + "\">\n");
		//result += "<td width=\"" + width + "\">\n";
		
		String result = new String();
		result += "<tr>\n";
		result += "<td>Correct</td>\n";
		result += "<td width=\"100%\">";
		//result += ((Sentence) si.label).getTokenString();
		for (int i = 0; i < tokens.length; i++) {
			result += goldPrefix[i];
			
			//if (candidates[i])
			//	result += "<i><b>" + tokens[i].getText() + "</b></i>";
			//else
			
				result += tokens[i].getText();
			result += goldSuffix[i] + " ";
		}
		result += "{ ";
		for (Element relation : goldRelations) {
			Relation r = (Relation) relation;
			result += r.getLabel() + "(" + goldEntityID.get(r.getArgument(0).getID()) + "," + 
				goldEntityID.get(r.getArgument(1).getID()) + ") ";
		}
		result += "}";
		result += "</td>\n";
		result += "</tr>\n";
		
		result += "<tr>\n";
		result += "<td>Predicted</td>\n";
		result += "<td width=\"100%\">";
		//result += ((Sentence) si.label).getTokenString();
		for (int i = 0; i < tokens.length; i++) {
			result += predictedPrefix[i];
			
		//	if (candidates[i])
		//		result += "<i><b>" + tokens[i].getText() + "</b></i>";
		//	else
			
				result += tokens[i].getText();
			result += predictedSuffix[i] + " ";
		}
		
		//result += "{ ";
		//for (Element relation : goldRelations) {
		//	Relation r = (Relation) relation;
		//	result += r.getLabel() + "(" + goldEntityID.get(r.getArgument(0).getID()) + "," + 
		//		goldEntityID.get(r.getArgument(1).getID()) + ") ";
		//}
		//result += "}";
		
		result += "</td>\n";
		result += "</tr>\n";		
		*/
		//result += "</table>\n";
		//System.out.println(annotator.generator().explain(si));
		//return result;
		out.println(result);
	}

	//int[][] relationCorrect = evaluateRelations(predictedRelations, goldRelations);

	public String relationsPrint(ArrayList<Element> relations, int[] status) {
		String result = new String();
		int counter = 0;
		if (relations != null) {
			for (Element element : relations) {
				Relation r = (Relation) element;
				if (!r.getLabel().equals("R0")) {
					if (status[counter] == 1)
						result += mistakeColor(printRelation(r)) + " ";
					else
						result += printRelation(r) + " ";
				}
				counter++;
			}
		}
		return result;
	}
	
	public String entityPrint(Token[] tokens, ArrayList<Element> entities, boolean[] errors) {
		String result = new String();
		HashSet<Integer> start = new HashSet<Integer>();
		HashMap<Integer,String> end = new HashMap<Integer,String>();
		HashSet<Integer> mistake = new HashSet<Integer>();
		int counter = 0;
		for (Element e : entities) {
			Entity entity = (Entity) e;
			start.add(Integer.parseInt(entity.getProperty("start")));
			end.put(Integer.parseInt(entity.getProperty("end")), entity.getLabel());
			if (errors[counter])
				mistake.add(Integer.parseInt(entity.getProperty("start")));
			counter++;
		}
		boolean isMistake = false;
		boolean isEntity = false;
		String entity = new String();
 		for (int i = 0; i < tokens.length; i++) {
			if (!start.contains(i)) {
				if (isEntity)
					entity += tokens[i].getText() + " ";
				else
					result += tokens[i].getText() + " ";
			}
			else {
				entity = new String("[ " + tokens[i].getText() + " ");
				isEntity = true;
				if (mistake.contains(i))
					isMistake = true;
				else
					isMistake = false;
			}
			if (end.containsKey(i)) {
				isEntity = false;
				entity += "]<sub>" + end.get(i) + "</sub> ";
				if (isMistake)
					result += mistakeColor(entity);
				else
					result += entityColor(entity, end.get(i));
			}
		}
 		return result;
	}
	
	public String printRelation(Element element) {
		Relation relation = (Relation) element;
		String result = new String(relation.getLabel() + "(");
		result += relation.getArgument(0).getTokenString() + ",";
		result += relation.getArgument(1).getTokenString() + ")";
		return result;
	}
	
	public String printEntity(Element element) {
		Entity e = (Entity) element;
		String result = new String("[" + e.getTokenString() + "]<sub>" + e.getLabel() + "</sub>");
		return result;
	}
	
	// true is error, false is no error
	public boolean[][] evaluateEntities(ArrayList<Element> goldEntities, ArrayList<Element> predictedEntities) {
		boolean[][] result = new boolean[2][];
		if (goldEntities == null)
			result[0] = new boolean[0];
		else
			result[0] = new boolean[goldEntities.size()];
		if (predictedEntities == null)
			result[1] = new boolean[0];
		else 
			result[1] = new boolean[predictedEntities.size()];
		
		int counter = 0;
		for (Element entity : goldEntities) {
			if (existsEntity(entity, predictedEntities))
				result[0][counter] = false;
			else
				result[0][counter] = true;
			counter++;
		}

		counter = 0;
		if (predictedEntities != null) {
			for (Element entity : predictedEntities) {
			//System.out.println(((Relation) relation).getLabel());
				if (existsEntity(entity, goldEntities))
					result[1][counter] = false;
				else
					result[1][counter] = true;
				counter++;
			}
		}
				
		return result;
	}
	
	// 0 is no error, 1 is error, 2 is R0
	public int[][] evaluateRelations(ArrayList<Element> goldRelations, ArrayList<Element> predictedRelations) {
		int[][] result = new int[2][];
		if (goldRelations == null)
			result[0] = new int[0];
		else
			result[0] = new int[goldRelations.size()];
		if (predictedRelations == null)
			result[1] = new int[0];
		else
			result[1] = new int[predictedRelations.size()];
		
		int counter = 0;
		for (Element relation : goldRelations) {
			if (existsRelation(relation, predictedRelations))
				result[0][counter] = 0;
			else
				result[0][counter] = 1;
			counter++;
		}

		counter = 0;
		if (predictedRelations != null) {
			for (Element relation : predictedRelations) {
			//System.out.println(((Relation) relation).getLabel());
				if (((Relation) relation).getLabel().equals("R0"))
					result[1][counter] = 2;
				else if (existsRelation(relation, goldRelations))
					result[1][counter] = 0;
				else
					result[1][counter] = 1;
				counter++;
			}
		}

		return result;
	}
	
	public boolean existsRelation(Element relation, ArrayList<Element> relations) {
	
		if (relations == null)
			return false;
	
		Relation r0 = (Relation) relation;
		Entity r0e0 = r0.getArgument(0);
		Entity r0e1 = r0.getArgument(1);
		
		for (Element element : relations)  {
			Relation r1 = (Relation) element;
			Entity r1e0 = r1.getArgument(0);
			Entity r1e1 = r1.getArgument(1);
			if ((r0e0.getProperty("start").equals(r1e0.getProperty("start"))) &&
					(r0e0.getProperty("end").equals(r1e0.getProperty("end"))) &&
					(r0e1.getProperty("start").equals(r1e1.getProperty("start"))) &&
					(r0e1.getProperty("end").equals(r1e1.getProperty("end"))) &&
					(r0.getLabel().equals(r1.getLabel())))
				return true;
		}
		
		return false;
	}

	public boolean existsEntity(Element entity, ArrayList<Element> entities) {
		
		if (entities == null)
			return false;
	
		Entity e0 = (Entity) entity;
		
		for (Element element : entities)  {
			Entity e1 = (Entity) element;
			if ((e0.getProperty("start").equals(e1.getProperty("start"))) &&
					(e0.getProperty("end").equals(e1.getProperty("end"))) &&
					(e0.getLabel().equals(e1.getLabel())))
				return true;
		}
		
		return false;
	}

	// TODO -- bigrams
	public ArrayList<String> getCandidates(AnnotatedSentence sentence, ArrayList<Element> gold, 
			ArrayList<Element> predicted, int n) {
		//HashMap<Integer,Token> candidates = new HashMap<Integer,Token>();
		HashMap<Integer,String> candidates = new HashMap<Integer,String>();
		Token[] tokens = sentence.getTokens();
		for (Element element : gold) {
			Entity entity = (Entity) element;
			int start = Math.max(0, Integer.parseInt(entity.getProperty("start")) - left);
			int end = Math.min(sentence.length(), Integer.parseInt(entity.getProperty("end")) + right + 1);
			//for (int i = start; i < end; i++)
			//	candidates.put(i, tokens[i]);
			for (int x = 1; x <= n; x++) {
				for (int i = start; i < (end - x + 1); i++) {
					String phrase = new String();
					for (int j = 0; j < x; j++) {
						phrase += tokens[i + j].getText();
						if (j < x - 1)
							phrase += " ";
					}
					candidates.put(i, phrase);
				}
			}
		}
		for (Element element : predicted) {
			Entity entity = (Entity) element;
			int start = Math.max(0, Integer.parseInt(entity.getProperty("start")) - left);
			int end = Math.min(sentence.length(), Integer.parseInt(entity.getProperty("end")) + right + 1);
			/*
			for (int i = start; i < end; i++)
				candidates.put(i, tokens[i]);
			*/
			
			for (int x = 1; x <= n; x++) {
				for (int i = start; i < (end - x + 1); i++) {
					String phrase = new String();
					for (int j = 0; j < x; j++) {
						phrase += tokens[i + j].getText();
						if (j < x - 1)
							phrase += " ";
					}
					candidates.put(i, phrase);
				}
			}
			
		}		
		//Token[] result = new Token[candidates.keySet().size()];
		//int counter = 0;
		//ArrayList<Token> result = new ArrayList<Token>();
		ArrayList<String> result = new ArrayList<String>();
		for (Integer i : candidates.keySet()) {
			//result[counter++] = candidates.get(i);
			//ArrayList<SetPointer> set = dictionary.get(candidates.get(i).getText());
			ArrayList<SetPointer> set = dictionary.get(candidates.get(i));
			if (set != null)
				result.add(candidates.get(i));
		}
		return result;
	}
	
	/*
		if (predictedRelations != null) {
			for (Iterator<Element> it = trueRelations.iterator(); it.hasNext(); ) {
				Relation r1 = (Relation) it.next();
				Entity r1e0 = r1.getArgument(0);
				Entity r1e1 = r1.getArgument(1);
				if (Evaluation_Labels.contains(r1.getLabel())) {
					for (Iterator<Element> jt = predictedRelations.iterator(); jt.hasNext(); ) {
						Relation r2 = (Relation) jt.next();
						// assuming left and right here (as dictated by the labeler)
						Entity r2e0 = r2.getArgument(0);
						Entity r2e1 = r2.getArgument(1);
						// this is where we would do F1*
						if ((r1e0.getProperty("start").equals(r2e0.getProperty("start"))) &&
							(r1e0.getProperty("end").equals(r2e0.getProperty("end"))) &&
							(r1e1.getProperty("start").equals(r2e1.getProperty("start"))) &&
							(r1e1.getProperty("end").equals(r2e1.getProperty("end"))) &&
							(r1.getLabel().equals(r2.getLabel())))
							thisCorrect++;
					}
					thisPredicted++;
				}
				thisGold++;
			}
			gold += thisGold;
			correct += thisCorrect;
			predicted += thisPredicted;
		}
		else {
			gold += trueRelations.size();
		}
	}
	*/
	
	public String mistakeColor(String input) {
		return new String("<b><font size=\"+1\" color=\"" + COLOR_MISTAKE + "\">" + input + "</font></b>");
	}

	protected String entityColor(String input, String label) {
		if (label.equals("Peop"))
			return "<font color=\"" + COLOR_PEOP + "\">" + input + "</font>";
		else if (label.equals("Loc"))
			return "<font color=\"" + COLOR_LOC + "\">" + input + "</font>";
		else if (label.equals("Org"))
			return "<font color=\"" + COLOR_ORG + "\">" + input + "</font>";
		else
			return "";
	}
	
	public void xml(OutputStream out) {
		writer.xml(out);
	}

	public String xml() {
		return writer.xml();
	}
}
