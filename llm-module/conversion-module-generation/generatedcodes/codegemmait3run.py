import json

def main(json_data):
    """
    Converts JSON data to schema.

    Args:
        json_data: JSON data to convert.

    Returns:
        Schema data.
    """

    # Load JSON data
    data = json.loads(json_data)

    # Create schema data
    schema_data = {
        "type": "FeatureCollection",
        "features": []
    }

    # Iterate over values in data
    for value in data["values"]:
        # Create feature data
        feature_data = {
            "type": "Feature",
            "properties": {
                "name": value["name"],
                "type": value["type"],
                "source": value["source"],
                "date": value["date"]
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": [value["coordinates"]]
            }
        }

        # Add feature data to schema data
        schema_data["features"].append(feature_data)

    # Return schema data
    return json.dumps(schema_data)

if __name__ == "__main__":
    main()
