import json

def main(json_data):
    """
    Converts JSON data to a schema.

    Args:
        json_data: The JSON data to convert.

    Returns:
        The schema as a dictionary.
    """

    # Load the JSON data
    data = json.loads(json_data)

    # Create the schema
    schema = {
        "type": "FeatureCollection",
        "features": []
    }

    # Add the features
    for feature in data["values"]:
        schema["features"].append({
            "type": "Feature",
            "properties": {
                "name": feature["name"],
                "type": feature["type"],
                "source": feature["source"],
                "date": feature["date"]
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": [feature["coordinates"]]
            }
        })

    # Return the schema
    return json.dumps(schema)

if __name__ == "__main__":
    main()
