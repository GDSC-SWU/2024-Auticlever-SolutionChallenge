import nltk
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
from transformers import T5ForConditionalGeneration, T5Tokenizer

nltk.download('punkt')

# Text summarization
class DialogueRequest(BaseModel):
    dialogue: str

class DialogueResponse(BaseModel):
    summarized_dialogue: str

# Initialize T5 model and tokenizer
model_t5 = T5ForConditionalGeneration.from_pretrained('t5-small')
tokenizer_t5 = T5Tokenizer.from_pretrained('t5-small')

# Initialize SentenceTransformer
model_st = SentenceTransformer('distilbert-base-nli-mean-tokens')

def summarize_dialogue(dialogue):
    # Split dialogue into sentences
    sentences = nltk.sent_tokenize(dialogue)
    summarized_sentences = []

    # Summarize each sentence
    for sentence in sentences:
        summarized_sentence = summarize_sentence(sentence)
        summarized_sentences.append(summarized_sentence)

    # Combine summarized sentences to generate overall dialogue summary
    summarized_dialogue = ' '.join(summarized_sentences)
    return summarized_dialogue

def summarize_sentence(sentence):
    # Preprocessing and processing required for summarizing the sentence
    prefix = "summarize: "
    inputs = [prefix + sentence]

    inputs = tokenizer_t5(inputs, max_length=512, truncation=True, return_tensors="pt")
    output = model_t5.generate(**inputs, num_beams=3, max_length=64, early_stopping=True)
    decoded_output = tokenizer_t5.batch_decode(output, skip_special_tokens=True)[0]
    summarized_sentence = nltk.sent_tokenize(decoded_output.strip())[0]

    return summarized_sentence

