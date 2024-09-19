from datetime import datetime
import os
import requests
import sys
from os import listdir, makedirs
import progressbar
import numpy as np

models = {
    "llama3:instruct": "llama3-it",
    "codegemma:instruct": "codegemma-it",
    "qwen2:7b-instruct": "qwen2-it"
}

def generate(prompt, model, path, test, temperature):
    response_file_path = f'{path}/{test[:-5]}-response.txt'

    makedirs(f'{path}', exist_ok=True)
    if not os.path.exists(response_file_path):
        try:
            r = requests.post('http://localhost:11434/api/generate',
                            json={
                                "model": model,
                                "prompt": prompt,
                                "format": "json",
                                "stream": False,
                                })
            r.raise_for_status()

            with open(response_file_path, "w") as f:
                body = r.json()
                response = body['response']
                f.write(response)

                if 'error' in body:
                    raise Exception(body['error'])

                if body.get('done', False):
                    return body.get('context', "Context not provided")
                
        except requests.RequestException as e:
            print(f"Request failed: {e}")
        except Exception as e:
            print(f"An error occurred: {e}")
    else:
        print(f'File {response_file_path} already exists. Skipping...')

def is_run_complete(run_path):
    for i in range(1, 161):
        file_name = f'test-{i}-response.txt'
        file_path = os.path.join(run_path, file_name)
        if not os.path.exists(file_path):
            return False
    return True

def main():

    bar = progressbar.ProgressBar(
        widgets = ['Progress: ', progressbar.Percentage(), ' ', progressbar.Bar(), ' ', progressbar.Timer()],
        maxval=160)

    test_folder = '../SimpleRealFieldsJD'

    prompt_file_1 = open('../prompts/boundaries_conversion_prompt1.txt', 'r')
    prompt_file_2 = open('../prompts/boundaries_conversion_prompt2.txt', 'r')

    direct_conversion_prompt1 = prompt_file_1.read()
    direct_conversion_prompt2 = prompt_file_2.read()
   
    prompt_file_1.close()
    prompt_file_2.close()

    for model in models.keys():
        print(f'\n//////// Model {model} run started {datetime.today().strftime("%Y-%m-%d %H:%M:%S")}')
        makedirs(f'./{models[model]}', exist_ok=True)

        # Add subfolder for prompt size and temperature as needed in base_path
        base_path = f'{models[model]}'

        for run in range(1,4):
            path = f'{base_path}/{run}'
            if path is None:
                print('All tests have been completed (First Run, Second Run, Third Run).')
                return
                
            for test in listdir(test_folder):
                with open(f'{test_folder}/{test}', 'r') as test_file:
                    test_jd = test_file.read()
                    test_prompt = direct_conversion_prompt1 + test_jd + direct_conversion_prompt2
                process += 1
                generate(test_prompt, model, path, test)
                bar.update(process)
        bar.finish()
        print(f'\n//////// Model {model} run complete {datetime.today().strftime("%Y-%m-%d %H:%M:%S")}')


if __name__ == "__main__":
    main()