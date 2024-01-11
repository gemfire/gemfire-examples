/*
 * Copyright 2019 - 2021 VMware, Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

import React, {useState} from 'react';
import axios from "axios";


const ImageSearchForm = ({setSearchResults, onClearResults, onSearch}) => {
    const [searchQuery, setSearchQuery] = useState('');
    const [isLoadingEmbeddings, setIsLoadingEmbeddings] = useState(false);


    const handleSearch = async (event) => {
        event.preventDefault();
        onClearResults();

        if (!isLoadingEmbeddings) {
            try {
                const response = await fetch('/searchImages', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({searchQuery})
                });

                const result = await response.json();

                setSearchResults(result)
                onSearch(searchQuery)

            } catch (error) {
                console.error('Error fetching images: ', error);
            }

            setSearchQuery('');
        }

};
    const handleLoadEmbeddings = async () => {
        try{
            setIsLoadingEmbeddings(true)
            const response = await axios.post('/loadEmbeddings');
            console.log(response);
        } catch (error){
            console.error('Error loading embeddings:', error);
        }finally{
            setIsLoadingEmbeddings(false);
        }
    }

    const createIndex = async () => {
        try {

            const response = await axios.post('/createIndex');
            console.log(response.data);
        } catch (error) {
            console.error('Error creating index:', error);
        }
    }

    const deleteIndex = async () => {
        try {

            const response = await axios.delete('/deleteIndex');
            console.log(response.data);
        } catch (error) {
            console.error('Error deleting index:', error);
        }
    }



return (
   <form className="ImagesForm" onSubmit={handleSearch}>

         <input
             className="ImagesForm__image-input"
             type='text'
              value={searchQuery}
             onChange={(e) => setSearchQuery(e.target.value)}
             placeholder="Enter your search"
         />
         <button
          disabled={isLoadingEmbeddings}
          type="submit">
             Search Images
         </button>

       <button
          disabled={isLoadingEmbeddings}
          type="button"
          onClick={handleLoadEmbeddings}>
           Load Embeddings
       </button>
       <button
           disabled={isLoadingEmbeddings}
           type="button"
           onClick={createIndex}>
           Create Index
       </button>
       <button
           disabled={isLoadingEmbeddings}
           type="button"
           onClick={deleteIndex}>
           Delete Index and Data
       </button>
   </form>
);

}

export default ImageSearchForm;