/*
 * Copyright 2019 - 2021 VMware, Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

import React, {useEffect, useState} from 'react';

const SearchResults = ({results, onClearResults, searchQuery}) => {
    const [showNoResults, setShowNoResults] = useState(false);


    useEffect(() => {
        setShowNoResults(results.length === 0);
    }, [results]);


    return (
        <div>
            <div className="ImagesDisplay__header-wrapper">
                <h2 data-qa="ImagesDisplay__header">Search Results</h2>
                <button
                    className="ImagesDisplay__destroy-session-button"
                    onClick={() => {
                        onClearResults();
                        setShowNoResults(false);

                    }}>
                    Clear Results
                </button>
            </div>
            {searchQuery && (
                <div className="search-query">
                    <p>Search query: "{searchQuery}"</p>
                </div>
            )}
            <div className="search-results">
                {
                    results.map((imageUrl, index) => (
                        <img key={index} src={imageUrl} alt={`Image ${index + 1}`} />
                    ))
                }
            </div>
        </div>
    );

};

export default SearchResults;

