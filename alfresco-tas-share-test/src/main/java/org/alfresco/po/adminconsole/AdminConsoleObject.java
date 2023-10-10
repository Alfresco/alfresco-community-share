package org.alfresco.po.adminconsole;

import org.openqa.selenium.WebElement;

/**
 * <div class="control text">
 * <span class="label">Allowed Users:</span>
 * <span class="value">
 * <span class="description">By default, this field is empty and all users can log in. Enter a comma-separated list of users to allow only those users to log
 * in. If a list is entered that does not contain the current user, the current user will be added automatically.</span>
 * </div>
 */
public class AdminConsoleObject implements ControlObject
{
    private String label;
    private WebElement value;
    private String description;
    private String type;

    public AdminConsoleObject(String label, WebElement value, String description, String type)
    {
        this.label = label;
        this.value = value;
        this.description = description;
        this.type = type;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String name = label.replace(":", "").toLowerCase().replaceAll(" ", "");

        String by = "undefined";
        String byValue = "";
        if (value != null)
        {
            if (!value.getAttribute("id").isEmpty())
            {
                by = "id";
                byValue = value.getAttribute("id");

            } else if (!value.getAttribute("name").isEmpty())
            {
                by = "name";
                byValue = value.getAttribute("name");
            }
        }
        sb.append("@FindBy(").append(by).append("=").append("\"").append(byValue).append("\"").append(")").append("\n").append("WebElement ").append(name)
          .append(";\n\n");
        return sb.toString();
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public WebElement getInput()
    {
        return value;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getType()
    {
        return type;
    }

}
