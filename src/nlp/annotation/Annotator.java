package nlp.annotation;

/**
 * The annotator interface.
 * 
 * @author ksmall
 *
 * @param <T>
 */
public interface Annotator<T> {

	public void annotate(T element);
	
}
