/**************************************************************************
 OmegaT Addon - Import of legacy translations of Java(TM) Resource Bundles
 Copyright (C) 2004  Maxym Mykhalchuk
                     mihmax@yahoo.com

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
**************************************************************************/


package org.omegat.addons.properties_import;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

/**
 * Main frame of importer
 *
 * @author  Maxym Mykhalchuk
 */
public class Main extends javax.swing.JFrame
{
	
	/** Creates new form Main */
	public Main()
	{
		initComponents();
		setLocation(100,100);
		
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jBtnImport = new javax.swing.JButton();
        jLabelSrcLang = new javax.swing.JLabel();
        jLabelLocLang = new javax.swing.JLabel();
        jLabelFolder = new javax.swing.JLabel();
        jLabelNote = new javax.swing.JLabel();
        jLabelLegacyFile = new javax.swing.JLabel();
        jEditLecacyFile = new javax.swing.JTextField();
        jBtnBrowseLegacyFile = new javax.swing.JButton();
        jBtnBrowseFolder = new javax.swing.JButton();
        jEditFolder = new javax.swing.JTextField();
        jEditLocLang = new javax.swing.JTextField();
        jEditSrcLang = new javax.swing.JTextField();
        jLabelItAllowsYou = new javax.swing.JLabel();
        jLabelWelcome = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Legacy importer");
        setName("mainFrame");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

        jBtnImport.setMnemonic('I');
        jBtnImport.setText("Import");
        jBtnImport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBtnImportActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 3, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        getContentPane().add(jBtnImport, gridBagConstraints);

        jLabelSrcLang.setText("Please set the language of default bundle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        getContentPane().add(jLabelSrcLang, gridBagConstraints);

        jLabelLocLang.setText("Please set the language of localization");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        getContentPane().add(jLabelLocLang, gridBagConstraints);

        jLabelFolder.setText("Please enter or browse to the folder, containing resource bundles.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 5);
        getContentPane().add(jLabelFolder, gridBagConstraints);

        jLabelNote.setText("Note, this folder will be scanned recursively");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 6, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jLabelNote, gridBagConstraints);

        jLabelLegacyFile.setText("Enter or browse to the name of a file to keep legacy TMX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jLabelLegacyFile, gridBagConstraints);

        jEditLecacyFile.setText("legacy.tmx");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jEditLecacyFile, gridBagConstraints);

        jBtnBrowseLegacyFile.setText("...");
        jBtnBrowseLegacyFile.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBtnBrowseLegacyFileActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jBtnBrowseLegacyFile, gridBagConstraints);

        jBtnBrowseFolder.setText("...");
        jBtnBrowseFolder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jBtnBrowseFolderActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jBtnBrowseFolder, gridBagConstraints);

        jEditFolder.setText(".");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jEditFolder, gridBagConstraints);

        jEditLocLang.setText("ru");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        getContentPane().add(jEditLocLang, gridBagConstraints);

        jEditSrcLang.setText("EN-US");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        getContentPane().add(jEditSrcLang, gridBagConstraints);

        jLabelItAllowsYou.setFont(new java.awt.Font("MS Sans Serif", 1, 12));
        jLabelItAllowsYou.setText("It allows you to import existing translation from .properties files.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 15, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabelItAllowsYou, gridBagConstraints);

        jLabelWelcome.setFont(new java.awt.Font("MS Sans Serif", 1, 12));
        jLabelWelcome.setText("Welcome to the legacy Java(TM) Resource Bundle importer.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        getContentPane().add(jLabelWelcome, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 1, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabelStatus, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

	private void jBtnBrowseLegacyFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBtnBrowseLegacyFileActionPerformed
	{//GEN-HEADEREND:event_jBtnBrowseLegacyFileActionPerformed
		JFileChooser fch = new JFileChooser(new File(jEditLecacyFile.getText()).getParent());
		fch.addChoosableFileFilter(new FileFilter() 
			{
				public boolean accept(File f) 
				{
					return f.isDirectory() || f.getName().endsWith(".tmx");
				}
				public String getDescription()
				{
					return "TMX Files (.tmx)";
				}
			}
		);
		if( JFileChooser.APPROVE_OPTION == fch.showSaveDialog(this) )
		{
			String file = fch.getSelectedFile().getAbsolutePath();
			if( !file.endsWith(".tmx") )
				file = file + ".tmx";
			jEditLecacyFile.setText(file);
		}
	}//GEN-LAST:event_jBtnBrowseLegacyFileActionPerformed

	private void jBtnBrowseFolderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBtnBrowseFolderActionPerformed
	{//GEN-HEADEREND:event_jBtnBrowseFolderActionPerformed
		JFileChooser fch = new JFileChooser(jEditFolder.getText());
		fch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if( JFileChooser.APPROVE_OPTION == fch.showOpenDialog(this) )
			jEditFolder.setText(fch.getSelectedFile().getAbsolutePath());
	}//GEN-LAST:event_jBtnBrowseFolderActionPerformed

	private void jBtnImportActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jBtnImportActionPerformed
	{//GEN-HEADEREND:event_jBtnImportActionPerformed

		jLabelStatus.setText("Importing...");
		
		try
		{
			Core.doImport(jEditSrcLang.getText(), jEditLocLang.getText(), 
				          jEditFolder.getText(), jEditLecacyFile.getText());
			jLabelStatus.setText("Importing complete");
		}
		catch( Exception e )
		{
			jLabelStatus.setText("Error while importing");
		}
		
	}//GEN-LAST:event_jBtnImportActionPerformed
	
	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt)//GEN-FIRST:event_exitForm
	{
		System.exit(0);
	}//GEN-LAST:event_exitForm
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch( Exception ulfe ) 
		{ 
			// do nothing
		}
		
		new Main().setVisible(true);
	}
	
    // ���������� ���������� - �� ��������� ������ ���//GEN-BEGIN:variables
    private javax.swing.JButton jBtnBrowseFolder;
    private javax.swing.JButton jBtnBrowseLegacyFile;
    private javax.swing.JButton jBtnImport;
    private javax.swing.JTextField jEditFolder;
    private javax.swing.JTextField jEditLecacyFile;
    private javax.swing.JTextField jEditLocLang;
    private javax.swing.JTextField jEditSrcLang;
    private javax.swing.JLabel jLabelFolder;
    private javax.swing.JLabel jLabelItAllowsYou;
    private javax.swing.JLabel jLabelLegacyFile;
    private javax.swing.JLabel jLabelLocLang;
    private javax.swing.JLabel jLabelNote;
    private javax.swing.JLabel jLabelSrcLang;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelWelcome;
    // ����� ���������� ����������//GEN-END:variables
	
}
