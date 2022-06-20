package org.alfresco.share.userDashboard.dashlets;

import static org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import static org.alfresco.dataprep.DashboardCustomization.DashletLayout;

import org.alfresco.share.BaseTest;
import org.alfresco.utility.model.UserModel;

public abstract class AbstractUserDashboardDashletsTests extends BaseTest
{
    protected void addDashlet(UserModel userModel, UserDashlet dashlet, int columnNumber, int position)
    {
        getUserService().addDashlet(userModel.getUsername(),
            userModel.getPassword(),
            dashlet,
            DashletLayout.TWO_COLUMNS_WIDE_RIGHT,
            columnNumber,
            position);
    }
}
