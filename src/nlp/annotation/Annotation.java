package nlp.annotation;

/**
 * The Annotation interface.  Used when an annotation can be added to the implementation object.
 * 
 * @author ksmall
 *
 * @param <T>
 */
public interface Annotation<T> {

	// must add internal links also
	T addAnnotation(org.w3c.dom.Element e, AnnotatedSentence sentence, String args);
	
}
