package text;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UtilityXML {

	public static String[] getNodeValues(Element element, String name) {
		NodeList list = element.getElementsByTagName(name);
		if (list == null)
			return null;
		String[] result = new String[list.getLength()];
		for (int i = 0; i < result.length; i++)
			result[i] = list.item(i).getFirstChild().getNodeValue().trim();
		return result;
	}
	
	// a little faster
	public static String getNodeValue(Element element, String name, int index) {
		NodeList list = element.getElementsByTagName(name);
		if (list == null)
			return null;
		Node node = list.item(index).getFirstChild();
		if (node == null)
			return null;
		return node.getNodeValue().trim();
	}
	
	public static String getNodeValue(Node element, String name) {
		return getNodeValue((Element) element, name, 0);
	}
	
	public static String getNodeValue(Element element, String name) {
		return getNodeValue(element, name, 0);
	}
	
	public static Element getElement(Element element, String name, int index) {
		return (Element) element.getElementsByTagName(name).item(index);
	}
	
	public static Element getElement(Element element, String name) {
		return getElement(element, name, 0);
	}
}