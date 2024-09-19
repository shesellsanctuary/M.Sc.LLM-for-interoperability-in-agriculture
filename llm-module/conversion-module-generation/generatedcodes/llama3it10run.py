import json
from shapely.geometry import Polygon

def main(json_input):
    # Load the input JSON data
    data = json.loads(json_input)

    # Create a list to store the converted features
    features = []

    for boundary in data["values"]:
        # Extract the necessary information from the boundary
        name = boundary["name"]
        source = "Rawdata"
        date = boundary["createdTime"]

        # Convert the multipolygons to Shapely Polygons
        polygons = []
        for multipolygon in boundary["multipolygons"]:
            polygon_points = [(point["lon"], point["lat"]) for point in multipolygon["rings"][0]["points"]]
            polygon = Polygon(polygon_points)
            polygons.append([list(p) for p in list(polygon.exterior.coords)])

        # Create the feature dictionary
        feature = {
            "type": "Feature",
            "properties": {
                "name": name,
                "type": "Feldweg",
                "source": source,
                "date": date
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": polygons
            }
        }

        # Add the feature to the list of features
        features.append(feature)

    # Create the FeatureCollection dictionary
    feature_collection = {
        "type": "FeatureCollection",
        "features": features
    }

    return json.dumps(feature_collection)

if __name__ == "__main__":
    main()