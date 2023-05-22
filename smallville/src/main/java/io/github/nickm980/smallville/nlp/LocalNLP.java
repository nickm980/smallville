package io.github.nickm980.smallville.math;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class SentenceTokenizer {

    /**
     * Find the named entities (people and locations) from a text
     * 
     * @param text
     */
    public String getNamedEntities(String text) {
	Properties props = new Properties();
	props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
	StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	Annotation annotation = new Annotation(text);

	pipeline.annotate(annotation);

	List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	StringBuilder sb = new StringBuilder();
	for (CoreMap sentence : sentences) {
	    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
	    for (int i = 0; i < tokens.size(); i++) {
		String ner = tokens.get(i).get(CoreAnnotations.NamedEntityTagAnnotation.class);
		if (ner.equals("PERSON")) {
		    // Check if the current token is part of a multi-word named entity
		    if (i < tokens.size() - 1
			    && tokens.get(i + 1).get(CoreAnnotations.NamedEntityTagAnnotation.class).equals("PERSON")) {
			// Merge the current and next tokens into a single named entity
			sb.append("Name: ");
			while (i < tokens.size()
				&& tokens.get(i).get(CoreAnnotations.NamedEntityTagAnnotation.class).equals("PERSON")) {
			    sb.append(tokens.get(i).word() + " ");
			    i++; // Increment the index to skip the next token
			}
			sb.append("\n");
		    } else {
			sb.append("Name: " + tokens.get(i).word() + "\n");
		    }
		} else if (ner.equals("LOCATION")) {
		    sb.append("Location: " + tokens.get(i).word() + "\n");
		}
	    }
	}

	return sb.toString();
    }

    public String extractName(String observation) {
	String input = getNamedEntities(observation);

	String[] lines = input.split("\n");
	String result = null;

	for (int i = lines.length - 1; i >= 0; i--) {
	    if (lines[i].startsWith("Name:")) {
		result = lines[i].substring(6).trim();
		break;
	    }
	}

	return result;
    }
    
    public String convertToPastTense(String sentence) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Create an Annotation object with the input sentence
        Annotation document = new Annotation(sentence);

        // Run the pipeline on the Annotation object
        pipeline.annotate(document);

        // Get the list of sentences from the Annotation object
        CoreMap sentenceMap = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);
        // Get the parse tree of the sentence
        Tree tree = sentenceMap.get(TreeCoreAnnotations.TreeAnnotation.class);

        // Convert verbs to past tense
        convertVerbsToPastTense(tree);

        // Print the converted sentence
        return tree.toString();
    }

    private static void convertVerbsToPastTense(Tree tree) {
        if (tree.isLeaf()) {
            String word = tree.value();
            System.out.println(word + " word");
            String pos = tree.parent(tree).label().value();
            String pastTense = getPastTense(word, pos);
            if (pastTense != null) {
                tree.setValue(pastTense);
            }
        } else {
            for (Tree child : tree.children()) {
                convertVerbsToPastTense(child);
            }
        }
    }

    private static String getPastTense(String word, String pos) {
        if (pos.startsWith("VB")) {
            switch (pos) {
                case "VBD": // Past tense verb
                    return word;
                case "VBG": // Gerund or present participle verb
                    return word + "ed";
                case "VBN": // Past participle verb
                    return word;
                case "VBP": // Non-third person singular present tense verb
                case "VBZ": // Third person singular present tense verb
                    // Add 'ed' suffix to convert to past tense
                    return word + "ed";
            }
        }
        return null; // Not a verb or not supported
    }
}
