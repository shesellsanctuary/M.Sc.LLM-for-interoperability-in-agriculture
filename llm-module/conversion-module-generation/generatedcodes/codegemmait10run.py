import json

def main(json_data):
    """
    Converts a JSON data to a GeoJSON schema.

    Args:
        json_data: The JSON data.

    Returns:
        A GeoJSON schema.
    """

    # Parse the JSON data.
    data = json.loads(json_data)

    # Create the GeoJSON schema.
    schema = {
        "type": "FeatureCollection",
        "features": []
    }

    # Iterate over the values in the JSON data.
    for value in data["values"]:
        # Create a GeoJSON feature.
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
                    [
                        [value["coordinates"][0]["longitude"], value["coordinates"][0]["latitude"]],
                        [value["coordinates"][1]["longitude"], value["coordinates"][1]["latitude"]],
                        [value["coordinates"][2]["longitude"], value["coordinates"][2]["latitude"]],
                        [value["coordinates"][3]["longitude"], value["coordinates"][3]["latitude"]],
                        [value["coordinates"][0]["longitude"], value["coordinates"][0]["latitude"]]
                    ]
                ]
            }
        }

        # Add the feature to the GeoJSON schema.
        schema["features"].append(feature)

    # Return the GeoJSON schema.
    return json.dumps(schema)

if __name__ == "__main__":
    main()