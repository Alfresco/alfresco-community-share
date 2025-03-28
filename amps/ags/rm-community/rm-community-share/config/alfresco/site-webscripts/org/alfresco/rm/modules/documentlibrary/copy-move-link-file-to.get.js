<import resource="classpath:/alfresco/site-webscripts/org/alfresco/rm/components/dashlets/rm-utils.js">
/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2025 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

function main() {
   model.isRmUser = "false";
   model.isAdmin = "false";
   model.rmSiteExists = "false";

   // Check if the user is an RM User
   var conn = remote.connect("alfresco");
   if (isRmUser(conn))
   {
      model.isRmUser = "true";
   }

   //Check if the user is an alfresco administrator
   if (isAdmin(conn))
   {
      model.isAdmin = "true";
   }

   if(isRmSite(conn, "rm"))
   {
      model.rmSiteExists ="true";
   }
}

main();
