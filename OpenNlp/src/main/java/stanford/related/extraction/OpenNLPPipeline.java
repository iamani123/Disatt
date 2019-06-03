package stanford.related.extraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import opennlp.tools.coref.DefaultLinker;
import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.LinkerMode;
import opennlp.tools.coref.mention.DefaultParse;
import opennlp.tools.coref.mention.Mention;
import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.coref.resolver.ResolverUtils;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class OpenNLPPipeline {

	private static OpenNLPPipeline instance;

	private final static TokenizerModel tokenModel;
	private final static SentenceModel sentenceModel;
	private final static ParserModel parseModel;
	private final static POSModel posModel;

	private final SentenceDetector sentenceDetector;
	private final Tokenizer tokenizer;
	private final Parser parser;
	//private final Linker linker;
	@SuppressWarnings("unused")
	private final POSTagger tagger;

	static {
		try {
			// tokens
			InputStream modelIn = getModel("D:\\Subjects\\research\\Tools\\en-token.bin");
			tokenModel = new TokenizerModel(modelIn);
			modelIn.close();

			// sentence model
			modelIn = getModel("D:\\Subjects\\research\\Tools\\en-sent.bin");
			sentenceModel = new SentenceModel(modelIn);
			modelIn.close();

			// parser
			modelIn = getModel("D:\\Subjects\\research\\Tools\\en-parser-chunking.bin");
			parseModel = new ParserModel(modelIn);
			modelIn.close();
			
			// part-of-speech model. not currently used
			modelIn = getModel("D:\\Subjects\\research\\Tools\\en-pos-maxent.bin");
			posModel = new POSModel(modelIn);
			modelIn.close();		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private static InputStream getModel(String fileName) throws FileNotFoundException {
		FileInputStream modelUse = new FileInputStream(fileName);
		
		return modelUse;
		
	}
	
	public static synchronized OpenNLPPipeline getInstance() {
		if (instance == null) {
			System.out.println("Initializing NLP pipeline...");
			instance = new OpenNLPPipeline();
			System.out.println("Done initializing.");
		}
		return instance;
	}
	private OpenNLPPipeline() {
		sentenceDetector = new SentenceDetectorME(sentenceModel);
		tokenizer = new TokenizerME(tokenModel);
		parser = ParserFactory.create(parseModel);
		tagger = new POSTaggerME(posModel);
		try {
			//File path = new File(, "coref");
		//	linker = new DefaultLinker(path.getAbsolutePath(), LinkerMode.TEST);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	

	/**
	 * Create parse trees from a sentence
	 * @param text the sentence string
	 * @param tokens an array of spans with token positions
	 * @param n how many parse trees to return
	 * @return an array of probable parse trees
	 */
	public Parse[] parseSentence(final String text, final Span[] tokens, final int n) {
		final Parse p = new Parse(text,
		// a new span covering the entire text
				new Span(0, text.length()),
				// the label for the top if an incomplete node
				AbstractBottomUpParser.INC_NODE,
				// the probability of this parse...uhhh...?
				1,
				// the token index of the head of this parse
				0);

		for (int idx = 0; idx < tokens.length; idx++) {
			final Span tok = tokens[idx];
			// flesh out the parse with token sub-parses
			p.insert(new Parse(text, tok, AbstractBottomUpParser.TOK_NODE, 0, idx));
		}
		synchronized (parser) {
			return parser.parse(p, n);
		}

	}
	
	public Parse parseSentence(final String text, final Span[] tokens) {
		return parseSentence(text, tokens, 1)[0];
	}
	
	
	
	public Set<Triple> generateTriples(String text) {
		Set<Triple> triples = new HashSet<Triple>();
		new TripleGenerator(triples, text).run();
		return triples;
	}

	
	
	public String[] getSentences(String text) {
		synchronized (sentenceDetector) {
			return sentenceDetector.sentDetect(text);
		}
	}
	
	public Span[][] getTokens(String[] sentences) {
		Span[][] tokens = new Span[sentences.length][];
		synchronized (tokenizer) {
			for (int i = 0; i < tokens.length; i++) {
				tokens[i] = tokenizer.tokenizePos(sentences[i]);
			}
		}
		return tokens;
	}

	
	}

