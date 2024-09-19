import json
from shapely.geometry import Polygon

def main(json_data):
    # Load the input JSON data
    data = json.loads(json_data)
    features = []
    for boundary in data["values"]:
        properties = {
            "name": boundary["name"],
            "type": "Feldweg",
            "source": "Rawdata",
            "date": boundary["createdTime"]
        }
        coordinates = []
        for multipolygon in boundary["multipolygons"]:
            for ring in multipolygon["rings"]:
                for point in ring["points"]:
                    coordinates.append([point["lon"], point["lat"]])
        geometry = {
            "type": "Polygon",
            "coordinates": [coordinates]
        }
        feature = {
            "type": "Feature",
            "properties": properties,
            "geometry": geometry
        }
        features.append(feature)
    return json.dumps({
        "type": "FeatureCollection",
        "features": features
    })


if __name__ == "__main__":
    main()