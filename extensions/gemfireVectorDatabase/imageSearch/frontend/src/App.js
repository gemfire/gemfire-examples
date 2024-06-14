/*
Copyright 2019 - 2021 VMware, Inc.
SPDX-License-Identifier: Apache-2.0
*/

import React, {useState} from 'react';
import ImageSearchForm from "./components/ImageSearchForm";
import SearchResults from "./components/SearchResults";
import './styles/main.css';


const App = () => {
    const [searchResults, setSearchResults] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');

    const handleClearResults = () => {
        setSearchResults([]);
        setSearchQuery('');
    };
    const handleSearch = async (query) => {
        setSearchQuery(query);
    };



    return (
         <div className="App">
                <h1>GemFire Image Search</h1>
                <ImageSearchForm setSearchResults={setSearchResults} onClearResults={handleClearResults} onSearch={handleSearch}/>
                <SearchResults results={searchResults} onClearResults={handleClearResults} searchQuery={searchQuery}/>
         </div>
    );

};

export default App;
