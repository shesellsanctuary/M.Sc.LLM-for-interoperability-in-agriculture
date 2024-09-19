import json

def main(json_data):
    """
    Convert JSON data from the first format to the second format.

    :param json_data: The input JSON string in the old format.
    :return: A dictionary representing the feature collection with a single feature in the new schema format.
    """

    # Parse the input JSON
    input_json = json.loads(json_data)

    if not isinstance(input_json, dict) or 'values' not in input_json:
        raise ValueError("Input JSON data is not in the expected format")

    values = input_json['values']

    # Create a new feature collection with one feature based on the first entry
    feature_collection = {
        "type": "FeatureCollection",
        "features": [{
            "type": "Feature",
            "properties": {
                "name": f"ArablebleArea_{values[0]['id']}",
                "type": "Feldweg",
                "source": "Rawdata",
                "date": values[0]['modifiedTime']
            },
            "geometry": {
                "type": "Polygon",
                # Extracting coordinates from the first boundary
                "coordinates": [
                    [(coord['lon'], coord['lat']) for coord in boundary['exterior']['passable']]
                ]
            }
        }]
    }

    return json.dumps(feature_collection)

if __name__ == "__main__":
    main()
