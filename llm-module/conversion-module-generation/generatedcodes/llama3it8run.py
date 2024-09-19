import json
from geojson import FeatureCollection, Feature, Point, Polygon

def main(input_json):
    # Parse the input JSON
    data = json.loads(input_json)

    # Create a new GeoJSON FeatureCollection
    feature_collection = FeatureCollection()

    # Iterate over the values (features) in the input data
    for boundary in data["values"]:
        # Create a new GeoJSON Feature
        feature = Feature(type="Feature")

        # Set the properties of the feature
        feature.properties = {
            "name": boundary["name"],
            "type": "Feldweg",  # Hardcoded, adjust if needed
            "source": "Rawdata",
            "date": data["createdTime"]  # Use the created time from the input data
        }

        # Create a GeoJSON Polygon geometry
        polygon = Polygon()

        # Iterate over the multipolygons in the boundary
        for multipolygon in boundary["multipolygons"]:
            for ring in multipolygon["rings"]:
                points = []
                for point in ring["points"]:
                    points.append(Point(point["lon"], point["lat"]))
                polygon.add(ring, type="exterior", passable=True)

        # Add the geometry to the feature
        feature.geometry = polygon

        # Add the feature to the FeatureCollection
        feature_collection.features.append(feature)

    return json.dumps(feature_collection.to_json())

if __name__ == "__main__":
    main()