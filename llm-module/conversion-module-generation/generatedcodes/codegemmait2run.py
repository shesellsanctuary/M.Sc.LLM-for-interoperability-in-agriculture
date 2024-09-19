import json

def main(json_data):
    """
    Converts JSON data to a FeatureCollection schema.

    Args:
        json_data: The JSON data to convert.

    Returns:
        A FeatureCollection schema.
    """

    # Parse the JSON data.
    data = json.loads(json_data)

    # Create the FeatureCollection schema.
    feature_collection = {
        "type": "FeatureCollection",
        "features": []
    }

    # Iterate over the values in the 'values' key of the JSON data.
    for value in data["values"]:
        # Create a Feature object.
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

        # Add the Feature object to the FeatureCollection schema.
        feature_collection["features"].append(feature)

    # Return the FeatureCollection schema.
    return json.dumps(feature_collection)

if __name__ == "__main__":
    main()
