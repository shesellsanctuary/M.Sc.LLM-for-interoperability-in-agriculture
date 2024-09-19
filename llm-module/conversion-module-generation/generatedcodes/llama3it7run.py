import json
from geojson import FeatureCollection, Feature, Point, Polygon

def main(json_data):
    data = json.loads(json_data)
    feature_collection = FeatureCollection()
    
    for value in data["values"]:
        feature = Feature()
        
        # Set properties
        feature.properties = {
            "name": value["name"],
            "type": "Feldweg",
            "source": "Rawdata",
            "date": value["createdTime"]
        }
        
        # Set geometry
        rings = []
        for multipolygon in value["multipolygons"]:
            ring = []
            for point in multipolygon["rings"][0]["points"]:
                ring.append([point["lon"], point["lat"]])
            rings.append(ring)
        feature.geometry = Polygon(shell=rings[0], coordinates=rings)
        
        # Add feature to collection
        feature_collection.features.append(feature)
    
    return json.dumps(feature_collection)

if __name__ == "__main__":
    main()