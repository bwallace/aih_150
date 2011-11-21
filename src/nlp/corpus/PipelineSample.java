package nlp.corpus;


import ml.instance.structured.StructuredInstance;
import nlp.annotation.LearnableSentenceAnnotator;

/**
 * Used when the CorpusSample is going to be passed through multiple Pipeline stages.
 * 
 * @author ksmall
 */
// TODO -- see what Parser means for this class
public class PipelineSample extends CorpusSample {

	protected LearnableSentenceAnnotator annotator;

	public PipelineSample(String fileName, LearnableSentenceAnnotator annotator) {
		super(fileName, null);
		this.annotator = annotator;
	}
	
	public StructuredInstance generate(String entry) {
		return annotator.testGenerate(parse(entry));
	}
}
