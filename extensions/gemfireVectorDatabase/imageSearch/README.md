# GemFire Image Search
An image search webapp that uses Tanzu GemFire and the Tanzu GemFire Vector Database extension.
Download the extension from support.broadcom.com: [GemFire Vector Database extension](https://support.broadcom.com/group/ecx/productfiles?subFamily=VMware%20Tanzu%20GemFire%20Vector%20Database&displayGroup=VMware%20Tanzu%20GemFire%20Vector%20Database&release=1.2.0&os=&servicePk=530223&language=EN)

## Using the Application

1. Click "Create Index"
2. Click "Load Embeddings" the application will then 
   3. Determine if the `/static/images` directory exists in the `backend` directory and if there are any files in that directory.
         2. If the directory **DOES NOT** exist, the application will create the directory, download the images dataset (~1.9 GB), unzip the file, and extract the images to the directory (~25,000 images)
         3. If the directory **DOES** exist, then it will skip the above step.
   2. Determine if the precomputed embeddings file exists. If it does not, then it will download the file.
   3. The precomputed embeddings are then loaded into GemFire.

3. Now that the index has been created, images are available, and the embeddings have been loaded into GemFire, you can search for images.

The full image set can also be downloaded here if needed:
[Image Data Set](https://public.ukp.informatik.tu-darmstadt.de/reimers/sentence-transformers/datasets/unsplash-25k-photos.zip)

---
If you would like to use your own images data set:
  - The images must be placed in the `backend/static/images` directory.
  - The images must be `.jpg` files.
  - You must set `use_precomputed_embeddings` (~line 20) in the `embeddings_processor.py` to **False**. 
    - The application will randomly select and encode 100 images from this folder. This is only because the encoding takes time and memory. You can adjust this number if needed in the `embeddings_processor.py` file, in the `load_embeddings` method (~line 33).  
  - With 1500 images on a local machine it can   take about ~20 minutes to encode them. This number will vary for each user. 

When searching images 
- The application filters out results that have a score less than .60. This is set in the `app.py` file, in the `/searchImages` endpoint.
- The search query sets `top-k:100` - this means it will return the top 100 results from the query. This can be changed in the `embeddings_processor.py` -> `def create_query_embedding(search_query):` 
 
## Running the Application

### Host and Port

The application currently expects to find the GemFire `--http-service-port`  at **8081** and running at `localhost`. If you change this port, you will need to update the `baseUrl` in the `app.py` file with the new host and port.

## Requirements

- [GemFire 10.0+](https://support.broadcom.com/group/ecx/productfiles?subFamily=VMware%20Tanzu%20GemFire&displayGroup=VMware%20Tanzu%20GemFire&release=10.2.0&os=&servicePk=536435&language=EN)
- [GemFire Vector Database extension 1.2+](https://support.broadcom.com/group/ecx/productfiles?subFamily=VMware%20Tanzu%20GemFire%20Vector%20Database&displayGroup=VMware%20Tanzu%20GemFire%20Vector%20Database&release=1.2.0&os=&servicePk=530223&language=EN)
- React
- Flask (python webserver)
- Python 3.9+
- Node 18+
- npm

There are a few python packages you may need to install such as:
- Sentence Transformer (pip install -U sentence-transformers)
- pytorch (pip3 install torch torchvision)
- PIL
- There may be others. You'll have to build and see what you have or don't have on your own system.

### Starting GemFire
- Download the Tanzu GemFire Vector Database extension from support.broadcom.com and put the `.gfm` file into the GemFire extensions directory.
- Start a GemFire Shell (gfsh)
- In the GemFire Shell, start a GemFire locator

  `start locator`
- In the GemFire Shell, start a GemFire server, turning on the rest API and setting the HTTP Port   
    `start server --start-rest-api --http-service-port=8081 --name=gemfire-image-search`

### Starting The Web Application
- Open a terminal.
- Navigate to the `frontend` directory
- Run `npm install` (You may end up with an output after the installation of - "8 vulnerabilities (2 moderate, 6 high)"
  ) 
- Run `npm run build`
- Navigate to the `backend` directory
- Run `python app.py`
- The application should be running now. See the top of this ReadMe for instructions on how to use the application.


