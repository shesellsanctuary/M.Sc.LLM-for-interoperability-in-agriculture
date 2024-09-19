import json

class BoundaryConverter:
    def __init__(self):
        self.feature_collection = {
            "type": "FeatureCollection",
            "features": []
        }

    def convert(self, input_data):
        boundaries = input_data.get("values", [])

        for boundary in boundaries:
            feature = {
                "type": "Feature",
                "properties": self._convert_properties(boundary),
                "geometry": self._convert_geometry(boundary)
            }
            self.feature_collection["features"].append(feature)

    def _convert_properties(self, boundary):
        return {
            "name": boundary.get("name", ""),
            "type": "Feldweg",
            "source": "Rawdata",
            "date": boundary["date"],
        }

    def _convert_geometry(self, boundary):
        exterior_ring = []

        for coord in boundary["geometry"]["coordinates"][0]:
            exterior_ring.append([coord[1], coord[0]])

        return {
            "type": "Polygon",
            "coordinates": [exterior_ring]
        }


def main(your_input_json_here):
    input_data = json.loads(your_input_json_here)
    converter = BoundaryConverter()
    converter.convert(input_data)

    return json.dumps(converter.feature_collection, indent=2)

if __name__ == "__main__":
    main()