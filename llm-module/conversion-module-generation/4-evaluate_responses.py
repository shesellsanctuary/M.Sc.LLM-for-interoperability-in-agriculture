import geojson
import geojson_validation
from os import listdir, makedirs, path, walk
import openpyxl 

def writeResultsToExcel(model, results, run):
    sheet = sheets[model]
    column = columns[str(run)]
    wb = openpyxl.load_workbook(excell_file)
    ws = wb[sheet]
    for test in results:
        cell = int(test["test"])+1
        ws.cell(row=cell, column=column, value = test['evaluation'])
    wb.save(excell_file)

def ordered(obj):
    if isinstance(obj, dict):
        return sorted((k, ordered(v)) for k, v in obj.items())
    if isinstance(obj, list):
        return sorted((ordered(x) for x in obj), key=str)
    else:
        return obj

def is_same_geojson(geo_1, geo_2):
    return ordered(geo_1) == ordered(geo_2)

def is_valid_geojson(resp_json):
    try:
        response_geojson = geojson.loads(resp_json)
        # print(json.dumps(response_geojson, indent=4))
        response_geometry = response_geojson["features"][0].get("geometry")
        result = geojson_validation.is_valid(response_geojson)
        if(result['valid'] == 'yes'):
            return response_geometry
        return False
    except:
        return False
    
def has_valid_geometry(geo):
    response_geometry = geojson.loads(geo).get("geometry")
    result = geojson_validation.is_valid(response_geometry)
    if (result['valid'] == 'yes'):
        return True
    else:
        return False
    
def extract_test_number(s):
    start = s.find('(') + 1
    end = s.find(')', start)
    if start == 0 or end == -1:
        return None
    return s[start:end]
    
def evaluate_responses(path):
    evaluations = []
    for response in listdir(path):
        with open(f'{path}/{response}', "r") as f:
            response_json = f.read()
            test_number = extract_test_number(response)
            if (response_json == "no code" ):
                evaluations.append({"test": test_number, "evaluation": -3})
                continue
            if (response_json == "failed to run"):
                evaluations.append({"test": test_number, "evaluation": -2})
                continue
            response_geometry = is_valid_geojson(response_json)
            if (response_geometry != False):
                # Get expected result file
                with open(f'../SimpleRealFieldsGeoJSON/Response ({test_number}).json', "r") as f:
                    expected_result = geojson.loads(f.read())
                    expected_geometry = expected_result.get("geometry")
                    # print("resp: ", json.dumps(response_geometry, indent=4))
                    # print("expe: ", json.dumps(expected_geometry, indent=4))
                    if (is_same_geojson(response_geometry, expected_geometry)):
                        print(f'test {test_number}: 1 semantically correct \n')
                        evaluations.append({"test": test_number, "evaluation": 1})
                    else: 
                        print(f'test {test_number}: 0 only syntactically valid \n')
                        evaluations.append({"test": test_number, "evaluation": 0})
            else: 
                print(f'test {test_number}: -1 is NOT a valid GEOJSON \n')
                evaluations.append({"test": test_number, "evaluation": -1})
    return evaluations

""" Config parameters """
""" Model folder name respective to model name"""
models = {
    "llama3:instruct": "llama3-it",
    "codegemma:instruct": "codegemma-it",
    "qwen2:7b-instruct": "qwen2-it"
}
excell_file = "evaluation_conversion_module.xlsx"

""" Excell sheet name respective to model """
sheets = {
    "llama3-it": "Llama3",
    "codegemma-it": "CodeGemma",
    "qwen2-it": "Qwen2"
}

""" Sheet column respective to learning type """
columns = {
    "1": 3,
    "2": 4,
    "3": 5,
    "4": 6,
    "5": 7,
    "6": 8,
    "7": 9,
    "8": 10,
    "9": 11,
    "10": 12,
}

""" 
    Program to run evaluation on all model responses
"""
def main():
    for model in models.values():
            for run in range(1,11):
                dir = f'./{model}/real-run-{run}-responses'
                if (path.isdir(dir)):
                    print("RUN: ", run, model)
                    resp_path = dir
                    evaluations = evaluate_responses(resp_path)
                    writeResultsToExcel(model, evaluations, run)
            print(f'\n//////// Model {model} evaluation complete')

if __name__ == "__main__":
    main()