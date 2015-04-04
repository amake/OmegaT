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
import javax.swing.SwingUtilities;
import org.omegat.core.Core;

/**
 *
 * @author Aaron Madlon-Kay
 */
public class PlainTextPanel extends BaseStatisticsPanel {

    /**
     * Creates new form PlainTextPanel
     */
    public PlainTextPanel(StatisticsWindow window) {
        super(window);
        initComponents();
        output.setFont(new Font("Monospaced", Font.PLAIN, Core.getMainWindow().getApplicationFont().getSize()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        output = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        output.setEditable(false);
        jScrollPane1.setViewportView(output);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    public void displayData(final String result) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                output.setText(result);
            }
        });
        setTextData(result);
    }
    
    public void appendData(final String result) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                output.append(result);
                setTextData(output.getText());
            }
        });
    }
    
    @Override
    public void finishData() {
        super.finishData();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                output.setCaretPosition(0);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea output;
    // End of variables declaration//GEN-END:variables
}
