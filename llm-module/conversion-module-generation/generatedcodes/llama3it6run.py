import json
from shapely.geometry import Polygon

def main(json_data):
    # Load the JSON data into a dictionary
    data = json.loads(json_data)

    # Create an empty FeatureCollection
    feature_collection = {
        "type": "FeatureCollection",
        "features": []
    }

    for boundary in data["values"]:
        # Extract relevant properties from the boundary object
        name = boundary["name"]
        type_ = "Feldweg"  # Assuming all boundaries are of this type
        source = "Rawdata"
        date = boundary["createdTime"]

        # Create a Feature object
        feature = {
            "type": "Feature",
            "properties": {
                "name": name,
                "type": type_,
                "source": source,
                "date": date
            }
        }

        # Extract the polygon coordinates from the multipolygons list
        multipolygons = boundary["multipolygons"][0]
        rings = multipolygons["rings"][0]
        points = rings["points"]
        coordinates = [[point["lon"], point["lat"]] for point in points]

        # Create a Polygon object using Shapely library
        polygon = Polygon(coordinates)

        # Add the geometry to the Feature object
        feature["geometry"] = {
            "type": "Polygon",
            "coordinates": [list(polygon.exterior.coords)]
        }

        # Add the Feature to the FeatureCollection
        feature_collection["features"].append(feature)

    return json.dumps(feature_collection, indent=4)


if __name__ == "__main__":
    main()
