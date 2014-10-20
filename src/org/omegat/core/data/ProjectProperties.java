/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2012 Guido Leenders, Didier Briel
               2013 Aaron Madlon-Kay, Yu Tang
               2014 Aaron Madlon-Kay, Alex Buloichik
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

import gen.core.filters.Filters;
import gen.core.project.RepositoryDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.omegat.core.segmentation.SRX;
import org.omegat.filters2.master.PluginUtils;
import org.omegat.util.FileUtil;
import org.omegat.util.Language;
import org.omegat.util.OConsts;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.StaticUtils;
import org.omegat.util.StringUtil;

/**
 * Storage for project properties. May read and write project from/to disk.
 * 
 * @author Keith Godfrey
 * @author Maxym Mykhalchuk
 * @author Guido Leenders
 * @author Didier Briel
 * @author Aaron Madlon-Kay
 * @author Yu Tang
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class ProjectProperties {

    public static final String[] DEFAULT_EXCLUDES = { "**/.svn/**", "**/CSV/**", "**/.cvs/**",
            "**/desktop.ini", "**/Thumbs.db" };

    /**
     * Constructor for tests only.
     */
    protected ProjectProperties() {
    }

    /** Default constructor to initialize fields (to get no NPEs). */
    public ProjectProperties(File projectDir) throws Exception {
        setProjectName(projectDir.getCanonicalFile().getName());
        setProjectRoot(projectDir.getAbsolutePath() + File.separator);
        setSourceRoot(projectRoot + OConsts.DEFAULT_SOURCE + File.separator);
        sourceRootExcludes.addAll(Arrays.asList(DEFAULT_EXCLUDES));
        setTargetRoot(projectRoot + OConsts.DEFAULT_TARGET + File.separator);
        setGlossaryRoot(projectRoot + OConsts.DEFAULT_GLOSSARY + File.separator);
        setWriteableGlossary(projectRoot + OConsts.DEFAULT_GLOSSARY + File.separator + OConsts.DEFAULT_W_GLOSSARY);
        setTMRoot(projectRoot + OConsts.DEFAULT_TM + File.separator);
        setDictRoot(projectRoot + OConsts.DEFAULT_DICT + File.separator);

        setSentenceSegmentingEnabled(true);
        setSupportDefaultTranslations(true);
        setRemoveTags(false);

        String sourceLocale = Preferences.getPreference(Preferences.SOURCE_LOCALE);
        if (!StringUtil.isEmpty(sourceLocale)) {
            setSourceLanguage(sourceLocale);
        } else {
            setSourceLanguage("EN-US");
        }

        String targetLocale = Preferences.getPreference(Preferences.TARGET_LOCALE);
        if (!StringUtil.isEmpty(targetLocale)) {
            setTargetLanguage(targetLocale);
        } else {
            setTargetLanguage("EN-GB");
        }

        projectSRX = SRX.loadSRX(new File(getProjectInternal(), SRX.CONF_SENTSEG));

        setSourceTokenizer(PluginUtils.getTokenizerClassForLanguage(getSourceLanguage()));
        setTargetTokenizer(PluginUtils.getTokenizerClassForLanguage(getTargetLanguage()));
    }

	/** Returns The Target (Compiled) Files Directory */
    public String getTargetRoot() {
        return targetRoot;
    }

    public File getTargetRootDir() {
        return new File(targetRoot);
    }

    /** Sets The Target (Compiled) Files Directory */
    public void setTargetRoot(String targetRoot) {
        if (!StringUtil.isEmpty(targetRoot)) {
            this.targetRoot = targetRoot;
        }
    }

    public String getTargetRootRelative() {
        return targetRootRelative;
    }

    public void setTargetRootRelative(String targetRootRelative) {
        this.targetRootRelative = targetRootRelative;
    }

    /** Returns The Glossary Files Directory */
    public String getGlossaryRoot() {
        return glossaryRoot;
    }

    public File getGlossaryRootDir() {
        return new File(glossaryRoot);
    }

    /** Sets The Glossary Files Directory */
    public void setGlossaryRoot(String glossaryRoot) {
        if (!StringUtil.isEmpty(glossaryRoot)) {
            this.glossaryRoot = glossaryRoot;
        }
    }

    public String getGlossaryRootRelative() {
        return glossaryRootRelative;
    }

    public void setGlossaryRootRelative(String glossaryRootRelative) {
        this.glossaryRootRelative = glossaryRootRelative;
    }

    /** Returns The Glossary File Location */
    public String getWriteableGlossary() {
        return writeableGlossaryFile;
    }

    /** Returns The Glossary File Directory */
    public String getWriteableGlossaryDir() {
        File fDir = new File(writeableGlossaryFile);
        String sDir = fDir.getParent();
        return sDir;
    }

    /** Sets The Writeable Glossary File Location */
    public void setWriteableGlossary(String writeableGlossaryFile) {
        if (!StringUtil.isEmpty(writeableGlossaryFile)) {
            this.writeableGlossaryFile = writeableGlossaryFile;
        }
    }

    public String getWriteableGlossaryFileRelative() {
        return writeableGlossaryFileRelative;
    }

    public void setWriteableGlossaryFileRelative(String writeableGlossaryFileRelative) {
        this.writeableGlossaryFileRelative = writeableGlossaryFileRelative;
    }

    public boolean isDefaultWriteableGlossaryFile() {
        return writeableGlossaryFile.equals(computeDefaultWriteableGlossaryFile());
    }

    public String computeDefaultWriteableGlossaryFile() {
        // Default glossary file name depends on where glossaryDir is:
        //  - Inside project folder: glossary.txt
        //  - Outside project folder: ${projectName}-glossary.txt
        String glossaryDir = getGlossaryRoot();
        if (glossaryDir.startsWith(projectRoot)) {
            return glossaryDir + OConsts.DEFAULT_W_GLOSSARY;
        } else {
            return glossaryDir + projectName + OConsts.DEFAULT_W_GLOSSARY_SUFF;
        }
    }

    /** Returns The Translation Memory (TMX) Files Directory */
    public String getTMRoot() {
        return tmRoot;
    }

    public String getTMRootRelative() {
        return tmRootRelative;
    }

    public void setTMRootRelative(String tmRootRelative) {
        this.tmRootRelative = tmRootRelative;
    }

    /** Sets The Translation Memory (TMX) Files Directory */
    public void setTMRoot(String tmRoot) {
        if (!StringUtil.isEmpty(tmRoot)) {
            this.tmRoot = tmRoot;
        }
    }
    
  
    /** Returns The Translation Memory (TMX) with translations to other languages Files Directory */
    public String getTMOtherLangRoot() {
        return tmRoot + OConsts.DEFAULT_OTHERLANG + File.separator;
    }

    /** Returns The Translation Memory (TMX) Files Directory for automatically applied files. */
    public String getTMAutoRoot() {
        return tmRoot + OConsts.AUTO_TM + File.separator;
    }

    /** Returns The Dictionaries Files Directory */
    public String getDictRoot() {
        return dictRoot;
    }

    public File getDictRootDir() {
        return new File(dictRoot);
    }

    /** Sets Dictionaries Files Directory */
    public void setDictRoot(String dictRoot) {
        if (!StringUtil.isEmpty(dictRoot)) {
            this.dictRoot = dictRoot;
        }
    }

    public String getDictRootRelative() {
        return dictRootRelative;
    }

    public void setDictRootRelative(String dictRootRelative) {
        this.dictRootRelative = dictRootRelative;
    }

    /** Returns the name of the Project */
    public String getProjectName() {
        return projectName;
    }

    /** Sets the name of the Project */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /** Returns The Project Root Directory */
    public String getProjectRoot() {
        return projectRoot;
    }

    public File getProjectRootDir() {
        return new File(projectRoot);
    }

    /** Sets The Project Root Directory */
    public void setProjectRoot(String projectRoot) {
        this.projectRoot = projectRoot;
    }

    /** Returns The Project's Translation Memory (TMX) File */
    public String getProjectInternal() {
        return projectRoot + OConsts.DEFAULT_INTERNAL + File.separator;
    }

    public File getProjectInternalDir() {
        return new File(projectRoot + OConsts.DEFAULT_INTERNAL + File.separator);
    }

    public String getProjectInternalRelative() {
        return OConsts.DEFAULT_INTERNAL + File.separator;
    }

    /** Returns The Source (to be translated) Files Directory */
    public String getSourceRoot() {
        return sourceRoot;
    }

    public File getSourceRootDir() {
        return new File(sourceRoot);
    }

    /** Sets The Source (to be translated) Files Directory */
    public void setSourceRoot(String sourceRoot) {
        if (!StringUtil.isEmpty(sourceRoot)) {
            this.sourceRoot = sourceRoot;
        }
    }

    public String getSourceRootRelative() {
        return sourceRootRelative;
    }

    public void setSourceRootRelative(String sourceRootRelative) {
        this.sourceRootRelative = sourceRootRelative;
        sourceDir = new ProjectDir(sourceRootRelative, OConsts.DEFAULT_SOURCE);
    }

    public ProjectDir getSourceDir() {
        return sourceDir;
    }

    public List<String> getSourceRootExcludes() {
        return sourceRootExcludes;
    }

    /**
     * Returns The Source Language (language of the source files) of the Project
     */
    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    /** Sets The Source Language (language of the source files) of the Project */
    public void setSourceLanguage(Language sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    /** Sets The Source Language (language of the source files) of the Project */
    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = new Language(sourceLanguage);
    }

    /**
     * Returns The Target Language (language of the translated files) of the Project
     */
    public Language getTargetLanguage() {
        return targetLanguage;
    }

    /**
     * Sets The Target Language (language of the translated files) of the Project
     */
    public void setTargetLanguage(Language targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    /**
     * Sets The Target Language (language of the translated files) of the Project
     */
    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = new Language(targetLanguage);
    }

    /**
     * Returns the class name of the source language tokenizer for the Project.
     */
    public Class<?> getSourceTokenizer() {
        if (sourceTokenizer == null) {
            Class<?> cls = PluginUtils.getTokenizerClassForLanguage(getSourceLanguage());
            setSourceTokenizer(cls);
        }
        return sourceTokenizer;
    }

    /**
     * Sets the class name of the source language tokenizer for the Project.
     */
    public void setSourceTokenizer(Class<?> sourceTokenizer) {
        this.sourceTokenizer = sourceTokenizer;
    }

    /**
     * Returns the class name of the target language tokenizer for the Project.
     */
    public Class<?> getTargetTokenizer() {
        return targetTokenizer;
    }

    /**
     * Sets the class name of the target language tokenizer for the Project.
     */
    public void setTargetTokenizer(Class<?> targetTokenizer) {
        this.targetTokenizer = targetTokenizer;
    }

    /**
     * Returns whether The Sentence Segmenting is Enabled for this Project. Default, Yes.
     */
    public boolean isSentenceSegmentingEnabled() {
        return sentenceSegmentingOn;
    }

    /** Sets whether The Sentence Segmenting is Enabled for this Project */
    public void setSentenceSegmentingEnabled(boolean sentenceSegmentingOn) {
        this.sentenceSegmentingOn = sentenceSegmentingOn;
    }

    public boolean isSupportDefaultTranslations() {
        return supportDefaultTranslations;
    }

    public void setSupportDefaultTranslations(boolean supportDefaultTranslations) {
        this.supportDefaultTranslations = supportDefaultTranslations;
    }

    public boolean isRemoveTags() {
        return removeTags;
    }

    public void setRemoveTags(boolean removeTags) {
        this.removeTags = removeTags;
    }

    public List<RepositoryDefinition> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RepositoryDefinition> repositories) {
        this.repositories = repositories;
    }

    public SRX getProjectSRX() {
        return projectSRX;
    }

    public void setProjectSRX(SRX projectSRX) {
        this.projectSRX = projectSRX;
    }

    public Filters getProjectFilters() {
        return projectFilters;
    }

    public void setProjectFilters(Filters projectFilters) {
        this.projectFilters = projectFilters;
    }
    
    public String getExternalCommand() {
        return externalCommand;
    }

    public void setExternalCommand(String command) {
        this.externalCommand = command;
    }

    public boolean isProjectValid() {
        boolean returnValue;
        try {
            verifyProject();
            returnValue = true;
        } catch (ProjectException ex) {
            returnValue = false;
        }
        return returnValue;
    }
    
    /**
     * Verify project and print any problems.
     */
    public void verifyProject() throws ProjectException {
        //
        // Now check whether these directories are where they're suposed to be.
        //
        String srcDir = getSourceRoot();
        File src = new File(srcDir);
        if (!src.exists()) {
            throw new ProjectException(StaticUtils.format(OStrings.getString("PROJECT_SOURCE_FOLDER"), srcDir));
        }
        //
        String tgtDir = getTargetRoot();
        File tgt = new File(tgtDir);
        if (!tgt.exists()) {
            throw new ProjectException(StaticUtils.format(OStrings.getString("PROJECT_TARGET_FOLDER"), tgtDir));
        }
        //
        String glsDir = getGlossaryRoot();
        File gls = new File(glsDir);
        if (!gls.exists()) {
            throw new ProjectException(StaticUtils.format(OStrings.getString("PROJECT_GLOSSARY_FOLDER"), glsDir));
        }
        String wGlsDir = getWriteableGlossaryDir();
        if (!wGlsDir.endsWith(File.separator)) {
            wGlsDir += File.separator;
        }
        if (!wGlsDir.contains(getGlossaryRoot())) {
            throw new ProjectException(StaticUtils.format(OStrings.getString("PROJECT_W_GLOSSARY"), glsDir));
        }

        //
        String tmxDir = getTMRoot();
        File tmx = new File(tmxDir);
        if (!tmx.exists()) {
            throw new ProjectException(StaticUtils.format(OStrings.getString("PROJECT_TM_FOLDER"), tmxDir));
        }
        
        // Dictionary folder is always created automatically when it does not exist, for ascending
        // compatibility reasons.
        // There is no exception handling when a failure occurs during folder creation.
        //
        File dict = new File(getDictRoot());
        if (!dict.exists()) {
            if (getDictRoot().equals(projectRoot + OConsts.DEFAULT_DICT + File.separator)) {
                dict.mkdirs();
            }
        }
    }

    /**
     * Verify the correctness of a language or country code
     * 
     * @param code
     *            A string containing a language or country code
     * @return <code>true</code> or <code>false</code>
     */
    private static boolean verifyLangCode(String code) {
        // Make sure all values are characters
        for (int i = 0; i < code.length(); i++) {
            if (!Character.isLetter(code.charAt(i)))
                return false;
        }
        if (new Language(code).getDisplayName().length() > 0) {
            return true;
        } else
            return false;
    }

    /**
     * Verifies whether the language code is OK.
     */
    public static boolean verifySingleLangCode(String code) {
        if (code.length() == 2 || code.length() == 3) {
            return verifyLangCode(code);
        } else if (code.length() == 5 || code.length() == 6) {
            int shift = 0;
            if (code.length() == 6)
                shift = 1;
            if ((verifyLangCode(code.substring(0, 2 + shift)))
                    && (code.charAt(2 + shift) == '-' || code.charAt(2 + shift) == '_')
                    && (verifyLangCode(code.substring(3 + shift, 5 + shift))))
                return true;
            else
                return false;
        }
        return false;
    }

    private String projectName;
    private String projectRoot;
    private String sourceRoot;
    private String sourceRootRelative;
    private final List<String> sourceRootExcludes = new ArrayList<String>();
    private String targetRoot;
    private String targetRootRelative;
    private String glossaryRoot;
    private String glossaryRootRelative;
    private String writeableGlossaryFile;
    private String writeableGlossaryFileRelative;
    private String tmRoot;
    private String tmRootRelative;
    private String dictRoot;
    private String dictRootRelative;
    private List<RepositoryDefinition> repositories;

    private Language sourceLanguage;
    private Language targetLanguage;

    private Class<?> sourceTokenizer;
    private Class<?> targetTokenizer;

    private boolean sentenceSegmentingOn;
    private boolean supportDefaultTranslations;
    private boolean removeTags;

    private SRX projectSRX;
    private Filters projectFilters;
    
    private String externalCommand;

    protected File projectRootDir;
    protected ProjectDir sourceDir;

    /**
     * Class for support directories functionality, like relative path, etc.
     */
    public class ProjectDir {
        protected final File dir;
        /** Null if directory not under project root. Or relative directory with '/' at the end. */
        protected String underRoot;

        public ProjectDir(String path, String defaultPath) {
            if (OConsts.DEFAULT_FOLDER_MARKER.equals(path)) {
                path = defaultPath;
            }
            underRoot = null;
            if (FileUtil.isDirRelative(path)) {
                dir = new File(projectRootDir, path);
                if (!path.contains("..")) {
                    underRoot = path.replace(File.separatorChar, '/');
                    if (!underRoot.endsWith("/")) {
                        underRoot += '/';
                    }
                }
            } else {
                dir = new File(path);
            }
        }

        public File getDirectory() {
            return dir;
        }

        /**
         * Returns path under project root with '/' at the end, or null if directory outside of project.
         */
        public String getUnderRoot() {
            return underRoot;
        }
    }
}
