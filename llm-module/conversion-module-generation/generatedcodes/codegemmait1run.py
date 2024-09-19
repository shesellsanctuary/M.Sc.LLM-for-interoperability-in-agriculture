import json

def main(json_data):
    """
    Converts a JSON object to a GeoJSON FeatureCollection.

    Args:
        json_data: A JSON object.

    Returns:
        A GeoJSON FeatureCollection object.
    """

    data = json.loads(json_data)
    
    # Get the values from the JSON object.
    features = data['values']

    # Create a GeoJSON Feature object for each feature in the JSON object.
    geojson_features = []
    for feature in features:
        geometry = feature['multiPolygon']
        properties = {
            'name': feature['name'],
            'type': feature['type'],
            'source': feature['source'],
            'date': feature['date']
        }
        geojson_features.append({
            'type': 'Feature',
            'properties': properties,
            'geometry': {
                'type': 'Polygon',
                'coordinates': [geometry]
            }
        })

    # Create a GeoJSON FeatureCollection object.
    geojson_feature_collection = {
        'type': 'FeatureCollection',
        'features': geojson_features
    }

    return json.dumps(geojson_feature_collection,indent=2)

if __name__ == "__main__":
    main()