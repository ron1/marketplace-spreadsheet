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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

/**
 * Spreadsheet page object
 *
 * @since 7.1
 */
public class SpreadsheetPage {

    private static final String HEADERS_XPATH = "thead/tr/th/div/span";

    private static final String ROWS_XPATH = "tbody/tr/*[1]";

    @Required
    @FindBy(xpath = "//table[@class=\"htCore\"]")
    WebElement table;

    @Required
    @FindBy(id = "query")
    WebElement query;

    @FindBy(id = "execute")
    WebElement execute;

    public List<WebElement> getHeaderElements() {
        return table.findElements(By.xpath(HEADERS_XPATH));
    }

    public List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        for (WebElement e : getHeaderElements()) {
            headers.add(e.getText());
        }
        return headers;
    }

    public List<WebElement> getRows() {
        return table.findElements(By.xpath(ROWS_XPATH));
    }
}
