/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nelson Silva
 */
package org.nuxeo.functionaltests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.nuxeo.functionaltests.contentView.ContentViewElement.ResultLayout;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.functionaltests.contentView.ContentViewElement;
import org.nuxeo.functionaltests.pages.DocumentBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Nuxeo Spreadsheet Tests
 *
 * @since 7.1
 */
public class ITSpreadsheetTest extends AbstractTest {

    private final static String SPREADSHEET_ACTION_TITLE = "Spreadsheet";

    private final static String[] STANDALONE_COLUMNS = new String[] {
       "Title", "Author", "Description", "Modified", "Nature", "Subjects"
    };

    private static final String IFRAME_XPATH = "//iframe[starts-with(@src, '/nuxeo/spreadsheet')]";

    private final static String WORKSPACE_TITLE = "WorkspaceSpreadsheet_"
        + new Date().getTime();

    @Before
    public void setUp()
        throws DocumentBasePage.UserNotConnectedException, IOException {
        DocumentBasePage documentBasePage = login();

        // Create test File
        DocumentBasePage workspacePage = createWorkspace(documentBasePage,
            WORKSPACE_TITLE, null);

        createFile(workspacePage,
            "Test file", "Test File description", false, null, null,
            null);

        logout();
    }

    @Test
    public void itShouldBeAvailableInWorkspace() throws DocumentBasePage.UserNotConnectedException,
        IOException {
        DocumentBasePage documentBasePage = login();
        documentBasePage.getNavigationSubPage().goToDocument(WORKSPACE_TITLE);

        ContentViewElement contentView = getWebFragment(
            By.id("cv_document_content_0_panel"),
            ContentViewElement.class);

        contentView.switchToResultLayout(ResultLayout.LISTING);
        assertNotNull(contentView.getActionByTitle(SPREADSHEET_ACTION_TITLE));

        logout();
    }

    @Test
    public void itShouldBeAvailableInSearch() throws DocumentBasePage.UserNotConnectedException,
        IOException {
        DocumentBasePage documentBasePage = login();
        documentBasePage.goToSearchPage();

        ContentViewElement contentView = getWebFragment(
            By.id("nxw_searchContentView"),
            ContentViewElement.class);

        contentView.switchToResultLayout(ResultLayout.LISTING);
        assertNotNull(contentView.getActionByTitle(SPREADSHEET_ACTION_TITLE));

        logout();
    }

    @Test
    public void itShouldBeAvailableStandalone() throws DocumentBasePage.UserNotConnectedException,
        IOException {
        login();

        navToUrl(NUXEO_URL + "/spreadsheet");
        SpreadsheetPage spreadsheet = asPage(SpreadsheetPage.class);

        // Run a query
        spreadsheet.query.sendKeys("SELECT * FROM Document");
        spreadsheet.execute.click();

        // wait for ajax requests to complete
        new AjaxRequestManager(driver).waitForJQueryRequests();

        // Should display the default columns
        List<String> headers = spreadsheet.getHeaders();
        headers.remove(0); // Remove first column
        assertEquals(Arrays.asList(STANDALONE_COLUMNS), headers);

        // Should have the same rows
        List<WebElement> rows = spreadsheet.getRows();
        assertTrue(rows.size() > 0);

        logout();
    }

    @Test
    public void itShouldDisplayTheSameData() throws DocumentBasePage.UserNotConnectedException,
        IOException {
        DocumentBasePage documentBasePage = login();
        documentBasePage.getNavigationSubPage().goToDocument(WORKSPACE_TITLE);

        ContentViewElement contentView = getWebFragment(
            By.id("cv_document_content_0_panel"),
            ContentViewElement.class);;

        contentView.switchToResultLayout(ResultLayout.LISTING);
        List<String> resultColumns = getContentViewColumns(contentView);

        SpreadsheetPage spreadsheet = openSpreadsheet(contentView);

        String query = spreadsheet.query.getAttribute("value");
        assertNotNull(query);

        // Should display the same columns
        List<String> headers = spreadsheet.getHeaders();
        headers.remove(0); // Remove first column
        assertEquals(resultColumns, headers);

        // Should have the same rows
        List<WebElement> rows = spreadsheet.getRows();
        assertEquals(1, rows.size());
    }

    @After
    public void tearDown() throws DocumentBasePage.UserNotConnectedException {
        DocumentBasePage documentBasePage = login();

        deleteWorkspace(documentBasePage, WORKSPACE_TITLE);

        logout();
    }

    /**
     * opens the spreadsheet popup
     */
    private SpreadsheetPage openSpreadsheet(ContentViewElement cv) {
        cv.switchToResultLayout(ResultLayout.LISTING);

        WebElement spreadsheetAction = cv.getActionByTitle(SPREADSHEET_ACTION_TITLE);
        assertNotNull(spreadsheetAction);

        spreadsheetAction.click();

        WebElement iFrame = Locator.findElementWithTimeout(By.xpath(IFRAME_XPATH));
        assertNotNull(iFrame);
        driver.switchTo().frame(iFrame);

        SpreadsheetPage spreadsheet = new SpreadsheetPage();
        // wait for ajax requests to complete
        new AjaxRequestManager(driver).waitForJQueryRequests();
        // fill the page object
        fillElement(SpreadsheetPage.class, spreadsheet);

        return spreadsheet;
    }

    /**
     * returns the names of the content view's result columns
     */
    private List<String> getContentViewColumns(ContentViewElement cv) {
        List<String> headers = new ArrayList<>();
        WebElement dataOutput = cv.findElement(By.className("dataOutput"));
        List<WebElement> cvHeaders = dataOutput.findElements(
            By.className("colHeader"));
        for (WebElement e : cvHeaders) {
            headers.add(e.getText());
        }
        return headers;
    }
}
