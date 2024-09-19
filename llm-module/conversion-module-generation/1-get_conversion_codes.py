from datetime import datetime
import json
import os
import requests
import sys
from os import listdir, makedirs
import progressbar
# NOTE: ollama must be running for this to work, start the ollama app or run `ollama serve`

models = {
    "llama3:instruct": "llama3-it",
    "codegemma:instruct": "codegemma-it",
    "qwen2:7b-instruct": "qwen2-it",
}
     
def generate(prompt, context, model, path):
    response_file_path = f'{path}'
    r = requests.post('http://localhost:11434/api/generate',
                    json={
                        "model": model,
                        "prompt": prompt,
                        "stream": False})
    r.raise_for_status()
    with open(response_file_path, "w") as f:
        body = json.loads(r.text)
        response = body['response']
        f.write(response)

        if 'error' in body:
            raise Exception(body['error'])

        if body.get('done', False):
            return body.get('context', "Context not provided")

def main():

    bar = progressbar.ProgressBar(
        widgets = ['Progress: ', progressbar.Percentage(), ' ', progressbar.Bar(),' ', progressbar.Timer()],
        maxval=30)

    gen_file = open('../prompts/code_gen_prompt.txt', 'r')
    code_gen_prompt = gen_file.read()
    gen_file.close()

    context = [] # the context stores a conversation history, you can use this to make the model more context aware

    prompt = code_gen_prompt

    process = 0
    bar.start()
    for model in models.keys():
        
        makedirs(f'./{models[model]}', exist_ok=True)
        for i in range(1,11): 
            file_path = f'./{models[model]}/{i}-run'
            process += 1
            context = generate(prompt, context, model, file_path) # returns array of tokens
            bar.update(process)
        print(f'\n//////// Model {model} code complete {datetime.today().strftime("%Y-%m-%d %H:%M:%S")}')
    bar.finish()


if __name__ == "__main__":
    main()