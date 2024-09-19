import json


def main(json_data):
    """
    Convert given JSON data to the specified feature collection schema.

    :param json_data: The input JSON as dictionary or string.
    :return: Dictionary in the desired schema.
    """

    if isinstance(json_data, str):
        # If provided as a string, load it as a dictionary
        json_data = json.loads(json_data)

    # Extracting fields from the 'values' array
    features = []
    for item in json_data.get("values", []):
        feature_properties = {
            "name": item.get("name"),
            "type": "Feldweg",
            "source": "Rawdata",
            "date": item.get("createdTime").split(".")[0]
        }
        
        # Get the first polygon from 'multipolygons' array
        poly_data = next((polys[0] for polys in item["multipolygons"] if "@type" in polys), None)
        
        if poly_data:
            coords = []

            lat, lon = zip(*[(point["lat"], point["lon"]) for ring in poly_data["rings"]
                             for point in ring["coordinates"][0]
                             if len(point) == 2])
            
            # Reverse the coordinates as they are in WGS84 (latitude, longitude)
            coords.append(list(zip(lon[::-1], lat[::-1])))

            feature_geometry = {
                "type": 'Polygon',
                "coordinates": [coords]
            }

            features.append({
                "type": 'Feature',
                "properties": feature_properties,
                "geometry": feature_geometry
            })
    
    return json.dumps({
        "type": "FeatureCollection",
        "features": features
    })

if __name__ == "__main__":
  main()
