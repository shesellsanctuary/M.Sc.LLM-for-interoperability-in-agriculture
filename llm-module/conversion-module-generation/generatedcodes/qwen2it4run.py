import json

class Boundary:
    def __init__(self, id, name, source_type, created_time, modified_time,
                 area, workable_area, multipolygons):
        self.id = id
        self.name = name
        self.sourceType = source_type
        self.createdTime = created_time
        self.modifiedTime = modified_time
        self.area = area
        self.workableArea = workable_area
        self.multipolygons = multipolygons

    def to_feature_collection(self):
        coordinates = []
        for polygon in self.multipolygons:
            points = polygon["rings"][0]["points"]
            coords = [(point['lat'], point['lon']) for point in points]
            coordinates.append(coords)

        feature = {
            "type": "Feature",
            "properties": {
                "name": self.name,
                "type": "Feldweg",
                "source": "Rawdata",
                "date": self.modifiedTime
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": [coordinates]
            }
        }

        return feature

def main(input_json):
    input_data = json.loads(input_json)

    id = input_data["id"]
    name = input_data.get("name", "")
    source_type = input_data["sourceType"]
    created_time = input_data["createdTime"]
    modified_time = input_data["modifiedTime"]

    area_input = input_data["area"]
    workable_area_input = input_data["workableArea"]

    area = {
        "@type": "Extent",
        "topLeft": {"lat": area_input["topLeft"]["lat"], "lon": area_input["topLeft"]["lon"]},
        "bottomRight": {"lat": area_input["bottomRight"]["lat"], "lon": area_input["bottomRight"]["lon"]}
    }

    workable_area = {
        "@type": "Extent",
        "topLeft": {"lat": workable_area_input["topLeft"]["lat"], "lon": workable_area_input["topLeft"]["lon"]},
        "bottomRight": {"lat": workable_area_input["bottomRight"]["lat"], "lon": workable_area_input["bottomRight"]["lon"]}
    }

    multipolygons = []
    for polygon in input_data["multipolygons"]:
        exterior_points = [tuple(point) for point in polygon["rings"][0]["points"]]
        passable = polygon["type"] == "exterior"
        exterior = {
            "coordinates": [exterior_points],
            "passable": passable,
            "type": "Polygon",
            "type": "exterior"
        }
        multipolygons.append(exterior)

    boundary_data = Boundary(id, name, source_type, created_time, modified_time, area, workable_area, multipolygons)
    return json.dumps(boundary_data.to_feature_collection())

if __name__ == "__main__":
    main()