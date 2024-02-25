import itertools
import numpy as np
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from sentence_transformers import SentenceTransformer

# Function to extract topic keywords from conversation content
def extract_topic_keywords(doc):
    n_gram_range = (1, 1)
    stop_words = "english"

    count = CountVectorizer(ngram_range=n_gram_range, stop_words=stop_words).fit([doc])
    candidates = count.get_feature_names_out()

    model = SentenceTransformer('distilbert-base-nli-mean-tokens')
    doc_embedding = model.encode([doc])
    candidate_embeddings = model.encode(candidates)

    def max_sum_sim(doc_embedding, candidate_embeddings, words, top_n, nr_candidates):
        distances = cosine_similarity(doc_embedding, candidate_embeddings)
        distances_candidates = cosine_similarity(candidate_embeddings, candidate_embeddings)

        words_idx = list(distances.argsort()[0][-nr_candidates:])
        words_vals = [candidates[index] for index in words_idx]
        distances_candidates = distances_candidates[np.ix_(words_idx, words_idx)]

        min_sim = np.inf
        candidate = None
        for combination in itertools.combinations(range(len(words_idx)), top_n):
            sim = sum([distances_candidates[i][j] for i in combination for j in combination if i != j])
            if sim < min_sim:
                candidate = combination
                min_sim = sim

        return [words_vals[idx] for idx in candidate][:top_n]

    topic_keywords = max_sum_sim(doc_embedding, candidate_embeddings, candidates, top_n=3, nr_candidates=10)
    return topic_keywords
