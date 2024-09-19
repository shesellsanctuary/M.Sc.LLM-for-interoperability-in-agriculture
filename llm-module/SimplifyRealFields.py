import os
import json
import numpy as np

def load_geojson(filepath):
    with open(filepath, 'r') as f:
        return json.load(f)

def save_geojson(data, filepath):
    def convert_ndarray(obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        elif isinstance(obj, list):
            return [convert_ndarray(item) for item in obj]
        elif isinstance(obj, dict):
            return {key: convert_ndarray(value) for key, value in obj.items()}
        else:
            return obj

    data = convert_ndarray(data)
    
    with open(filepath, 'w') as f:
        json.dump(data, f)


def douglas_peucker(points, epsilon):
    def perpendicular_distance(point, line_start, line_end):
        if np.array_equal(line_start, line_end):
            return np.linalg.norm(point - line_start)
        else:
            return np.abs(np.cross(line_end - line_start, line_start - point)) / np.linalg.norm(line_end - line_start)

    def rdp(points, epsilon):
        dmax = 0.0
        index = 0
        for i in range(1, len(points) - 1):
            d = perpendicular_distance(points[i], points[0], points[-1])
            if dmax < d:
                index = i
                dmax = d
        if dmax > epsilon:
            results1 = rdp(points[:index+1], epsilon)
            results2 = rdp(points[index:], epsilon)
            return results1[:-1] + results2
        else:
            return [points[0], points[-1]]

    return rdp(points, epsilon)

def process_geojson_files(input_folder, output_folder, epsilon):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for filename in os.listdir(input_folder):
        if filename.endswith('.json'):
            filepath = os.path.join(input_folder, filename)
            geojson_data = load_geojson(filepath)
            
            geometry = geojson_data['geometry']
            if geometry['type'] == 'Polygon':
                coordinates = geometry['coordinates'][0]
                simplified_coords = douglas_peucker(np.array(coordinates), epsilon)
                geometry['coordinates'] = [simplified_coords]  # Direkt die Liste verwenden

            output_filepath = os.path.join(output_folder, filename)
            save_geojson(geojson_data, output_filepath)

def main(): 
    input_folder = './RealFieldsGeoJSON'
    output_folder = './SimpleRealFieldsGeoJSON'
    epsilon = 0.001  # tolerance
    process_geojson_files(input_folder, output_folder, epsilon)

if __name__ == "__main__":
    main()
