import json

def main(json_data):
    """
    Converts JSON data to a GeoJSON FeatureCollection object.

    Args:
        json_data: JSON data to convert.

    Returns:
        GeoJSON FeatureCollection object.
    """

    # Parse JSON data
    data = json.loads(json_data)

    # Create FeatureCollection object
    feature_collection = {
        "type": "FeatureCollection",
        "features": []
    }

    # Iterate over boundaries in JSON data
    for boundary in data["values"]:
        # Create Feature object
        feature = {
            "type": "Feature",
            "properties": {
                "name": boundary["name"],
                "type": boundary["type"],
                "source": boundary["source"],
                "date": boundary["date"]
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": [
                    [
                        [boundary["topLeft"]["lon"], boundary["topLeft"]["lat"]],
                        [boundary["bottomRight"]["lon"], boundary["bottomRight"]["lat"]],
                        [boundary["bottomRight"]["lon"], boundary["topLeft"]["lat"]],
                        [boundary["topLeft"]["lon"], boundary["topLeft"]["lat"]]
                    ]
                ]
            }
        }

        # Add Feature to FeatureCollection
        feature_collection["features"].append(feature)

    return json.dumps(feature_collection)

if __name__ == "__main__":
    main()