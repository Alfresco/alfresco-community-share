package org.alfresco.share.adminTools.groups;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.GroupsPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;

/**
 * @author Bogdan Bocancea
 */
public class GroupsTests extends BaseTest
{
    private GroupsPage groupsPage;

    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String c9471group = "_C9471group" + uniqueIdentifier;
    private final String c9471group1 = "_C9471groupS" + uniqueIdentifier;
    private final String c9473ParentGroup1 = "c9473ParentGroup1" + RandomData.getRandomAlphanumeric();
    private final String c9473ParentGroup2 = "c9473ParentGroup2" + RandomData.getRandomAlphanumeric();
    private final String c9473ParentGroup3 = "c9473ParentGroup3" + RandomData.getRandomAlphanumeric();
    private final String c9473SubGroup = "C9473Group" + RandomData.getRandomAlphanumeric();

    private GroupModel createGroup, deleteGroup, editGroup, searchGroup, searchGroup1,
                       parent1, parent2, parent3, subGroup;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        createGroup = new GroupModel(RandomData.getRandomAlphanumeric());
        searchGroup = new GroupModel(c9471group);
        searchGroup1 = new GroupModel(c9471group1);
        parent1 = new GroupModel(c9473ParentGroup1);
        parent2 = new GroupModel(c9473ParentGroup2);
        parent3 = new GroupModel(c9473ParentGroup3);
        subGroup = new GroupModel(c9473SubGroup);

        deleteGroup = dataGroup.usingAdmin().createRandomGroup();
        editGroup = dataGroup.createRandomGroup();
        asList(subGroup, parent1, parent2, parent3, searchGroup, searchGroup1).forEach
            (group -> dataGroup.createGroup(group));
        dataGroup.addGroupToParentGroup(parent1, subGroup);
        dataGroup.addGroupToParentGroup(parent2, subGroup);
        dataGroup.addGroupToParentGroup(parent3, subGroup);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        groupsPage = new GroupsPage(browser);
        setupAuthenticatedSession(getAdminUser());
        groupsPage.navigate();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        asList(createGroup, deleteGroup, editGroup, searchGroup, searchGroup1, parent1, parent2, parent3, subGroup)
            .forEach(group -> dataGroup.usingAdmin().deleteGroup(group));
    }

    @TestRail (id = "C9462")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteGroup()
    {
        groupsPage.clickBrowse()
            .delete(deleteGroup).assertGroupIsNotDisplayed(deleteGroup);
    }

    @TestRail (id = "C9473")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.ADMIN_TOOLS })
    public void deleteGroupWithMultipleParents()
    {
        groupsPage.writeInSearchInput("C9473ParentGroup")
            .clickSearch()
            .clickBrowse()
            .selectGroup(c9473ParentGroup1)
            .assertColumnContainsGroup(2, c9473SubGroup)
            .clickDelete(c9473SubGroup)
                .assertDialogHeaderIsCorrect()
                .assertDialogMessageIsCorrect(c9473SubGroup)
                .assertParentsAre(c9473ParentGroup1, c9473ParentGroup2, c9473ParentGroup3)
                .assertRemoveRadioButtonIsDisplayed()
                .assertDeleteRadioButtonIsDisplayed()
                .assertDeleteButtonIsDisplayed()
                .assertCancelButtonIsDisplayed()
                .assertRemoveRadioButtonIsSelected()
                .assertDeleteRadioButtonIsNotSelected()
                .assertJustDeleteGroupLabelIsCorrect(subGroup, parent1)
                .assertRemoveGroupFromAllLabelIsCorrect(subGroup)
                .selectRemoveFromGroupRadio()
                .clickDelete()
                .assertDeleteInfoMessageIsDisplayed(c9473SubGroup);

        groupsPage.clickBrowse().selectGroup(c9473ParentGroup1)
            .assertColumnDoesNotContainsGroup(2, c9473SubGroup)
            .selectGroup(c9473ParentGroup2)
                .assertColumnContainsGroup(2, c9473SubGroup)
                .clickDelete(c9473SubGroup)
                .assertParentsAre(c9473ParentGroup2, c9473ParentGroup3)
                .selectDeleteGroupRadio()
                .clickDelete()
                .assertDeleteInfoMessageIsDisplayed(c9473SubGroup)
            .clickBrowse()
            .selectGroup(c9473ParentGroup3)
                .assertColumnDoesNotContainsGroup(2, c9473SubGroup);
    }

    @TestRail (id = "C9465")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editGroup()
    {
        String newDisplayName = RandomData.getRandomAlphanumeric();
        groupsPage.assertSectionTitleIsGroups()
            .clickBrowse().editGroup(editGroup, newDisplayName)
                .assertGroupIsNotDisplayed(editGroup);
        editGroup.setDisplayName(String.format("%s (%s)", newDisplayName, editGroup.getGroupIdentifier()));
        groupsPage.assertGroupIsDisplayed(editGroup);
    }

    @TestRail (id = "C9469")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createGroup()
    {
        groupsPage.clickBrowse()
            .clickCreateNewGroup(1).createGroup(createGroup)
                .assertGroupIsDisplayed(createGroup);
    }

    @TestRail (id = "C9471")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void searchForAGroup()
    {
        groupsPage.writeInSearchInput(c9471group)
            .clickSearch().waitUntilSearchElementDisplayed()
                .assertGroupIsFoundInSearch(searchGroup)
                .assertSearchBarTextIs(c9471group, 1);
        groupsPage.writeInSearchInput("*" + uniqueIdentifier)
            .clickSearch().waitUntilSearchElementDisplayed()
                .assertGroupIsFoundInSearch(searchGroup)
                .assertGroupIsFoundInSearch(searchGroup1);
    }
}
