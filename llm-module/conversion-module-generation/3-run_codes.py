from datetime import datetime
import progressbar
from os import listdir, makedirs, path, walk
from generatedcodes import codegemmait1run, codegemmait2run, codegemmait3run, codegemmait4run, codegemmait5run, codegemmait7run, codegemmait7run, codegemmait8run, codegemmait9run, codegemmait10run, llama3it1run, llama3it2run, llama3it3run, llama3it4run, llama3it5run, llama3it6run, llama3it7run, llama3it8run, llama3it9run, llama3it10run, qwen2it1run, qwen2it2run, qwen2it3run, qwen2it4run, qwen2it5run, qwen2it6run, qwen2it7run, qwen2it8run, qwen2it9run, qwen2it10run

models = {
    "codegemma-it": {1: codegemmait1run, 2: codegemmait2run, 3: codegemmait3run, 4: codegemmait4run, 5: codegemmait5run, 6: codegemmait7run, 7: codegemmait7run, 8: codegemmait8run, 9: codegemmait9run, 10: codegemmait10run },
    "llama3-it": {1: llama3it1run, 2: llama3it2run, 3: llama3it3run, 4: llama3it4run, 5: llama3it5run, 6: llama3it6run, 7: llama3it7run, 8: llama3it8run, 9: llama3it9run, 10: llama3it10run},
    "qwen2-it": {1: qwen2it1run, 2: qwen2it2run, 3: qwen2it3run, 4: qwen2it4run, 5: qwen2it5run, 6: qwen2it6run, 7: qwen2it7run, 8: qwen2it8run, 9: qwen2it9run, 10: qwen2it10run},
}

def main():
    
    bar = progressbar.ProgressBar(
        widgets = ['Progress: ', progressbar.Percentage(), ' ', progressbar.Bar(),' ', progressbar.Timer()],
        maxval=5000)

    real_fields_folder = '../SimpleRealFieldsJD'
    process = 0
    bar.start()
    for model in models.keys():
        for run, module in models[model].items():
            response_path = f'./{model}/real-run-{run}-responses'
            makedirs(response_path, exist_ok=True)
            print("MODEL: ", model)
            print("RUN: ", run)
            for test in listdir(real_fields_folder):
                with open(f'{real_fields_folder}/{test}', 'r') as test_file:
                    response_file_path = f'{response_path}/{test[:-4]}-response.txt'
                    if (hasattr(module, 'main')):
                        test_text = test_file.read()
                        try: 
                            # test_json = json.loads(test_text)
                            response = module.main(test_text)
                        except Exception as error:
                            print("\nAn exception occurred:", type(error).__name__, "–", error)
                            response = "failed to run"
                    else:
                        response = "no code"
                with open(response_file_path, "w") as f:
                    f.write(response)
                process += 1
        bar.update(process)
        print(f'\n//////// {model} code test complete {datetime.today().strftime("%Y-%m-%d %H:%M:%S")}')


if __name__ == "__main__":
    main()