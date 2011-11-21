package ml.extraction;

/**
 * The feature generator function interface.
 * 
 * @author ksmall
 *
 * @param <T>	the type of objects which will be generated
 */
public interface Generator<T> {

	T generate(Object o);
}
