import json
from shapely.geometry import Polygon

def main(json_data):
    data = json.loads(json_data)
    features = []
    for value in data["values"]:
        feature = {
            "type": "Feature",
            "properties": {
                "name": value["name"],
                "type": "Feldweg",
                "source": "Rawdata",
                "date": value["createdTime"]
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": []
            }
        }

        # Extract polygon coordinates from multipolygons
        for multipolygon in value["multipolygons"]:
            for ring in multipolygon["rings"]:
                points = [(point["lon"], point["lat"]) for point in ring["points"]]
                feature["geometry"]["coordinates"].append(points)

        features.append(feature)

    return json.dumps({
        "type": "FeatureCollection",
        "features": features
    })


if __name__ == "__main__":
    main()