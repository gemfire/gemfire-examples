from datetime import datetime
from sentence_transformers import SentenceTransformer, util
from PIL import Image
import random
import pickle
import os
import zipfile
from tqdm.autonotebook import tqdm


PRE_COMPUTED_EMBEDDINGS_FOLDER = 'static/pre_computed_embeddings/'
PHOTO_FILENAME = 'unsplash-25k-photos.zip'
EMBEDDINGS_FILENAME = 'unsplash-25k-photos-embeddings.pkl'
PRE_COMPUTED_URL = 'http://sbert.net/datasets/' + EMBEDDINGS_FILENAME
PHOTO_URL = 'http://sbert.net/datasets/' + PHOTO_FILENAME


def load_embeddings(images_folder):
    embeddings_to_load = []
    use_precomputed_embeddings = True

    if use_precomputed_embeddings:
        try:
            download_images_if_not_present(images_folder)
        except Exception as e:
            print("An error occurred:", e)

        return create_embeddings_precomputed(embeddings_to_load)
    else:
        images = [os.path.join(images_folder, image) for image in
                  os.listdir(os.path.join(images_folder)) if
                  image.lower().endswith('.jpg')]
        random_images = random.sample(images, 100)

        start_time = datetime.now()
        print("Creating Embeddings: " + start_time.strftime("%Y-%m-%d %H:%M:%S"))

        for index, image in enumerate(random_images):
            vector = create_vector_from_image(image)
            image_embedding = {"key": os.path.basename(image), "vector": vector.tolist()}
            embeddings_to_load.append(image_embedding)

        end_time = datetime.now()
        print("Embeddings Created: " + end_time.strftime("%Y-%m-%d %H:%M:%S"))

        return embeddings_to_load


def download_images_if_not_present(images_folder):
    if not os.path.exists(images_folder) or len(os.listdir(images_folder)) == 0:
        os.makedirs(images_folder, exist_ok=True)

        photo_file_path = os.path.join(images_folder, PHOTO_FILENAME)

        if not os.path.exists(photo_file_path):
            util.http_get(PHOTO_URL, photo_file_path)

        with zipfile.ZipFile(photo_file_path, 'r') as zf:
            for image in tqdm(zf.infolist(), desc='Extracting'):
                zf.extract(image, images_folder)


def create_embeddings_precomputed(embeddings_to_load):
    precomputed_embeddings_file = os.path.join(PRE_COMPUTED_EMBEDDINGS_FOLDER, EMBEDDINGS_FILENAME)

    if not os.path.exists(precomputed_embeddings_file):
        util.http_get(PRE_COMPUTED_URL, precomputed_embeddings_file)
    try:
        with open(precomputed_embeddings_file, 'rb') as fIn:
            img_names, img_emb = pickle.load(fIn)
    except Exception as e:
        print("An error occurred:", e)

    print("Images:", len(img_names))

    for img_name, vector in zip(img_names, img_emb):
        image_embedding = {"key": os.path.basename(img_name), "vector": vector.tolist()}
        embeddings_to_load.append(image_embedding)

    return embeddings_to_load


def create_vector_from_image(image):
    model = SentenceTransformer('clip-ViT-B-32')
    image_vector = model.encode(Image.open(image))
    return image_vector


def create_query_embedding(search_query):
    model = SentenceTransformer('clip-ViT-B-32')
    query_vector = model.encode(search_query)
    query_embedding = {
        "vector": query_vector.tolist(),
        "top-k": 100,
        "k-per-bucket": 100,
        "include-metadata": False
    }

    return query_embedding
