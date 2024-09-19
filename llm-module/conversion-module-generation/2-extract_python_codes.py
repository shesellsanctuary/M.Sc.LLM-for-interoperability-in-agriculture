import os

models = {
    "llama3:instruct": "llama3-it",
    "codegemma:instruct": "codegemma-it",
    "qwen2:7b-instruct": "qwen2-it",
}

def get_python_code(file):
    generated_text = ""
    with open(file, "r") as f:
        write_flag = False
        for response_part in f.readlines():
            if '```' in response_part and write_flag:
                write_flag = False

            if write_flag:
                generated_text += response_part

            if '```python' in response_part:
                write_flag = True
    return generated_text.strip()

def main():
    
    for model in models.values():
        rootdir = f'./{model}'
        for file in os.listdir(rootdir):
            if (file == ".DS_Store"):
                    continue
            file_path = os.path.join(rootdir, file)
            print(file_path)
            if(os.path.isfile(file_path)):
                print(file_path.strip("./").replace("/", ""))
                code = get_python_code(file_path)
                with open(f'./generatedcodes/{file_path.strip("./,-").replace("/", "").replace("-", "")}.py', "w") as f:
                    f.write(code)

if __name__ == "__main__":
    main()