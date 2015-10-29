/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.wcm.client;

import java.util.List;

/**
 * A class representing results of a search. 
 * @author Brian
 *
 */
public interface SearchResults
{
    /**
     * Obtain the list of results held by this object
     * @return List<SearchResult>
     */
    List<SearchResult> getResults();
    
    /**
     * Obtain the total results count.
     * This is the total number of results that the query returned before any pagination filters were applied.
     * @return long
     */
    long getTotalSize();
    
    /**
     * Obtain the number of results held by this object.
     * @return long
     */
    long getSize();

    /**
     * Obtain the query that was executed to return these results.
     * @return Query
     */
    Query getQuery();
}
