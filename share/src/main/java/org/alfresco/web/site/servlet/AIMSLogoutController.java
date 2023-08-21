/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site.servlet;

import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.mvc.LogoutController;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AIMSLogoutController extends AbstractController
{
    protected AIMSConfig config;
    protected LogoutController logoutController;
    private ApplicationContext applicationContext;
    private AIMSLogoutHandler aimsLogoutHandler;
    /**
     *
     * @param config
     */
    public void setConfig(AIMSConfig config) { this.config = config; }

    /**
     *
     * @param logoutController
     */
    public void setLogoutController(LogoutController logoutController) { this.logoutController = logoutController; }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        if (config.isEnabled())
        {
            // Handle callback from Identity Service
            if (request.getParameter("success") != null)
            {
                // Do the Share logout
                logoutController.handleRequestInternal(request, response);

                doRedirect(response, request.getContextPath());
            }
            else
            {
                this.applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(super.getServletContext());
                this.aimsLogoutHandler = this.applicationContext.getBean(AIMSLogoutHandler.class);
                // Redirect the user to Identity Service logout endpoint
                HttpSession session = request.getSession(false);
                if (session != null)
                {
                    String userId = (String) session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
                    if (userId != null)
                    {
                        SecurityContext account = (SecurityContext) session
                            .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

                        // Build the url for Identity Service Front-Channel logout
                        if (account != null)
                            try {
                                aimsLogoutHandler
                                    .handle(request, response, account.getAuthentication());
                            } catch (IOException | ServletException e) {
                                throw new RuntimeException(e);
                            }
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @param response
     * @param location
     */
    protected void doRedirect(HttpServletResponse response, String location)
    {
        response.setStatus(301);
        response.setHeader("Location", location);
        response.setHeader("Cache-Control", "max-age=0");
    }
}
