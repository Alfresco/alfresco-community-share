package org.alfresco.po.share.user.admin;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claudia Agache on 7/1/2016.
 */
@PageObject
public class SitesManagerPage extends SharePage<SitesManagerPage> implements AccessibleByMenuBar {
	@Autowired
	private Toolbar toolbar;

	@Autowired
	private ListPagination listPagination;

	private List<ManagedSiteRow> managedSiteRows;

	@FindBy(css = "thead .label")
	private List<WebElement> tableHeadList;

	@RenderWebElement
	@FindBy(id = "DOCLIB_DOCUMENT_LIST")
	private WebElement sitesTable;

	@FindAll(@FindBy(css = "tr.alfresco-lists-views-layouts-Row"))
	private List<WebElement> siteRows;

	@FindAll(@FindBy(css = "div.dijitPopup[style*=visible] td.dijitMenuItemLabel"))
	private List<WebElement> dropdownOptionsList;

	private By siteRowName = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteName span.inner");
	private By siteRowDescription = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteDescription");
	private By siteRowVisibility = By.cssSelector("td.alfresco-lists-views-layouts-Cell.visibility table");
	private By successIndicator = By.cssSelector(".indicator.success");
	private By siteRowSiteManager = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteManager");
	private By siteRowActions = By.cssSelector("td.alfresco-lists-views-layouts-Cell.actions div.dijitPopupMenuItem");

	@Override
	public String getRelativePath() {
		return "share/page/console/sites-console/manage-sites";
	}

	@SuppressWarnings("unchecked")
	@Override
	public SitesManagerPage navigateByMenuBar() {
		toolbar.clickSitesManager();
		return (SitesManagerPage) renderedPage();
	}

	/**
	 * @return list of string representing the header of Site Manager table
	 */
	public ArrayList<String> getTableHeader() {
		ArrayList<String> tableHeaderText = new ArrayList<>();
		for (WebElement aTableHeadList : tableHeadList) {
			tableHeaderText.add(aTableHeadList.getText());
		}
		return tableHeaderText;
	}

	/**
	 * Get the available site rows
	 */
	private void loadManagedSiteRows() {
		this.managedSiteRows = new ArrayList<>();
		for (WebElement siteRow : siteRows) {
			ManagedSiteRow manageSiteRow = new ManagedSiteRow();
			manageSiteRow.setSiteName(siteRow.findElement(siteRowName));
			manageSiteRow.setSiteDescription(siteRow.findElement(siteRowDescription).getText());
			manageSiteRow.setSiteVisibilityText(siteRow.findElement(siteRowVisibility).getText());
			manageSiteRow.setSiteVisibilityButton(siteRow.findElement(siteRowVisibility));
			manageSiteRow.setUpdateVisibilitySuccessIndicator(siteRow.findElement(successIndicator));
			manageSiteRow.setSiteManager(siteRow.findElement(siteRowSiteManager).getText());
			manageSiteRow.setSiteActionsButton(siteRow.findElement(siteRowActions));
			this.managedSiteRows.add(manageSiteRow);
		}
	}

	/**
	 * Find a managed site row by name from paginated results.
	 *
	 * @param siteName
	 *            the required site name
	 * @return the managed site row
	 */
	public ManagedSiteRow findManagedSiteRowByNameFromPaginatedResults(String siteName) {
		do {
			loadManagedSiteRows();

			for (ManagedSiteRow row : managedSiteRows) {
				if (row.getSiteName().getText().equals(siteName))
					return row;
			}

			listPagination.clickNextButton();
		}

		while (listPagination.hasNextPage());
		return null;
	}

	/**
	 * Verify presence of site in Sites Manager table
	 *
	 * @param siteName
	 *            to be checked
	 * @return true if site is displayed, false otherwise
	 */
	public boolean isSiteDisplayed(String siteName) {
		ManagedSiteRow manageSiteRow = findManagedSiteRowByNameFromPaginatedResults(siteName);

		return manageSiteRow != null;
	}

	/**
	 * Get the actions list for the specified site
	 *
	 * @param siteName
	 *            String, the site for which actions list is load
	 * @return ManagedSiteRow object
	 */
	private ManagedSiteRow loadActionsForManagedSiteRow(String siteName) {
		ManagedSiteRow manageSiteRow = findManagedSiteRowByNameFromPaginatedResults(siteName);

		if (manageSiteRow != null) {
			manageSiteRow.getSiteActionsButton().click();
			manageSiteRow.setSiteActions(dropdownOptionsList);
		}

		return manageSiteRow;
	}

	/**
	 * Check if an action is listed in Actions list for the specified site
	 *
	 * @param siteName
	 *            String, the required site name
	 * @param action
	 *            String, the action searched in the Actions list
	 * @return true if the action is available for the site, false otherwise
	 */
	public boolean isActionAvailableForManagedSiteRow(String siteName, String action) {

		getBrowser().waitInSeconds(10);

		ManagedSiteRow manageSiteRow = loadActionsForManagedSiteRow(siteName);

		if (manageSiteRow != null) {
			for (WebElement siteAction : manageSiteRow.getSiteActions()) {
				if (action.equals(siteAction.getText()))
					return true;
			}
		}
		return false;
	}

	/**
	 * Click on the specified action from "Actions" site's list
	 *
	 * @param siteName
	 *            String, the required site name
	 * @param action
	 *            String, the action to be clicked
	 */
	public HtmlPage clickActionForManagedSiteRow(String siteName, String action, HtmlPage pageToBeRendered) {
		ManagedSiteRow manageSiteRow = loadActionsForManagedSiteRow(siteName);

		if (manageSiteRow != null) {
			for (WebElement siteAction : manageSiteRow.getSiteActions()) {
				if (action.equals(siteAction.getText())) {
					siteAction.click();
					break;
				}
			}
		}
		return pageToBeRendered.renderedPage();
	}

	private ManagedSiteRow loadSiteVisibilityOptionsForManagedSiteRow(String siteName) {
		ManagedSiteRow manageSiteRow = findManagedSiteRowByNameFromPaginatedResults(siteName);

		if (manageSiteRow != null) {
			manageSiteRow.getSiteVisibilityButton().click();
			manageSiteRow.setSiteVisibilityOptions(dropdownOptionsList);
		}

		return manageSiteRow;
	}

	/**
	 * Click on option from 'Visibility' dropdown
	 *
	 * @param siteName
	 *            site to be updated
	 * @param visibility
	 *            option to be chosen
	 */
	public void updateSiteVisibility(String siteName, String visibility) {
		ManagedSiteRow manageSiteRow = loadSiteVisibilityOptionsForManagedSiteRow(siteName);

		if (manageSiteRow != null) {
			browser.waitUntilElementsVisible(manageSiteRow.getSiteVisibilityOptions());
			for (WebElement siteVisibility : manageSiteRow.getSiteVisibilityOptions()) {
				if (visibility.equals(siteVisibility.getText())) {
					siteVisibility.click();
				}
			}
		}
	}

	/**
	 * Verify presence of success indicator on Site Visibility update
	 *
	 * @param siteName
	 *            updated site
	 * @return true if indicator is displayed
	 */
	public boolean isSuccesIndicatorDisplayed(String siteName) {
		ManagedSiteRow manageSiteRow = loadSiteVisibilityOptionsForManagedSiteRow(siteName);

		if (manageSiteRow != null) {
			browser.waitUntilElementVisible(manageSiteRow.getUpdateVisibilitySuccessIndicator());
			return browser.isElementDisplayed(manageSiteRow.getUpdateVisibilitySuccessIndicator());
		}

		return false;
	}

	/**
	 * Click on the site name link
	 *
	 * @param siteName
	 *            String, the required site name
	 */
	public void clickSiteNameLink(String siteName) {
		ManagedSiteRow manageSiteRow = findManagedSiteRowByNameFromPaginatedResults(siteName);
		if (manageSiteRow != null) {
			manageSiteRow.getSiteName().click();
		}
	}

	/**
	 * Check the value from I'm a Site Manager column
	 *
	 * @param siteName
	 *            String, the site for which user is or not a manager
	 * @return true if the value from I'm a Site Manager column is Yes, false
	 *         otherwise
	 */
	public boolean isUserSiteManager(String siteName) {
		ManagedSiteRow manageSiteRow = findManagedSiteRowByNameFromPaginatedResults(siteName);
		if (manageSiteRow != null) {
			if (manageSiteRow.getSiteManager().equals("Yes"))
				return true;
		}
		return false;
	}

	public String getSiteDescription(String siteName) {
		ManagedSiteRow manageSiteRow = findManagedSiteRowByNameFromPaginatedResults(siteName);
		if (manageSiteRow != null) {
			return manageSiteRow.getSiteDescription();
		}
		return siteName + " not found!";
	}

	public String getSiteVisibility(String siteName) {
		ManagedSiteRow manageSiteRow = findManagedSiteRowByNameFromPaginatedResults(siteName);
		if (manageSiteRow != null) {
			return manageSiteRow.getSiteVisibilityText();
		}
		return siteName + " not found!";
	}

	public boolean isSitesTableDisplayed() {
		browser.waitUntilElementIsDisplayedWithRetry(siteRowName);
		return browser.isElementDisplayed(sitesTable);
	}
}