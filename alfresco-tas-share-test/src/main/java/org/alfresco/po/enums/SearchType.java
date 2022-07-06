package org.alfresco.po.enums;

public enum SearchType
{
    STORE_ROOT("storeroot"),
    NODEREF("noderef"),
    XPATH("xpath"),
    LUCENE("lucene"),
    FTS_ALFRESCO("fts-alfresco"),
    CMIS_STRICT("cmis-strict"),
    CMIS_ALFRESCO("cmis-alfresco"),
    DB_AFTS("db-afts"),
    DB_CMIS("db-cmis");

    private final String searchType;

    SearchType(String searchType)
    {
        this.searchType = searchType;
    }

    public String getSearchType()
    {
        return this.searchType;
    }
}

