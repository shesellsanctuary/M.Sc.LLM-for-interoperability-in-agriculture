import json
from geojson import FeatureCollection, Feature, Polygon

def main(json_data):
    data = json.loads(json_data)
    # Extracting the 'values' key from the JSON input
    boundaries = data.get('values', [])
    
    feature_collection = []

    for boundary in boundaries:
        geometry = []
        coords = [[
            (point['lon'], point['lat']) 
            for point in polygon['rings'][0]['points']
        ] for polygon in boundary['multipolygons']]

        if coords:
            geometry.append(Polygon(coords))
        
        feature_properties = {
            'name': boundary.get('name', ''),
            'type': boundary.get('@type', '').split()[-1],  # type: Field
            'source': boundary.get('sourceType', ''),      
            'date': boundary.get('createdTime', '').replace('+00:00', '')  # Convert ISO format to Unix timestamp 
        }

        feature_collection.append(Feature(
            geometry=geometry[0] if geometry else None,
            properties=feature_properties
        ))

    return json.dumps(FeatureCollection(feature_collection))

if __name__ == "__main__":
    main()