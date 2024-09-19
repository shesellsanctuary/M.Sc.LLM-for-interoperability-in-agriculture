import json
from geojson import Feature, FeatureCollection, LineString, Polygon, Point

def main(json_f):
    data = json.loads(json_f)

    feature_collection = FeatureCollection()

    for boundary in data['values']:
        feature = Feature()
        feature.properties = {
            "name": boundary["name"],
            "type": "Feldweg",
            "source": "Rawdata",
            "date": boundary["createdTime"]
        }

        multipolygons = boundary["multipolygons"][0]
        coordinates = []

        for ring in multipolygons["rings"]:
            points = []
            for point in ring["points"]:
                points.append((point["lon"], point["lat"]))
            coordinates.append(points)

        feature.geometry = Polygon(coordinates)
        feature_collection.features.append(feature)

    return json.dumps(feature_collection)

if __name__ == "__main__":
    main()
