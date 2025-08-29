package org.alfresco.share.adminTools.groups;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.GroupsPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang3.RandomStringUtils;
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

    private final String c9473ParentGroup1 = "c9473ParentGroup1" + RandomData.getRandomAlphanumeric();
    private final String c9473ParentGroup2 = "c9473ParentGroup2" + RandomData.getRandomAlphanumeric();
    private final String c9473ParentGroup3 = "c9473ParentGroup3" + RandomData.getRandomAlphanumeric();
    private final String c9473SubGroup = "C9473Group" + RandomData.getRandomAlphanumeric();

    private GroupModel subGroup;
    private GroupModel parent1;
    private GroupModel parent2;
    private GroupModel parent3;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        parent1 = new GroupModel(c9473ParentGroup1);
        parent2 = new GroupModel(c9473ParentGroup2);
        parent3 = new GroupModel(c9473ParentGroup3);
        subGroup = new GroupModel(c9473SubGroup);

        asList(subGroup, parent1, parent2, parent3).forEach
            (group -> dataGroup.createGroup(group));
        dataGroup.addGroupToParentGroup(parent1, subGroup);
        dataGroup.addGroupToParentGroup(parent2, subGroup);
        dataGroup.addGroupToParentGroup(parent3, subGroup);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        groupsPage = new GroupsPage(webDriver);
        authenticateUsingLoginPage(getAdminUser());
        groupsPage.navigate();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        asList(
             parent1,
            parent2, parent3, subGroup)
                .forEach(group -> dataGroup.usingAdmin().deleteGroup(group));
    }

    @TestRail (id = "C9462")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteGroup()
    {
        GroupModel deleteGroup = dataGroup.usingAdmin().createRandomGroup();
        groupsPage.navigate();
        groupsPage.clickBrowse()
            .delete(deleteGroup).assertGroupIsNotDisplayed(deleteGroup);
    }

    @TestRail (id = "C9473")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
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
            .assertColumnDoesNotContainsGroup(2, c9473SubGroup);

        groupsPage.clickBrowse()
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
        GroupModel editGroup = dataGroup.createRandomGroup();
        String newDisplayName = RandomData.getRandomAlphanumeric();

        groupsPage.navigate()
            .assertSectionTitleEquals(language.translate("adminTools.groups.title"))
            .clickBrowse().editGroup(editGroup, newDisplayName)
            .assertGroupIsNotDisplayed(editGroup);
        editGroup.setDisplayName(String.format("%s (%s)", newDisplayName, editGroup.getGroupIdentifier()));
        groupsPage.assertGroupIsDisplayed(editGroup);

        dataGroup.usingAdmin().deleteGroup(editGroup);
    }

    @TestRail (id = "C9469")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createGroup()
    {
        GroupModel createGroup = new GroupModel(RandomData.getRandomAlphanumeric());
        groupsPage.navigate().clickBrowse()
            .clickCreateNewGroup(1).createGroup(createGroup)
                .assertGroupIsDisplayed(createGroup);

        dataGroup.usingAdmin().deleteGroup(createGroup);
    }

    @TestRail (id = "C9471")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void searchForAGroup()
    {
        String uniqueIdentifier = RandomStringUtils.randomAlphanumeric(5);
        String c9471group = "_C9471group" + uniqueIdentifier;
        String c9471group1 = "_C9471group" + uniqueIdentifier;
        GroupModel searchGroup = new GroupModel(c9471group);
        GroupModel searchGroup1 = new GroupModel(c9471group1);

        dataGroup.usingAdmin().createGroup(searchGroup);
        dataGroup.usingAdmin().createGroup(searchGroup1);

        groupsPage.navigate()
            .writeInSearchInput(c9471group)
            .clickSearch().waitUntilSearchElementDisplayed()
                .assertGroupIsFoundInSearch(searchGroup)
                .assertSearchBarTextIs(c9471group, 1);
        groupsPage.writeInSearchInput("*" + uniqueIdentifier)
            .clickSearch().waitUntilSearchElementDisplayed()
                .assertGroupIsFoundInSearch(searchGroup)
                .assertGroupIsFoundInSearch(searchGroup1);

        dataGroup.usingAdmin().deleteGroup(searchGroup);
        dataGroup.usingAdmin().deleteGroup(searchGroup1);
    }
}
