/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.TemporaryMultilingualAspectTest;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebRootModelTest;
import org.alfresco.module.org_alfresco_module_wcmquickstart.rendition.RenditionTest;
import org.alfresco.module.org_alfresco_module_wcmquickstart.webscript.WebscriptTest;

/**
 * WCM QS Module Test Suite
 * 
 * @author Roy Wetherall
 */
public class WCMQuickStartModuleTestSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return  the test suite
     */
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(WebRootModelTest.class);
        suite.addTestSuite(TemporaryMultilingualAspectTest.class);
        suite.addTestSuite(WebscriptTest.class);
        suite.addTestSuite(RenditionTest.class);
        return suite;
    }
}
