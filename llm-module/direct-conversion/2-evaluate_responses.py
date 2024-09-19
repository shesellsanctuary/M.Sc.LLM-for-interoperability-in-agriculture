import json
from os import listdir
import geojson
import openpyxl
import re
import geojson_validation

def writeResultsToExcel(model, results, run):
    sheet = sheets[model]
    column = columns[f'{run}']
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


def extract_test_number(s):
    match = re.search(r'\d+', s)
    
    if match:
        return int(match.group())
    else:
        return None

runs = ["First Run", "Second Run", "Third Run"]

def evaluate_responses(path):
    evaluations = []
    for response in listdir(path):
        with open(f'{path}/{response}', "r") as f:
            response_json = f.read()
            test_number = extract_test_number(response)
            try:
                response_geojson = geojson.loads(response_json)
                if response_geojson.get("type") == "Feature":
                    response_geometry = response_geojson.get("geometry")
                    result = geojson_validation.is_valid(response_geometry)
                    if (result['valid'] == 'yes'):
                        # change folder for expected results
                        with open(f'../SimpleRealFieldsGeoJSON/Response ({test_number}).json', "r") as f:
                            expected_result = geojson.loads(f.read())
                            # use for .txt:
                            #expected_geometry = expected_result.features[0].geometry
                            # use for .json:
                            expected_geometry = expected_result.get("geometry")
                            print(expected_geometry)
                            if (is_same_geojson(response_geometry, expected_geometry)):
                                print(f'test {test_number}: 1 semantically correct \n')
                                evaluations.append({"test": test_number, "evaluation": 1})
                            else: 
                                print(f'test {test_number}: 0 only syntactically valid \n')
                                evaluations.append({"test": test_number, "evaluation": 0})
                    else: 
                        print(f'test {test_number}: -1 is NOT a valid GEOJSON \n')
                        evaluations.append({"test": test_number, "evaluation": -1})
                else: 
                    print(f'test {test_number}: -1 is NOT a valid GEOJSON \n')
                    evaluations.append({"test": test_number, "evaluation": -1})
                    
            except ValueError as e:
                print(f'test {test_number}: -1 is NOT a valid JSON \n')
                evaluations.append({"test": test_number, "evaluation": -1})
    return evaluations

""" Config parameters """
""" Model folder name respective to model name"""
models = {
    "llama3:instruct": "llama3-it",
    "codegemma:instruct": "codegemma-it",
    "qwen2:7b-instruct": "qwen2-it"
}
""" 
     """
# change accordingly
excell_file = "evaluation_direct_conversion.xlsx"
""" Excell sheet name respective to model """
sheets = {
    "llama3:instruct": "Llama3",
    "codegemma:instruct": "CodeGemma",
    "qwen2:7b-instruct": "Qwen2"
}
""" 
 """

""" Sheet column respective to learning type """
columns = {
    "First Run": 2,
    "Second Run": 3,
    "Third Run": 4
}

""" 
    Program to run evaluation on all model responses of the given learning type
    Example call:
        >> python evaluate_responses.py
"""
def main():

    for model in models.keys():
        # Change base path accordingly
        path = f'./{models[model]}'
        for run in runs:
            evaluations = evaluate_responses(f'{path}/{run}')
            writeResultsToExcel(model, evaluations, run)
        print(f'\n//////// Model {model} evaluation complete')

if __name__ == "__main__":
    main()