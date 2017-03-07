package org.alfresco.po.renderer;

/**
 * Represents the element State.
 */
public enum ElementState
{
    DELETED_FROM_DOM(RenderDeleted.class),
    VISIBLE(RenderVisible.class),
    INVISIBLE(RenderInvisible.class),
    CLICKABLE(RenderClickable.class),
    PRESENT(RenderPresent.class),
    INVISIBLE_WITH_TEXT(RenderInvisibleWithText.class),
    PAGE_LOADED(RenderPageLoaded.class);

    private Class<?> clazzValue;

    private ElementState(Class<?> clazz)
    {
        this.clazzValue = clazz;
    }

    public Renderer toInstance()
    {
        Renderer newInstance = null;
        try
        {
            newInstance = (Renderer) clazzValue.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
        }
        return newInstance;
    }
}
