package io.github.nickm980.smallville.math;

import com.robrua.nlp.bert.Bert;

public final class SmallvilleMath {

    private static Bert bert;

    private SmallvilleMath() {
    }

    private static Bert getBert() {
	if (bert == null) {
	    bert = Bert.load("com/robrua/nlp/easy-bert/bert-cased-L-12-H-768-A-12");
	}
	
	return bert;
    }
    
    public static void loadBert() {
	getBert();
    }
    /**
     * Normalizes a vector to a value between 0 and 1
     * 
     * @param value Any double
     * @return A value between 0 and 1
     */
    public static double normalize(double value, double max, double min) {
	return (value - min) / (max - min);
    }

    /**
     * Calculates the exponential decay
     * 
     * @param original     - the original value
     * @param changeInTime - the change in time
     * @return
     */
    public static double decay(double original, double changeInTime) {
	return original * Math.pow(1 - 0.99, changeInTime);
    }

    /**
     * Calculate the semantical sentence similarity by using bert token embeddings
     * and cosine similarity. If Settings.TOKEN_USAGE are set to high, then open
     * ai's token embedding API will be used for better memory retrieval but higher
     * costs.
     * <p>
     * Might eventually use the openai Embedding API for better results, this method
     * will probably be changed in the future because results aren't that great.
     * 
     * @param a String a to compare
     * @param b String b to compare
     * @return Normalized value between 0 and 1 where 1 is an identical
     */
    public static double calculateSentenceSimilarity(String a, String b) {
	if (a.isEmpty() || b.isEmpty()) {
	    return 0.0;
	}
	float[][] sequenceA = getTextEmbedding(a);
	float[][] sequenceB = getTextEmbedding(b);

	float[] embeddingA = getWeightedAverage(sequenceA);
	float[] embeddingB = getWeightedAverage(sequenceB);

	return cosineSimilarity(embeddingA, embeddingB);
    }

    private static float[] getWeightedAverage(float[][] sequence) {
	float[] weights = new float[sequence.length];
	for (int i = 0; i < sequence.length; i++) {
	    weights[i] = 1.0f - ((float) i / sequence.length);
	}
	float[] embedding = new float[sequence[0].length];
	float sum = 0.0f;
	for (int i = 0; i < sequence.length; i++) {
	    for (int j = 0; j < sequence[i].length; j++) {
		embedding[j] += weights[i] * sequence[i][j];
		sum += weights[i];
	    }
	}
	for (int j = 0; j < embedding.length; j++) {
	    embedding[j] /= sum;
	}
	return embedding;
    }

    /**
     * Uses bert to get the token embedding representation. I'm not sure why it
     * returns a float[][] instead of a float[] so this is something that needs to
     * be looked into because it's essential for memory retrieval
     * 
     * @param input
     * @return
     */
    public static float[][] getTextEmbedding(String input) {
	return getBert().embedTokens(input);
    }

    public static double cosineSimilarity(float[] vec1, float[] vec2) {
	double dotProduct = 0.0;
	double norm1 = 0.0;
	double norm2 = 0.0;
	for (int i = 0; i < vec1.length; i++) {
	    dotProduct += vec1[i] * vec2[i];
	    norm1 += vec1[i] * vec1[i];
	    norm2 += vec2[i] * vec2[i];
	}
	double cosineSimilarity = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
	return normalize(cosineSimilarity, 1, .8);
    }
}
