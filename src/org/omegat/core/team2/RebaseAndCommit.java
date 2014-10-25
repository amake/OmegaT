/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2014 Alex Buloichik, Martin Fleurke, Aaron Madlon-Kay
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

package org.omegat.core.team2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.omegat.util.FileUtil;
import org.omegat.util.Log;

/**
 * Core for rebase and commit files.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Martin Fleurke
 * @author Aaron Madlon-Kay
 */
public class RebaseAndCommit {
    private static final Logger LOGGER = Logger.getLogger(RebaseAndCommit.class.getName());

    public static void rebaseAndCommit(RemoteRepositoryProvider provider, File projectDir, String path,
            IRebase rebaser) throws Exception {

        if (!provider.isUnderMapping(path)) {
            throw new RuntimeException("Path is not under mapping: " + path);
        }

        Log.logDebug(LOGGER, "Rebase and commit '" + path + "'");

        final String currentBaseVersion;
        String savedVersion = readVersion(projectDir, path);
        if (savedVersion != null) {
            currentBaseVersion = savedVersion;
        } else {
            // version wasn't stored - assume latest. TODO Probably need to ask ?
            provider.switchToVersion(path, null);
            currentBaseVersion = provider.getVersion(path);
        }

        final boolean fileChangedLocally;
        {
            File baseRepoFile = provider.switchToVersion(path, currentBaseVersion);
            if (FileUtil.isFilesEqual(baseRepoFile, new File(projectDir, path))) {
                // versioned file was not changed - no need to commit
                Log.logDebug(LOGGER, "local file '" + path + "' wasn't changed");
                fileChangedLocally = false;
            } else {
                Log.logDebug(LOGGER, "local file '" + path + "' was changed");
                fileChangedLocally = true;
                rebaser.parseBaseFile(baseRepoFile);
            }
            // baseRepoFile is not valid anymore because we will switch to other version
        }

        final boolean fileChangedRemotely;
        final File headRepoFile = provider.switchToVersion(path, null);
        final String headVersion = provider.getVersion(path);
        if (currentBaseVersion.equals(headVersion)) {
            Log.logDebug(LOGGER, "remote file '" + path + "' wasn't changed");
            fileChangedRemotely = false;
        } else {
            // base and head versions are differ - somebody else committed changes
            Log.logDebug(LOGGER, "remote file '" + path + "' was changed");
            fileChangedRemotely = true;
            rebaser.parseHeadFile(headRepoFile);
        }

        final File tempOut = new File(projectDir, path + "#based_on_" + headVersion);
        if (tempOut.exists() && !tempOut.delete()) {
            throw new Exception("Unable to delete previous temp file");
        }
        if (fileChangedLocally && fileChangedRemotely) {
            // rebase need only in case file was changed locally AND remotely
            Log.logDebug(LOGGER, "rebase and save '" + path + "'");
            rebaser.rebaseAndSave(tempOut);
        } else if (fileChangedLocally && !fileChangedRemotely) {
            // only local changes - just use local file
            Log.logDebug(LOGGER, "only local changes - just use local file '" + path + "'");
        } else if (!fileChangedLocally && fileChangedRemotely) {
            // only remote changes - get remote
            Log.logDebug(LOGGER, "only remote changes - get remote '" + path + "'");
            FileUtil.copyFile(headRepoFile, tempOut);
        } else {
            Log.logDebug(LOGGER, "there are no changes '" + path + "'");
            // there are no changes
        }

        if (tempOut.exists()) {
            // new file was saved, need to update version
            // code below tries to update file "in transaction" with update version
            final File bakTemp = new File(projectDir, path + "#oldbased_on_" + currentBaseVersion);
            move(new File(projectDir, path), bakTemp);
            saveVersion(projectDir, path, headVersion);
            move(tempOut, new File(projectDir, path));
        }

        if (fileChangedLocally) {
            // new file already saved - need to commit
            String comment = rebaser.getCommentForCommit();
            provider.copyFilesFromProjectToRepo(path);
            String newVersion = provider.commitFileAfterVersion(path, headVersion, comment);
            if (newVersion != null) {
                // file was committed good
                saveVersion(projectDir, path, newVersion);
            }
        }
    }

    /**
     * Get version for file.
     */
    private synchronized static String readVersion(File projectRoot, String path) throws Exception {
        Properties p = new Properties();
        File versionsFile = new File(projectRoot, RemoteRepositoryProvider.REPO_SUBDIR
                + "versions.properties");
        if (versionsFile.exists()) {
            FileInputStream in = new FileInputStream(versionsFile);
            try {
                p.load(in);
            } finally {
                in.close();
            }
        }
        return p.getProperty(path);
    }

    /**
     * Update version for file.
     */
    private synchronized static void saveVersion(File projectRoot, String path, String newVersion)
            throws Exception {
        Properties p = new Properties();
        File f = new File(projectRoot, RemoteRepositoryProvider.REPO_SUBDIR + "versions.properties");
        File fNew = new File(projectRoot, RemoteRepositoryProvider.REPO_SUBDIR + "versions.properties.new");
        if (f.exists()) {
            FileInputStream in = new FileInputStream(f);
            try {
                p.load(in);
            } finally {
                in.close();
            }
        }
        p.setProperty(path, newVersion);
        FileOutputStream out = new FileOutputStream(fNew);
        try {
            p.store(out, null);
        } finally {
            out.close();
        }
        move(fNew, f);
    }

    static void move(File f1, File f2) throws Exception {
        if (f1.equals(f2)) {
            return;
        }
        if (f2.exists()) {
            if (!f2.delete()) {
                throw new IOException("Unable to delete " + f2);
            }
        }
        if (!f1.renameTo(f2)) {
            throw new IOException("Unable to rename " + f1 + " to " + f2);
        }
    }

    public interface IRebase {
        /**
         * Rebaser should read and parse BASE version of file. It can't just remember file path because file
         * will be removed after switch into other version. Rebase can be called after that or can not be
         * called.
         */
        void parseBaseFile(File file) throws Exception;

        /**
         * Rebaser should read and parse HEAD version of file. It can't just remember file path because file
         * will be removed after switch into other version. Rebase can be called after that or can not be
         * called.
         */
        void parseHeadFile(File file) throws Exception;

        /**
         * Rebase using BASE, HEAD and non-committed version should be processed. At this time parseBaseFile
         * and parseHeadFile was already called. Keep in mind that this method can display some dialogs to
         * user, i.e. can work up to some minutes.
         */
        void rebaseAndSave(File out) throws Exception;

        /**
         * Construct commit message.
         */
        String getCommentForCommit();
    }
}
