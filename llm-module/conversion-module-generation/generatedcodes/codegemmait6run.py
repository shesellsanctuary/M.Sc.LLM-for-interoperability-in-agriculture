import json

def main(data):
    """
    Converts a JSON object to a GeoJSON FeatureCollection.

    Args:
        json_data: A JSON object.

    Returns:
        A GeoJSON FeatureCollection object.
    """

    json_data = json.loads(data)
    # Get the first value from the 'values' key.
    first_value = json_data['values'][0]

    # Create a GeoJSON Feature object.
    feature = {
        'type': 'Feature',
        'properties': {
            'name': first_value['name'],
            'type': first_value['type'],
            'source': first_value['source'],
            'date': first_value['date']
        },
        'geometry': {
            'type': 'Polygon',
            'coordinates': [
                [
                    [first_value['extent']['topLeft']['lon'], first_value['extent']['topLeft']['lat']],
                    [first_value['extent']['bottomRight']['lon'], first_value['extent']['bottomRight']['lat']],
                    [first_value['extent']['bottomRight']['lon'], first_value['extent']['topLeft']['lat']],
                    [first_value['extent']['topLeft']['lon'], first_value['extent']['topLeft']['lat']]
                ]
            ]
        }
    }

    # Create a GeoJSON FeatureCollection object.
    feature_collection = {
        'type': 'FeatureCollection',
        'features': [feature]
    }

    return json.dumps(feature_collection)

if __name__ == '__main__':
    main()