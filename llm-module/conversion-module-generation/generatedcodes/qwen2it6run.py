import json

def main(json_data):
    data = json.loads(json_data)
    # Extracting values from JSON data and creating FeatureCollection structure.
    feature_collection = {
        "type": "FeatureCollection",
        "features": []
    }

    features = data['values']
    for feature in features:
        geometry_coords = []

        polygon_points = feature['multipolygons'][0]['rings'][0]['points']

        for point in polygon_points:
            lat, lon = point['lat'], point['lon']
            geometry_coords.append([lon, lat])

        feature_collection['features'].append({
            "type": "Feature",
            "properties": {
                "name": feature['name'],
                "type": feature['sourceType'] if 'sourceType' in feature else None,
                "date": feature['createdTime']
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": [geometry_coords]
            }
        })

    return json.dumps(feature_collection, indent=4)

if __name__ == "__main__":
    main()
