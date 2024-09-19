import json
from geojson import FeatureCollection, Feature, Point, Polygon

def main(json_data):
    # Load the input JSON data
    data = json.loads(json_data)

    # Create a new GeoJSON FeatureCollection
    feature_collection = FeatureCollection()

    # Iterate over the values in the input JSON
    for value in data["values"]:
        # Create a new GeoJSON Feature
        feature = Feature()
        feature.properties = {
            "name": value["name"],
            "type": "Feldweg",
            "source": "Rawdata",
            "date": value["createdTime"]
        }

        # Extract the multipolygons from the input JSON
        for multipolygon in value["multipolygons"]:
            # Create a new GeoJSON Polygon
            polygon = Polygon()
            coordinates = []
            for ring in multipolygon["rings"]:
                points = []
                for point in ring["points"]:
                    points.append([point["lon"], point["lat"]])
                coordinates.append(points)

            polygon.coordinates = [coordinates]

            # Set the geometry of the GeoJSON Feature
            feature.geometry = polygon

        # Add the GeoJSON Feature to the FeatureCollection
        feature_collection.features.append(feature)

    return json.dumps(feature_collection.__dict__)

# Example usage:
if __name__ == "__main__":
    main()