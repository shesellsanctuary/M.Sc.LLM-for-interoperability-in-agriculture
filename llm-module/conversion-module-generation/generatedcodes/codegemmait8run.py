import json

def main(data):
    """
    Converts JSON data to schema format.

    Args:
        json_data: JSON data in dictionary format.

    Returns:
        Schema data in dictionary format.
    """
    json_data = json.loads(data)
    
    features = []
    for feature in json_data['values']:
        properties = {
            "name": feature['name'],
            "type": feature['type'],
            "source": feature['source'],
            "date": feature['date']
        }

        coordinates = []
        for polygon in feature['multiPolygon']:
            coordinates.append([point['coordinates'] for point in polygon])

        geometry = {
            "type": "Polygon",
            "coordinates": coordinates
        }

        features.append({
            "type": "Feature",
            "properties": properties,
            "geometry": geometry
        })

    schema_data = {
        "type": "FeatureCollection",
        "features": features
    }

    return json.dumps(schema_data)

if __name__ == "__main__":
    main()
