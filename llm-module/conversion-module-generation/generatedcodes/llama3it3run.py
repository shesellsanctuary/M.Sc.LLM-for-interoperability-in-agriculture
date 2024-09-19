import json
from geojson import Feature, FeatureCollection, Point, Polygon, GeometryCollection

def main(json_data):
    # Load JSON data
    data = json.loads(json_data)

    # Initialize FeatureCollection and Feature
    feature_collection = FeatureCollection()
    feature = None

    # Iterate over the values array
    for boundary in data["values"]:
        # Create a new Feature for each boundary
        feature = Feature(
            type="Feature",
            properties={
                "name": boundary["name"],
                "type": "Feldweg",  # hardcoded for now, you can modify this logic
                "source": "Rawdata",
                "date": boundary["createdTime"]
            },
            geometry=Polygon()
        )

        # Iterate over the multipolygons array and extract points
        multipolygon = boundary["multipolygons"][0]
        ring = multipolygon["@type"] == "Ring"
        for point in multipolygon["rings"][0]["points"]:
            feature.geometry.coordinates.append([point["@type"] == "Point" and [point["lon"], point["lat"]] or None])

        # Add the Feature to the FeatureCollection
        feature_collection.features.append(feature)

    return json.dumps(feature_collection.__dict__)

if __name__ == "__main__":
    main()
    