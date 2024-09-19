import json

def main(json_data):
    """
    Converts json data to schema.
    """
    # Parse json data
    data = json.loads(json_data)

    # Create schema
    schema = {
        "type": "FeatureCollection",
        "features": []
    }

    # Iterate over values
    for value in data["values"]:
        # Create feature
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

        # Add feature to schema
        schema["features"].append(feature)

    # Return schema
    return json.dumps(schema)

# Main function
if __name__ == "__main__":
    main()