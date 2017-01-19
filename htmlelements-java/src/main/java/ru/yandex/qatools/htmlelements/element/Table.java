package ru.yandex.qatools.htmlelements.element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.yandex.qatools.htmlelements.exceptions.HtmlElementsException;

/**
 * Represents web page table element. Provides convenient ways of retrieving
 * data stored in it.
 *
 * @author Alexander Tolmachev starlight@yandex-team.ru
 *         Date: 11.03.13
 */
public class Table extends TypifiedElement {
    /**
     * Specifies {@link org.openqa.selenium.WebElement} representing table tag.
     *
     * @param wrappedElement {@code WebElement} to wrap.
     */
    public Table(WebElement wrappedElement) {
        super(wrappedElement);
    }

    /**
     * Returns a list of table heading elements ({@code <th>}).
     * <p>
     * Multiple rows of heading elements, ({@code <tr>}), are flattened
     * i.e. the second row, ({@code <tr>}), will follow the first, which can be
     * misleading when the table uses {@code colspan} and {@code rowspan}.
     *
     * @return List with table heading elements.
     */
    public List<WebElement> getHeadings() {
        return getWrappedElement().findElements(By.xpath(".//th"));
    }

    /**
     * Returns text values of table heading elements (contained in "th" tags).
     *
     * @return List with text values of table heading elements.
     */
    public List<String> getHeadingsAsString() {
        List<String> headingsAsString = new ArrayList<>();
        for (WebElement heading : getHeadings()) {
            headingsAsString.add(heading.getText());
        }
        return headingsAsString;
    }

    /**
     * Returns table cell elements ({@code <td>}), grouped by rows.
     *
     * @return List where each item is a table row.
     */
    public List<List<WebElement>> getRows() {
        List<List<WebElement>> rows = new ArrayList<>();
        List<WebElement> rowElements = getWrappedElement().findElements(By.xpath(".//tr"));
        for (WebElement rowElement : rowElements) {
            List<WebElement> columns = rowElement.findElements(By.xpath(".//td"));
            if (!columns.isEmpty()) {
                rows.add(columns);
            }
        }
        return rows;
    }

    /**
     * Returns text values of table cell elements ({@code <td>}), grouped by rows.
     *
     * @return List where each item is text values of a table row.
     */
    public List<List<String>> getRowsAsString() {
        List<List<String>> rowValues = new ArrayList<>();
        for (List<WebElement> row : getRows()) {
            List<String> cellValues = new ArrayList<>();
            for (WebElement cell : row) {
                cellValues.add(cell.getText());
            }
            rowValues.add(cellValues);
        }
        return rowValues;
    }

    /**
     * Returns table cell elements ({@code <td>}), grouped by columns.
     *
     * @return List where each item is a table column.
     */
    public List<List<WebElement>> getColumns() {
        List<List<WebElement>> columns = new ArrayList<>();
        List<List<WebElement>> rows = getRows();

        if (rows.isEmpty()) {
            return columns;
        }

        int columnCount = rows.get(0).size();
        for (int i = 0; i < columnCount; i++) {
            List<WebElement> column = new ArrayList<>();
            for (List<WebElement> row : rows) {
                column.add(row.get(i));
            }
            columns.add(column);
        }

        return columns;
    }

    /**
     * Returns table cell elements ({@code <td>}), of a particular column.
     *
     * @param index the 1-based index of the desired column
     * @return List where each item is a cell of a particular column.
     */
    public List<WebElement> getColumnByIndex(int index) {
        return getWrappedElement().findElements(
                By.cssSelector(String.format("tr > td:nth-of-type(%d)", index)));
    }

    /**
     * Returns text values of table cell elements ({@code <td>}), grouped by columns.
     *
     * @return List where each item is text values of a table column.
     */
    public List<List<String>> getColumnsAsString() {
        List<List<String>> columnsValues = new ArrayList<>();
        List<String> cellValues = new ArrayList<>();
        for (List<WebElement> columns : getColumns()) {
            cellValues.clear();
            for (WebElement cell : columns) {
                cellValues.add(cell.getText());
            }
            columnsValues.add(cellValues);
        }
        return columnsValues;
    }

    /**
     * Returns table cell element ({@code <td>}), at i-th row and j-th column.
     *
     * @param i Row number
     * @param j Column number
     * @return Cell element at i-th row and j-th column.
     */
    public WebElement getCellAt(int i, int j) {
        return getRows().get(i).get(j);
    }

    /**
     * Returns list of maps where keys are table headings and values are table
     * row elements ({@code <td>}).
     *
     * @return List of maps where keys are table headings and values are table *
     * row elements.
     */
    public List<Map<String, WebElement>> getRowsMappedToHeadings() {
        return getRowsMappedToHeadings(getHeadingsAsString());
    }

    /**
     * Returns list of maps where keys are passed headings and values are table
     * row elements ({@code <td>}).
     *
     * @param headings List containing strings to be used as table headings.
     * @return List of maps where keys are passed headings and values are table
     * row elements.
     */
    public List<Map<String, WebElement>> getRowsMappedToHeadings(List<String> headings) {
        List<Map<String, WebElement>> rowsMappedToHeadings = new ArrayList<>();

        for (List<WebElement> row : getRows()) {
            if (row.size() != headings.size()) {
                throw new HtmlElementsException("Headings count is not equal to number of cells in row");
            }

            Map<String, WebElement> rowToHeadingsMap = new HashMap<>();
            int cellNumber = 0;
            for (String heading : headings) {
                rowToHeadingsMap.put(heading, row.get(cellNumber));
                cellNumber++;
            }
            rowsMappedToHeadings.add(rowToHeadingsMap);
        }
        return rowsMappedToHeadings;
    }

    /**
     * Same as {@link #getRowsMappedToHeadings()} but retrieves text from row
     * elements ({@code <td>}).
     *
     * @return List of maps where keys are passed headings and values are table
     * row text of elements.
     */
    public List<Map<String, String>> getRowsAsStringMappedToHeadings() {
        return getRowsAsStringMappedToHeadings(getHeadingsAsString());

    }

    /**
     * Same as {@link #getRowsMappedToHeadings(java.util.List)} but retrieves
     * text from row elements ({@code <td>}).
     *
     * @param headings List containing strings to be used as table headings.
     * @return List of maps where keys are passed headings and values are table
     * row text of elements.
     */
    public List<Map<String, String>> getRowsAsStringMappedToHeadings(List<String> headings) {
        List<Map<String, String>> rowsMappedToHeadings = new ArrayList<>();
        for (Map<String, WebElement> row : getRowsMappedToHeadings(headings)) {
            Map<String, String> rowMap = new HashMap<>();
            for (Map.Entry<String, WebElement> cell : row.entrySet()) {
                rowMap.put(cell.getKey(), cell.getValue().getText());
            }
            rowsMappedToHeadings.add(rowMap);
        }
        return rowsMappedToHeadings;
    }
}
