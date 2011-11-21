package nlp.extraction;

import nlp.extraction.filter.Lowercase;

public class UtilityExtraction {

	public static MembershipTester getGazetteer(String name, String file, boolean lowercase) {
		MembershipTester result = new MembershipTester(name);
		if (lowercase) {
			result.addSourceFilter(new Lowercase());
			result.addTargetFilter(new Lowercase());
		}
		result.addFile(file);
		return result;
	}	
}
