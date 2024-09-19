from datetime import datetime
import os
import requests
import sys
from os import listdir, makedirs
import requests
from transformers import AutoTokenizer, AutoModel
from huggingface_hub import login
# NOTE: ollama must be running for this to work, start the ollama app or run `ollama serve`

modelsOnHuggingface = {
    "llama3:instruct": "meta-llama/Meta-Llama-3-8B-Instruct",
    "codegemma:instruct": "google/codegemma-7b-it",
    "mistral:instruct": "mistralai/Mistral-7B-Instruct-v0.3" # AGREE on https://huggingface.co/mistralai/Mistral-7B-Instruct-v0.3!
}

def analyseTokens(model_name, prompt):
    #print(f"Model name: {model_name}")
    #print(f"Prompt: {prompt}")
    tokenizer = AutoTokenizer.from_pretrained(model_name) # there is a way to fetch the config file without downloading the model but I always received a 401
    tokens = tokenizer.tokenize(prompt)
    num_tokens = len(tokens)
    print(f"Token count: {num_tokens}")
    
    max_tokens = get_max_tokens(model_name)
    print(f"Max tokens: {max_tokens}")
    if num_tokens > max_tokens:
        print("Prompt is too large!")
    else:
        print("Prompt is okay!")


def get_max_tokens(model_name):
    file_path = './iteration2/max_embeddings.txt'
    model_name_with_colon = f"{model_name}:"
    
    # Read the file and check if the entry exists
    with open(file_path, 'r') as file:
        lines = file.readlines()
    
    for line in lines:
        if line.startswith(model_name_with_colon):
            parts = line.split(":")
            if len(parts) > 1 and parts[1].strip():
                return int(parts[1].strip())
            else:
                break  # Break if the entry is found but no value is present
    
    # If entry does not exist or is empty, calculate max_tokens
    model = AutoModel.from_pretrained(model_name)
    max_tokens = model.config.max_position_embeddings
    
    # Update the file with the new max_tokens
    updated_lines = []
    for line in lines:
        if line.startswith(model_name_with_colon):
            updated_lines.append(f"{model_name}: {max_tokens}\n")
        else:
            updated_lines.append(line)
    
    with open(file_path, 'w') as file:
        file.writelines(updated_lines)
    
    return max_tokens

def main():

    # Set your Hugging Face token
    hf_token = "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"
    login(token=hf_token)

    model = sys.argv[1]
    learning_type = sys.argv[2]
    file = sys.argv[3] #1 - 100

    test_folder = './boundaries-tests/iteration_2/strategy_a'
    example_file_1 = open('./boundaries-examples/example-1-prompt.txt', 'r')
    example_file_2 = open('./boundaries-examples/example-2-prompt.txt', 'r')
    example_file_3 = open('./boundaries-examples/example-3-prompt.txt', 'r')
    prompt_file_1 = open('./prompts/boundaries_conversion_prompt1_it2.txt', 'r')
    prompt_file_2 = open('./prompts/boundaries_conversion_prompt2_it2.txt', 'r')
    init_example_file = open('./prompts/init_example_prompt_it2.txt', 'r')
    init_example = init_example_file.read()
    example_prompt_1 = example_file_1.read()
    example_prompt_2 = example_file_2.read()
    example_prompt_3 = example_file_3.read()
    direct_conversion_prompt1 = prompt_file_1.read()
    direct_conversion_prompt2 = prompt_file_2.read()
    example_file_1.close()
    example_file_2.close()
    example_file_3.close()
    prompt_file_1.close()
    prompt_file_2.close()
    init_example_file.close()

    few_shot_1_example = init_example + example_prompt_1
    few_shot_2_examples = init_example + example_prompt_1 + example_prompt_2
    few_shot_3_examples = init_example + example_prompt_1 + example_prompt_2 + example_prompt_3


    with open(f'{test_folder}/test-{file}.txt', 'r') as test_file:
        test_jd = test_file.read()
        test_prompt = direct_conversion_prompt1 + test_jd + direct_conversion_prompt2

    prompts = {
        "zero-shot": test_prompt,
        "few-shot-1": few_shot_1_example + test_prompt,
        "few-shot-2": few_shot_2_examples + test_prompt,
        "few-shot-3": few_shot_3_examples + test_prompt
        }
    
    prompt = prompts[learning_type]
    print(f'\n//////// Token analysis for {model} started {datetime.today().strftime("%Y-%m-%d %H:%M:%S")}')
    analyseTokens(modelsOnHuggingface[model], prompt)


if __name__ == "__main__":
    main()