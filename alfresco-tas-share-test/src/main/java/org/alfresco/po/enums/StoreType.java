package org.alfresco.po.enums;

public enum StoreType
{
    ALFRESCO_USER_STORE("user://alfrescoUserStore"),
    SYSTEM("system://system"),
    LIGHT_WEIGHT_STORE("workspace://lightWeightVersionStore"),
    VERSION_2_STORE("workspace://version2Store"),
    ARCHIVE_SPACES_STORE("archive://SpacesStore"),
    WORKSPACE_SPACES_STORE("workspace://SpacesStore");

    private final String storeType;

    StoreType(String storeType)
    {
        this.storeType = storeType;
    }

    public String getStoreType()
    {
        return this.storeType;
    }
}
