/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2008 Alex Buloichik
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

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
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 **************************************************************************/

package org.omegat.gui.editor;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.text.AttributeSet;

import org.omegat.core.Core;
import org.omegat.core.data.SourceTextEntry.DUPLICATE;
import org.omegat.core.spellchecker.SpellCheckerMarker;
import org.omegat.util.Preferences;
import org.omegat.util.gui.Styles;
import org.omegat.util.gui.UIThreadsUtil;

/**
 * Editor behavior control settings.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class EditorSettings {
    private final EditorController parent;

    private boolean useTabForAdvance;
    private boolean markTranslated;
    private boolean markUntranslated;
    private boolean displaySegmentSources;
    private boolean markNonUniqueSegments;
    private String displayModificationInfo;
    private boolean autoSpellChecking;
    private boolean viewSourceBold;

    public static String DISPLAY_MODIFICATION_INFO_NONE = "none";
    public static String DISPLAY_MODIFICATION_INFO_SELECTED = "selected";
    public static String DISPLAY_MODIFICATION_INFO_ALL = "all";

    protected EditorSettings(final EditorController parent) {
        this.parent = parent;

        useTabForAdvance = Preferences.isPreference(Preferences.USE_TAB_TO_ADVANCE);
        markTranslated = Preferences.isPreference(Preferences.MARK_TRANSLATED_SEGMENTS);
        markUntranslated = Preferences.isPreference(Preferences.MARK_UNTRANSLATED_SEGMENTS);
        displaySegmentSources = Preferences.isPreference(Preferences.DISPLAY_SEGMENT_SOURCES);
        markNonUniqueSegments = Preferences.isPreference(Preferences.MARK_NON_UNIQUE_SEGMENTS);
        displayModificationInfo = Preferences.getPreferenceDefault(Preferences.DISPLAY_MODIFICATION_INFO,
                DISPLAY_MODIFICATION_INFO_NONE);
        autoSpellChecking = Preferences.isPreference(Preferences.ALLOW_AUTO_SPELLCHECKING);
        viewSourceBold = Preferences.isPreference(Preferences.VIEW_OPTION_SOURCE_ALL_BOLD);
    }

    public char getAdvancerChar() {
        if (useTabForAdvance) {
            return KeyEvent.VK_TAB;
        } else {
            return KeyEvent.VK_ENTER;
        }
    }

    public boolean isUseTabForAdvance() {
        return useTabForAdvance;
    }

    public void setUseTabForAdvance(boolean useTabForAdvance) {
        this.useTabForAdvance = useTabForAdvance;
        Preferences.setPreference(Preferences.USE_TAB_TO_ADVANCE, useTabForAdvance);
    }

    public boolean isMarkTranslated() {
        return markTranslated;
    }

    public void setMarkTranslated(boolean markTranslated) {
        UIThreadsUtil.mustBeSwingThread();

        parent.commitAndDeactivate();

        this.markTranslated = markTranslated;
        Preferences.setPreference(Preferences.MARK_TRANSLATED_SEGMENTS, markTranslated);

        if (Core.getProject().isProjectLoaded()) {
            parent.loadDocument();
            parent.activateEntry();
        }
    }

    public boolean isMarkUntranslated() {
        return markUntranslated;
    }

    public void setMarkUntranslated(boolean markUntranslated) {
        UIThreadsUtil.mustBeSwingThread();

        parent.commitAndDeactivate();

        this.markUntranslated = markUntranslated;
        Preferences.setPreference(Preferences.MARK_UNTRANSLATED_SEGMENTS, markUntranslated);

        if (Core.getProject().isProjectLoaded()) {
            parent.loadDocument();
            parent.activateEntry();
        }
    }

    /** display the segment sources or not */
    public boolean isDisplaySegmentSources() {
        return displaySegmentSources;
    }

    public boolean isMarkNonUniqueSegments() {
        return markNonUniqueSegments;
    }

    public void setDisplaySegmentSources(boolean displaySegmentSources) {
        UIThreadsUtil.mustBeSwingThread();

        parent.commitAndDeactivate();

        this.displaySegmentSources = displaySegmentSources;
        Preferences.setPreference(Preferences.DISPLAY_SEGMENT_SOURCES, displaySegmentSources);

        if (Core.getProject().isProjectLoaded()) {
            parent.loadDocument();
            parent.activateEntry();
        }
    }

    public void setMarkNonUniqueSegments(boolean markNonUniqueSegments) {
        UIThreadsUtil.mustBeSwingThread();

        parent.commitAndDeactivate();

        this.markNonUniqueSegments = markNonUniqueSegments;
        Preferences.setPreference(Preferences.MARK_NON_UNIQUE_SEGMENTS, markNonUniqueSegments);

        if (Core.getProject().isProjectLoaded()) {
            parent.loadDocument();
            parent.activateEntry();
        }
    }

    /**
     * returns the setting for display the modification information or not
     * Either DISPLAY_MODIFICATION_INFO_NONE,
     * DISPLAY_MODIFICATION_INFO_SELECTED, DISPLAY_MODIFICATION_INFO_ALL
     */
    public String getDisplayModificationInfo() {
        return displayModificationInfo;
    }

    /**
     * Sets the setting for display the modification information or not
     * 
     * @param displayModificationInfo
     *            Either DISPLAY_MODIFICATION_INFO_NONE ,
     *            DISPLAY_MODIFICATION_INFO_SELECTED ,
     *            DISPLAY_MODIFICATION_INFO_ALL
     */
    public void setDisplayModificationInfo(String displayModificationInfo) {
        UIThreadsUtil.mustBeSwingThread();

        parent.commitAndDeactivate();

        this.displayModificationInfo = displayModificationInfo;
        Preferences.setPreference(Preferences.DISPLAY_MODIFICATION_INFO, displayModificationInfo);

        if (Core.getProject().isProjectLoaded()) {
            parent.loadDocument();
            parent.activateEntry();
        }
    }

    /** need to check spell or not */
    public boolean isAutoSpellChecking() {
        return autoSpellChecking;
    }

    public void setAutoSpellChecking(boolean autoSpellChecking) {
        UIThreadsUtil.mustBeSwingThread();
        if (Core.getProject().isProjectLoaded()) {
            parent.commitAndDeactivate();
        }

        this.autoSpellChecking = autoSpellChecking;

        if (Core.getProject().isProjectLoaded()) {
            // parent.loadDocument();
            parent.activateEntry();
            parent.remarkOneMarker(SpellCheckerMarker.class.getName());
        }
    }
    
    /**
     * Choose segment's attributes based on rules.
     * @param isSource is it a source segment or a target segment
     * @param duplicate is the sourceTextEntry a duplicate or not? values: DUPLICATE.NONE, DUPLICATE.FIRST or DUPLICATE.NEXT. See sourceTextEntryste.getDuplicate()
     * @param active is it an active segment?
     * @param translationExists does a translation already exist
     * @return proper AttributeSet to use on displaying the segment.
     */
    public AttributeSet getAttributeSet(boolean isSource, DUPLICATE duplicate, boolean active, boolean translationExists) {
        Color fg = null;
        if (isMarkNonUniqueSegments()) {
            switch (duplicate) {
            case NONE:
                break;
            case FIRST:
                if (Preferences.isPreference(Preferences.VIEW_OPTION_UNIQUE_FIRST)) {
                    fg = Styles.COLOR_LIGHT_GRAY;
                }
                break;
            case NEXT:
                fg = Styles.COLOR_LIGHT_GRAY;
                break;
            }
        }

        Color bg = null;
        if (active) {
            if (isSource) {
                bg = Styles.COLOR_GREEN;
            }
        } else {
            if (isSource) {
                if (isMarkUntranslated() && !translationExists) {
                    bg = Styles.COLOR_UNTRANSLATED;
                } else if (isDisplaySegmentSources()) {
                    bg = Styles.COLOR_GREEN;
                }
            } else if (isMarkTranslated()) {
                bg = Styles.COLOR_TRANSLATED;
            }
        }

        Boolean bold = false;
        if (isSource) {
            if (active || viewSourceBold && isDisplaySegmentSources()) {
                bold = true;
            }
        }
        Boolean italic = false;

        return Styles.createAttributeSet(fg, bg, bold, italic);
    }
    /**
     * Returns font attributes for the modification info line.
     * @return
     */
    public AttributeSet getModificationInfoAttributeSet() {
        return Styles.createAttributeSet(null, null, null, true);
    }
    /**
     * Returns font attributes for the segment marker.
     * @return
     */
    public AttributeSet getSegmentMarkerAttributeSet() {
        return Styles.createAttributeSet(null, null, true, false);
    }
}
