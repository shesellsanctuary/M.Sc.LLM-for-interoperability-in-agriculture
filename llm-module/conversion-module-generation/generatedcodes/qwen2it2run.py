import json

def main(json_data):
    data = json.loads(json_data)
    # Extract the values from 'values' array in the input JSON data
    boundaries = data["values"]
    
    # Initialize an empty list for storing FeatureCollection features
    feature_collection = []
    
    for boundary in boundaries:
        # Create a new Feature object with properties and geometry populated accordingly
        feature_properties = {
            "name": boundary.get("name", ""),
            "type": "Feldweg",
            "source": "Rawdata",
            "date": boundary["createdTime"]
        }
        
        feature_geometry = None
        
        if "@type" in boundary:
            polygon_points = []

            points = boundary["@type"].get("points", [])
            
            for point_data in points:
                lat, lon = point_data.get("lat"), point_data.get("lon")
                polygon_points.append((lon, lat))
                
            feature_geometry = {
                "type": "Polygon",
                "coordinates": [polygon_points]
            }
        
        # Create and append a new Feature to the FeatureCollection
        if feature_geometry:
            feature_collection.append({
                "type": "Feature",
                "properties": feature_properties,
                "geometry": feature_geometry,
            })
    
    # Construct the FeatureCollection object from GeoJSON specification
    geojson_feature_collection = {
        "type": "FeatureCollection",
        "features": feature_collection,
    }
    
    return json.dumps(geojson_feature_collection)

if __name__ == "__main__":
    main()
