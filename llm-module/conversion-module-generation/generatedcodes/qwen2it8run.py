import json
from shapely.geometry import Polygon

def main(json_data):
    # Parse the input JSON data
    input_data = json.loads(json_data)

    # Extract necessary information from the input JSON
    name = "ArablebleArea"
    type_ = "Feldweg"
    source = "Rawdata"
    date = "2021-05-25T11:25:45Z"

    boundaries_json = input_data["values"][0]["multipolygons"]
    if not isinstance(boundaries_json, list):
        # If there's only one boundary, make it a list for consistency
        boundaries_json = [boundaries_json]

    # Loop through the boundaries and create polygons from their coordinates
    features = []
    for boundary in boundaries_json:
        coordinates = []

        # Extract ring points and add them to the polygon coordinate list
        rings = boundary["rings"]
        for point in rings[0]["points"]:
            lon, lat = point["lon"], point["lat"]
            coordinates.append((lon, lat))

        # Create a Polygon from the extracted coordinates
        geometry = Polygon(coordinates)

        feature = {
            "type": "Feature",
            "properties": {
                "name": name,
                "type": type_,
                "source": source,
                "date": date,
            },
            "geometry": geometry,
        }
        features.append(feature)

    # Return the converted data as a FeatureCollection
    return json.dumps({"type": "FeatureCollection", "features": features})

if __name__ == "__main__":
    main()