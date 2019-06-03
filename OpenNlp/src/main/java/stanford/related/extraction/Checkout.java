package stanford.related.extraction;

import java.util.Set;

public class Checkout {

	public static void main(String[] args) {
		String text = "How can I increase the speed of my internet connection while using a VPN.";
		Set<Triple> triple = OpenNLPPipeline.getInstance().generateTriples(text);
		System.out.println(triple);
	}
}
