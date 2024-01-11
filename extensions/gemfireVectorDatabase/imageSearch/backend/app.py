from datetime import datetime
from flask import Flask, send_from_directory, request, jsonify, url_for
import embeddings_processor
import requests

app = Flask(__name__, static_url_path='', static_folder="../frontend/build")
app.config['IMAGES_FOLDER'] = 'static/images'
app.add_url_rule('/images/<path:filename>', endpoint='serve_images')
base_url = "http://localhost:8081/gemfire-vectordb/v1"


@app.route('/')
def index():
    return send_from_directory(app.static_folder, 'index.html')


@app.route('/createIndex', methods=['POST'])
def create_index():
    try:
        gemfire_create_index_api = base_url + '/indexes'
        headers = {'Content-Type': 'application/json'}

        response = requests.post(gemfire_create_index_api, headers=headers, json={"name":"image_search"})

        if response.status_code == 201:

            return jsonify({"status": "success", "message": "Successfully Created Index "})
        else:
            return jsonify({"error": f"Error: {response.status_code} - {response.text}"}), response.status_code

    except Exception as e:
        return jsonify({"error": str(e)})


@app.route('/loadEmbeddings', methods=['POST'])
def load_embeddings():
    try:
        embeddings_to_load = embeddings_processor.load_embeddings(app.config['IMAGES_FOLDER'])
        gemfire_bulk_load_api = base_url + '/indexes/image_search/embeddings'

        headers = {'Content-Type': 'application/json'}

        start_time = datetime.now()
        start_timestamp_string = start_time.strftime("%Y-%m-%d %H:%M:%S")

        print("Sending Request to GemFire: " + start_timestamp_string)
        response = requests.post(gemfire_bulk_load_api, headers=headers, json=embeddings_to_load)

        end_time = datetime.now()
        end_timestamp_string = end_time.strftime("%Y-%m-%d %H:%M:%S")
        print("Embeddings Loaded Into GemFire: " + end_timestamp_string)

        if response.status_code == 204:
            result = response.json()
            return jsonify(result)
        else:
            return jsonify({"error": f"Error: {response.status_code}"}), response.status_code

    except Exception as e:
        return jsonify({"error": str(e)})


@app.route('/images/<path:filename>')
def serve_images(filename):
    images_folder = app.config['IMAGES_FOLDER']
    return send_from_directory(images_folder, filename)


@app.route('/searchImages', methods=['POST'])
def search_images():
    try:
        data = request.get_json()
        search_query = data.get('searchQuery')
        search_query_embedding = embeddings_processor.create_query_embedding(search_query)

        gemfire_query_api = base_url + '/indexes/image_search/query'
        headers = {'Content-Type': 'application/json'}

        response = requests.post(gemfire_query_api, headers=headers, json=search_query_embedding)
        matching_images = response.json()

        images_to_return = []

        for image in matching_images:
            score = image.get('score')
            if score > 0.60:
                filename = image.get('key')
                images_to_return.append(url_for('serve_images', filename=filename))

        return jsonify(images_to_return)

    except Exception as e:
        return jsonify({"error": str(e)})


@app.route('/deleteIndex', methods=['DELETE'])
def delete_index():

    try:
        gemfire_delete_index_api = base_url + '/indexes/image_search'

        response = requests.delete(gemfire_delete_index_api)

        if response.status_code == 204:

            return jsonify({"status": "success", "message": "Successfully Deleted Index "})
        else:
            return jsonify({"error": f"Error: {response.status_code} - {response.text}"}), response.status_code

    except Exception as e:
        return jsonify({"error": str(e)})


if __name__ == '__main__':
    app.run(debug=True)
