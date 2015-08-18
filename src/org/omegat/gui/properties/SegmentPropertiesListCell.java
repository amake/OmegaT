package org.omegat.gui.properties;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import org.omegat.util.gui.Styles;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class SegmentPropertiesListCell extends javax.swing.JPanel {

    String key = null;
    
    /**
     * Creates new form SegmentPropertiesListCell
     */
    public SegmentPropertiesListCell() {
        initComponents();
        setBackground(Styles.EditorColor.COLOR_BACKGROUND.getColor());
        labelPanel.setBackground(ISegmentPropertiesView.ROW_HIGHLIGHT_COLOR);
        labelPanel.setBorder(ISegmentPropertiesView.MARGIN_BORDER);
        label.setFont(UIManager.getFont("Label.font"));
        label.setForeground(Styles.EditorColor.COLOR_FOREGROUND.getColor());
        label.setBackground(ISegmentPropertiesView.ROW_HIGHLIGHT_COLOR);
        value.setForeground(Styles.EditorColor.COLOR_FOREGROUND.getColor());
        value.setBackground(Styles.EditorColor.COLOR_BACKGROUND.getColor());
        value.setBorder(ISegmentPropertiesView.MARGIN_BORDER);
        settingsButton.setBackground(ISegmentPropertiesView.ROW_HIGHLIGHT_COLOR);
        settingsButton.setIcon(ISegmentPropertiesView.SETTINGS_ICON_INVISIBLE);
        settingsButton.setRolloverIcon(ISegmentPropertiesView.SETTINGS_ICON);
        settingsButton.setPressedIcon(ISegmentPropertiesView.SETTINGS_ICON_PRESSED);
        settingsButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        MouseAdapter revealSettingsIcon = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                settingsButton.setIcon(ISegmentPropertiesView.SETTINGS_ICON_INACTIVE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                settingsButton.setIcon(ISegmentPropertiesView.SETTINGS_ICON_INVISIBLE);
            }
        };
        label.addMouseListener(revealSettingsIcon);
        value.addMouseListener(revealSettingsIcon);
        // Prevent list from scrolling down as new cells are added
        Caret caret = label.getCaret();
        if (caret instanceof DefaultCaret) {
            ((DefaultCaret) caret).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
        caret = value.getCaret();
        if (caret instanceof DefaultCaret) {
            ((DefaultCaret) caret).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelPanel = new javax.swing.JPanel();
        label = new javax.swing.JTextArea();
        settingsButton = new javax.swing.JButton();
        value = new org.omegat.gui.properties.FlashableTextArea();

        setLayout(new java.awt.BorderLayout());

        labelPanel.setLayout(new java.awt.BorderLayout());

        label.setEditable(false);
        label.setLineWrap(true);
        labelPanel.add(label, java.awt.BorderLayout.CENTER);

        settingsButton.setBorderPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setFocusable(false);
        settingsButton.setRolloverEnabled(true);
        labelPanel.add(settingsButton, java.awt.BorderLayout.EAST);

        add(labelPanel, java.awt.BorderLayout.NORTH);

        value.setEditable(false);
        value.setLineWrap(true);
        add(value, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JTextArea label;
    private javax.swing.JPanel labelPanel;
    javax.swing.JButton settingsButton;
    org.omegat.gui.properties.FlashableTextArea value;
    // End of variables declaration//GEN-END:variables
}
