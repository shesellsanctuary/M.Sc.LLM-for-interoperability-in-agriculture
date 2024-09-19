import json
from geojson import Feature, FeatureCollection, Polygon

def main(json_data):
    data = json.loads(json_data)
    features = []
    
    for boundary in data["values"]:
        feature_properties = {
            "name": boundary.get("name", ""),
            "type": "Feldweg",
            "source": "Rawdata",
            "date": boundary.get("createdTime", "")
        }
        
        geometry = create_polygon(boundary)
        if geometry:
            feature = Feature(
                type="Feature",
                properties=feature_properties,
                geometry=geometry
            )
            features.append(feature)

    return json.dumps(FeatureCollection(features))

def create_polygon(boundary):
    try:
        outer_ring = []
        for point in boundary["multipolygons"][0]["rings"][0]["points"]:
            lat, lon = point.get("lat"), point.get("lon")
            if lat is not None and lon is not None:
                outer_ring.append((lon, lat))
        
        if len(outer_ring) > 1: # Check that we have at least three points to form a polygon
            return Polygon([outer_ring])
    except (KeyError, IndexError):
        pass

    return None

if __name__ == "__main__":
    main()