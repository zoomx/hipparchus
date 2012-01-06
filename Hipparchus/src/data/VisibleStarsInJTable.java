/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.awt.Color;
import java.awt.Component;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import orchestration.Orchestrator;
import gui.GuiUpdater;

/**
 *
 * @author mandim
 */
public class VisibleStarsInJTable extends AbstractTableModel {

    private static final long serialVersionUID = 7470711745568615848L;
		
	private List<String> data;
    private List<String> columns;
    private String[] columnNames = {"Star", "RA", "Dec"};

    public VisibleStarsInJTable() {
        try {
            String starName = "", ra = "", dec = "";
            data = new ArrayList<String>();
            columns = new ArrayList<String>();
            for (int i = 0; i < Orchestrator.visibleStarsLabelNames.size(); i++) {
                starName = Orchestrator.visibleStarsLabelNames.get(i);
                ra = Orchestrator.visibleStarsLabelRa.get(i);
                dec = Orchestrator.visibleStarsLabelDec.get(i);

                String line = starName + "\n" + ra + "\n" + dec;

                StringTokenizer st2 = new StringTokenizer(line, "\n");
                while (st2.hasMoreTokens()) {
                    data.add(st2.nextToken());
                }

            }
            columns.add("");
            columns.add("");
            columns.add("");

        } catch (Exception e) {
            //GuiUpdater.updateLog(e.toString(), Color.RED);
        }
    }

    public void updateTableModel() {

        try {
            String starName = "", ra = "", dec = "";
            data = new ArrayList<String>();
            columns = new ArrayList<String>();
            for (int i = 0; i < Orchestrator.visibleStarsLabelNames.size(); i++) {
                starName = Orchestrator.visibleStarsLabelNames.get(i);
                ra = Orchestrator.visibleStarsLabelRa.get(i);
                dec = Orchestrator.visibleStarsLabelDec.get(i);

                String line = starName + "\n" + ra + "\n" + dec;

                StringTokenizer st2 = new StringTokenizer(line, "\n");
                while (st2.hasMoreTokens()) {
                    data.add(st2.nextToken());
                }

            }
            columns.add("");
            columns.add("");
            columns.add("");

        } catch (Exception e) {
            GuiUpdater.updateLog(e.toString(), Color.RED);
        }

    }

    public JTable autoResizeColWidth(JTable table, AbstractTableModel model) {

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setModel(model);

        int margin = 5;

        for (int i = 0; i < table.getColumnCount(); i++) {
            int vColIndex = i;
            TableColumnModel colModel = table.getColumnModel();
            TableColumn col = colModel.getColumn(vColIndex);
            int width = 0;

            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();
            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }
            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

            width = comp.getPreferredSize().width;

            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);
                width = Math.max(width, comp.getPreferredSize().width);
            }

            // Add margin
            width += 2 * margin;

            // Set the width
            col.setPreferredWidth(width);
        }

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
                SwingConstants.LEFT);

        // table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);

        return table;
    }

    @Override
    public int getRowCount() {
        return data.size() / getColumnCount();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return (String) data.get((rowIndex * getColumnCount())
                + columnIndex);
    }
}
