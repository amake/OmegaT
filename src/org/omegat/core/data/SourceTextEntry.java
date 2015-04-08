/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2009-2013 Alex Buloichik
               2015 Aaron Madlon-Kay
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

package org.omegat.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Source text entry represents an individual segment for translation pulled
 * directly from the input files. There can be many SourceTextEntries having
 * identical source language strings
 * 
 * @author Keith Godfrey
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Aaron Madlon-Kay
 */
public class SourceTextEntry {

    private static final ProtectedPart[] EMPTY_PROTECTED_PARTS = new ProtectedPart[0];

    /** Storage for full entry's identifiers, including source text. */
    private final EntryKey key;

    /** 
     * String properties from source file. Contents are alternating 
     * key (even index), value (odd index) strings. Should be of even length.
     */
    private final String[] props;

    /** Translation from source files. */
    private final String sourceTranslation;

    /** Translation from source files is fuzzy. */
    private boolean sourceTranslationFuzzy;

    public enum DUPLICATE {
        /** There is no entries with the same source. */
        NONE,
        /** There is entries with the same source, and this is first entry. */
        FIRST,
        /** There is entries with the same source, and this is not first entry. */
        NEXT
    };

    /** 
     * A list of duplicates of this STE. Will be non-null for the FIRST duplicate,
     * null for NONE and NEXT STEs. See {@link #getDuplicate()} for full logic.
     */
    List<SourceTextEntry> duplicates;
    
    /**
     * The first duplicate of this STE. Will be null for NONE and FIRST STEs,
     * non-null for NEXT STEs. See {@link #getDuplicate()} for full logic.
     */
    SourceTextEntry firstInstance;

    /** Holds the number of this entry in a project. */
    private final int m_entryNum;

    /**
     * Protected parts(shortcuts) and details of full content (for tooltips).
     * Read-only info, cat be accessible from any threads. It can't be null.
     */
    private final ProtectedPart[] protectedParts;

    /**
     * Creates a new source text entry.
     * 
     * @param key
     *            entry key
     * @param entryNum
     *            the number of this entry in a project
     * @param comment
     *            entry comment
     * @param sourceTranslation
     *            translation from source file
     * @param shortcuts
     *            tags shortcuts
     */
    public SourceTextEntry(EntryKey key, int entryNum, String[] props, String sourceTranslation, List<ProtectedPart> protectedParts) {
        this.key = key;
        m_entryNum = entryNum;
        assert props == null || props.length % 2 == 0;
        this.props = props;
        this.sourceTranslation = sourceTranslation;
        if (protectedParts.isEmpty()) {
            this.protectedParts = EMPTY_PROTECTED_PARTS;
        } else {
            // remove empty protected parts
            for (int i = 0; i < protectedParts.size(); i++) {
                if (protectedParts.get(i).getTextInSourceSegment().isEmpty()) {
                    protectedParts.remove(i);
                    i--;
                }
            }
            this.protectedParts = protectedParts.toArray(new ProtectedPart[protectedParts.size()]);
        }
        this.duplicates = null;
        this.firstInstance = null;
    }

    public EntryKey getKey() {
        return key;
    }

    /**
     * Returns the source text (shortcut for
     * <code>getStrEntry().getSrcText()</code>).
     */
    public String getSrcText() {
        return key.sourceText;
    }

    /**
     * Returns comment of entry if exist in source document.
     */
    public String getComment() {
        if (props == null || props.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < props.length; i += 2) {
            sb.append(props[i]);
            sb.append("\n");
        }
        return sb.toString().trim();
    }
    
    public String[] getRawProperties() {
        if (props != null) {
            return Arrays.copyOf(props, props.length);
        }
        return new String[0];
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getProperties() {
        if (props != null) {
            return new SegmentProperties<String>(props);
        }
        return Collections.EMPTY_MAP;
    }

    /** Returns the number of this entry in a project. */
    public int entryNum() {
        return m_entryNum;
    }

    /** If entry with the same source already exist in project. */
    public DUPLICATE getDuplicate() {
        if (firstInstance != null) {
            return DUPLICATE.NEXT;
        }
        return duplicates == null ? DUPLICATE.NONE : DUPLICATE.FIRST;
    }

    public int getNumberOfDuplicates() {
        if (firstInstance != null) {
            return firstInstance.getNumberOfDuplicates();
        }
        return duplicates == null ? 0 : duplicates.size();
    }
    
    @SuppressWarnings("unchecked")
    public List<SourceTextEntry> getDuplicates() {
        if (firstInstance != null) {
            List<SourceTextEntry> result = new ArrayList<SourceTextEntry>(firstInstance.getDuplicates());
            result.remove(this);
            result.add(0, firstInstance);
            return Collections.unmodifiableList(result);
        }
        return duplicates == null ? Collections.EMPTY_LIST
                : Collections.unmodifiableList(duplicates);
    }
    
    public String getSourceTranslation() {
        return sourceTranslation;
    }

    public boolean isSourceTranslationFuzzy() {
        return sourceTranslationFuzzy;
    }

    public void setSourceTranslationFuzzy(boolean sourceTranslationFuzzy) {
        this.sourceTranslationFuzzy = sourceTranslationFuzzy;
    }

    public ProtectedPart[] getProtectedParts() {
        return protectedParts;
    }
}
