package org.alfresco.po.share.site;

public enum ItemActions {
    DOWNLOAD("document-download", "Download"),
    VIEW_IN_BROWSER("document-view-content", "View In Browser"),
    VIEW_WORKING_COPY("document-view-working-copy", "View Working Copy"),
    VIEW_ORIGINAL("document-view-original", "View Original Document"),
    MOVE_TO("document-move-to", "Move to..."),
    COPY_TO("document-copy-to", "Copy to..."),
    DELETE_DOCUMENT("document-delete", "Delete Document"),
    EDIT_OFFLINE("document-edit-offline", "Edit Offline"),
    EDIT_IN_ALFRESCO("document-inline-edit", "Edit in Alfresco Share"),
    EDIT_IN_MICROSOFT_OFFICE("document-edit-online-aos", "Edit in Microsoft Office™"),
    EDIT_IN_GOOGLE_DOCS("google-docs-edit-action-link", "Edit in Google Docs™"),
    CANCEL_EDITING("document-cancel-editing", "Cancel Editing"),
    EDIT_PROPERTIES("document-edit-properties", "Edit Properties"),
    UPLOAD_NEW_VERSION("document-upload-new-version", "Upload New Version"),
    START_WORKFLOW("document-assign-workflow", "Start Workflow"),
    MANAGE_PERMISSIONS("document-manage-granular-permissions", "Manage Permissions"),
    MANAGE_REPO_PERMISSIONS("document-manage-repo-permissions", "Manage Permissions"),
    MANAGE_ASPECTS("document-manage-aspects", "Manage Aspects"),
    LOCATE_FILE("document-locate", "Locate File"),
    VIEW_ON_GOOGLE_MAPS("document-view-googlemaps", "View on Google Maps"),
    CHECK_IN_GOOGLE_DOC("TBD", "Check In Google Doc™"),
    CANCEL_EDITING_IN_GOOGLE_DOC("TBD", "Cancel Editing in Google Docs™"),

    LOCATE_LINKED_ITEM("document-locate", "Locate Linked Item"),
    DELETE_LINK("document-delete", "Delete Link"),
    EDIT_ACTION("edit-link", "Edit"),
    DELETE_ACTION("delete-link", "Delete"),
    LOCATE_FOLDER("document-locate", "Locate Folder"),
    DELETE_FOLDER("document-delete", "Delete Folder"),
    DOWNLOAD_AS_ZIP("folder-download", "Download as Zip"),
    VIEW_DETAILS("folder-view-details", "View Details"),
    MANAGE_RULES("folder-manage-rules", "Manage Rules");

    private String actionLocator;
    private String actionName;

    ItemActions(String actionLocator, String actionName) {
        this.actionLocator = actionLocator;
        this.actionName = actionName;
    }

    public String getActionLocator() {
        return actionLocator;
    }

    public String getActionName() {
        return actionName;
    }
}
