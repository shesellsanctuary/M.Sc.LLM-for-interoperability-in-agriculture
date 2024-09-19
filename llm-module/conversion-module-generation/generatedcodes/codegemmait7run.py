import json

def main(json_data):
    # Load the JSON data
    data = json.loads(json_data)

    # Create the FeatureCollection object
    feature_collection = {
        "type": "FeatureCollection",
        "features": []
    }

    # Iterate over the values in the "values" list
    for value in data["values"]:
        # Create the Feature object
        feature = {
            "type": "Feature",
            "properties": {
                "name": value["name"],
                "type": value["type"],
                "source": value["source"],
                "date": value["date"]
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": [
                    value["coordinates"]
                ]
            }
        }

        # Add the Feature object to the FeatureCollection object
        feature_collection["features"].append(feature)

    # Return the FeatureCollection object
    return json.dumps(feature_collection)

if __name__ == "__main__":
    main()
