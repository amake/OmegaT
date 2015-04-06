/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2015 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package org.omegat.gui.stat;

import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.omegat.core.Core;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.gui.DataTableStyling;
import org.omegat.util.gui.TableColumnSizer;

/**
 *
 * @author Aaron Madlon-Kay
 */
@SuppressWarnings("serial")
public class StatisticsPanel extends BaseStatisticsPanel {
        
    /**
     * Creates new form StatisticsPanel
     */
    public StatisticsPanel(StatisticsWindow window) {
        super(window);
        initComponents();
        DataTableStyling.applyColors(projectTable);
        DataTableStyling.applyColors(filesTable);
        projectTable.setDefaultRenderer(Object.class, DataTableStyling.getNumberCellRenderer());
        filesTable.setDefaultRenderer(Object.class, DataTableStyling.getNumberCellRenderer());
        Font font = projectTable.getFont();
        if (Preferences.isPreference(Preferences.PROJECT_FILES_USE_FONT)) {
            font = Core.getMainWindow().getApplicationFont();
        }
        DataTableStyling.applyFont(projectTable, font);
        DataTableStyling.applyFont(filesTable, font);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        projectLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        projectTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        filesLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        filesTable = new javax.swing.JTable();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        jPanel2.setLayout(new java.awt.BorderLayout(0, 5));

        projectLabel.setText(OStrings.getString("CT_STATS_Project_Statistics")); // NOI18N
        jPanel2.add(projectLabel, java.awt.BorderLayout.NORTH);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(projectTable);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.BorderLayout(0, 5));

        filesLabel.setText(OStrings.getString("CT_STATS_FILE_Statistics")); // NOI18N
        jPanel1.add(filesLabel, java.awt.BorderLayout.NORTH);

        filesTable.setFillsViewportHeight(true);
        jScrollPane2.setViewportView(filesTable);

        jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel1, java.awt.BorderLayout.CENTER);

        add(jPanel4);
    }// </editor-fold>//GEN-END:initComponents

    public void setProjectTableData(final String[] headers, final String[][] projectData) {
        if (headers == null || headers.length == 0) {
            return;
        }
        if (projectData == null || projectData.length == 0) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                projectTable.setModel(new StringArrayTableModel(projectData));
                setTableHeaders(projectTable, headers);
                projectTable.getColumnModel().getColumn(0).setCellRenderer(
                        DataTableStyling.getHeaderTextCellRenderer());
                TableColumnSizer.autoSize(projectTable, 0, false);
                projectTable.setPreferredScrollableViewportSize(projectTable.getPreferredSize());
            }
        });
    }

    public void setFilesTableData(final String[] headers, final String[][] filesData) {
        if (headers == null || headers.length == 0) {
            return;
        }
        if (filesData == null || filesData.length == 0) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                filesTable.setModel(new StringArrayTableModel(filesData));
                setTableHeaders(filesTable, headers);
                filesTable.getColumnModel().getColumn(0).setCellRenderer(
                        DataTableStyling.getTextCellRenderer());
                TableColumnSizer.autoSize(filesTable, 0, false);
            }
        });
    }
    
    private static void setTableHeaders(JTable table, String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setHeaderValue(headers[i]);
        }
    }
    
    class StringArrayTableModel extends AbstractTableModel {
        
        private final String[][] data;

        public StringArrayTableModel(String[][] data) {
            this.data = data;
        }
        
        @Override
        public int getRowCount() {
            return data == null ? 0 : data.length;
        }

        @Override
        public int getColumnCount() {
            return data == null || data.length == 0 ? 0
                    : data[0] == null ? 0 : data[0].length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel filesLabel;
    private javax.swing.JTable filesTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTable projectTable;
    // End of variables declaration//GEN-END:variables
}
