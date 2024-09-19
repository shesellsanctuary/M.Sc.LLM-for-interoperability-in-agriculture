import json

def convert_to_feature_collection(boundary):
    """
    Convert the given JSON object to feature collection format.
    """
    geometry = get_geometry(boundary)
    
    properties = {
        "name": boundary.get("name", ""),
        "type": _get_type(boundary),
        "source": _get_source(boundary),
        "date": boundary.get("createdTime", "")
    }
        
    return {
      'type': 'FeatureCollection',
      'features': [
        {
          'type': 'Feature',
          'geometry': geometry,
          'properties': properties
        }
      ]
    }

def get_geometry(boundary):
    """
    Extract and convert the boundary polygon data to GeoJSON format.
    """
    points = []
    
    for ring in boundary["multipolygons"][0]["rings"]:
        coordinates = [(point["lat"], point["lon"]) for point in ring["points"]]
        points.append(coordinates)
        
    return {
        'type': 'Polygon',
        'coordinates': [points]
    }

def _get_type(boundary):
    """
    Helper function to get the type of boundary.
    """
    types = ["Boundary", "Field", "Organization"]
    
    for link in boundary["links"]:
        if link["@type"] == "Link" and link.get("rel") == "field":
            return "Field"
            
    return None

def _get_source(boundary):
    """
    Helper function to get the source of raw data.
    """
    source = "Rawdata"

    for link in boundary["links"]:
        if link["@type"] == "Link" and link.get("rel") == "owningOrganization":
            break
    else:
        return source

    return None

def main(json_data):
    # Convert a sample JSON object to feature collection format.
    boundary = json.loads(json_data)
    
    return json.dumps(convert_to_feature_collection(boundary), indent=2)

if __name__ == "__main__":
    main()