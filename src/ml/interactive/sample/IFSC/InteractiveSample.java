package ml.interactive.sample.IFSC;

import java.util.Collections;
import java.util.HashMap;

import ml.instance.Instance;
import ml.interactive.query.QueryFunction;
import ml.learn.Learner;
import ml.sample.ArrayListSample;
import ml.sample.Parser;

// TODO -- targeted specifically for IFSC sentence StructuredInstance (fix this later)

// TODO -- note that this is only applicable to SingleInstance items at this time (and binary -- ick)
// adding mistake driven stuff 7/24
public class InteractiveSample extends ArrayListSample<Instance, InteractiveEvent> {

	protected String datafile;
	protected InteractiveComparator comparator;
	protected int remaining;
	protected Parser<Instance> parser;
	
	public InteractiveSample() {
		this(new argmin());
	}
	
	public InteractiveSample(InteractiveComparator comparator) {
		super();
		this.comparator = comparator;
		remaining = 0;
	}

	public InteractiveSample(String datafile, HashMap<String,Integer> savedStatus, Parser<Instance> parser) {
		this(datafile, savedStatus, parser, new argmin());
	}
	
	public InteractiveSample(String datafile, HashMap<String,Integer> savedStatus, 
			Parser<Instance> parser, InteractiveComparator comparator) {
		this(comparator);
		this.datafile = datafile;
		this.parser = parser;
		readData(savedStatus);
	}
	
	/*
	public void extract(LearnableSentenceAnnotator system) {
		system.clearLexicons();
		System.out.println("here");
		System.out.println(system.featureLexicon.xml());
		for (InteractiveEvent i : events) {
			AnnotatedSentence s = (AnnotatedSentence) ((StructuredInstance) i.outcome).label;
			i.outcome = system.generate(s);
		}
	}
	*/
	
	// also used to reset
	public void readData(HashMap<String,Integer> savedStatus) {
		parser.open(datafile);
		events.clear();
		Instance item = null;
		while ((item = parser.next()) != null) {
			if (savedStatus.containsKey(item.identifier()))
				add(new InteractiveEvent(item, savedStatus.get(item.identifier())));
			else
				add(new InteractiveEvent(item, InteractiveEvent.NONE));
		}
		remaining = events.size();
		reset();		
	}
	
	/*
	public void unlabel() {
		for (int i = 0; i < size(); i++) {
			SelectiveEvent<Instance> event = events.get(i);
			if (event.status == SelectiveEvent.LABELED) {
				event.status = SelectiveEvent.UNLABELED;
				((SingleInstance) event.outcome).firstLabel().setVisible(false);
				unlabeled++;
			}
		}		
	}
	*/
	
	// assumes everything is unlabeled
	/*
	public void initialize(double seedPercentage, boolean permute) {
		unlabeled = size();
		// TODO -- really not necessary anymore
		for (int i = 0; i < size(); i++) {
			SelectiveEvent<Instance> event = events.get(i);
			if (event.status == SelectiveEvent.SEED) {
				event.status = SelectiveEvent.UNLABELED;
				((SingleInstance) event.outcome).firstLabel().setVisible(false);
			}
		}
		if (permute)
			permute();
		int count = 0;
		if (seedPercentage >= 1.0)
			count = (int) seedPercentage;
		else
			count = (int) (seedPercentage * unlabeled);
		for (int i = 0; i < count; i++) {
			if (i < size()) {
				((SelectiveEvent<Instance>) events.get(i)).status = SelectiveEvent.SEED;
				((SingleInstance) events.get(i).outcome).firstLabel().setVisible(true);
				unlabeled--;
			}
			else
				break;
		}
	}
	*/
	
	// TODO -- this shows that we need statistics
	public void query(Learner learner, QueryFunction<Instance> queryFunction, int required) {
		remaining = 0;
		for (InteractiveEvent event : events) {
			if (event.status == InteractiveEvent.DONE)
				event.score = comparator.lastValue();
			else { // TODO - just converting all to none {
				// TODO - other part of no dictionary hack
				//event.status = InteractiveEvent.NONE;
				event.status = InteractiveEvent.SELECTED;
				event.score = queryFunction.score(learner, event.outcome);
				remaining++;
			}
			/*
			if (((SingleInstance) event.outcome).firstLabel().isVisible())  // helps with "random seed" reorderings
				event.score = comparator.lastValue();
			else
				event.score = queryFunction.score(learner, event.outcome);
			*/
		}
		Collections.sort(events, comparator);
		// TODO -- hack to account for words
		/*
		int batch = Math.min(required, remaining);
		int counter = 0;
		int i = 0;
		while (counter < batch) {
			InteractiveEvent currentEvent = events.get(i);
			//if (currentEvent.status == SelectiveEvent.UNLABELED) {
			currentEvent.status = InteractiveEvent.SELECTED;
			counter++;
			i++;
		}
		*/
	}
}
