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

    private static final String VERSION_PREFIX = "version-based-on.";

    public static void rebaseAndCommit(RemoteRepositoryProvider provider, File projectDir, String path,
            IRebase rebaser) throws Exception {

        if (!provider.isUnderMapping(path)) {
            throw new RuntimeException("Path is not under mapping: " + path);
        }

        Log.logDebug(LOGGER, "Rebase and commit '" + path + "'");

        final String currentBaseVersion;
        String savedVersion = TeamSettings.get(VERSION_PREFIX + path);
        if (savedVersion != null) {
            currentBaseVersion = savedVersion;
        } else {
            // version wasn't stored - assume latest. TODO Probably need to ask ?
            provider.switchToVersion(path, null);
            currentBaseVersion = provider.getVersion(path);
        }
        final File localFile = new File(projectDir, path);
        final boolean fileChangedLocally;
        {
            File baseRepoFile = provider.switchToVersion(path, currentBaseVersion);
            if (!localFile.exists()) {
                // there is no local file - just use remote
                Log.logDebug(LOGGER, "local file '" + path + "' doesn't exist");
                fileChangedLocally = false;
            } else if (FileUtil.isFilesEqual(baseRepoFile, localFile)) {
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

        final File headRepoFile = provider.switchToVersion(path, null);
        final String headVersion = provider.getVersion(path);
        final boolean fileChangedRemotely;
        {
            if (!localFile.exists()) {
                // there is no local file - just use remote
                if (headRepoFile.exists()) {
                    fileChangedRemotely = true;
                    rebaser.parseHeadFile(headRepoFile);
                } else {
                    // there is no remote file also
                    fileChangedRemotely = false;
                }
            } else if (currentBaseVersion.equals(headVersion)) {
                Log.logDebug(LOGGER, "remote file '" + path + "' wasn't changed");
                fileChangedRemotely = false;
            } else {
                // base and head versions are differ - somebody else committed changes
                Log.logDebug(LOGGER, "remote file '" + path + "' was changed");
                fileChangedRemotely = true;
                rebaser.parseHeadFile(headRepoFile);
            }
        }

        final File tempOut = new File(projectDir, path + "#based_on_" + headVersion);
        if (tempOut.exists() && !tempOut.delete()) {
            throw new Exception("Unable to delete previous temp file");
        }
        boolean needBackup = false;
        if (fileChangedLocally && fileChangedRemotely) {
            // rebase need only in case file was changed locally AND remotely
            Log.logDebug(LOGGER, "rebase and save '" + path + "'");
            needBackup = true;
            rebaser.rebaseAndSave(tempOut);
        } else if (fileChangedLocally && !fileChangedRemotely) {
            // only local changes - just use local file
            Log.logDebug(LOGGER, "only local changes - just use local file '" + path + "'");
        } else if (!fileChangedLocally && fileChangedRemotely) {
            // only remote changes - get remote
            Log.logDebug(LOGGER, "only remote changes - get remote '" + path + "'");
            needBackup = true;
            if (headRepoFile.exists()) {// otherwise file was removed remotelly
                FileUtil.copyFile(headRepoFile, tempOut);
            }
        } else {
            Log.logDebug(LOGGER, "there are no changes '" + path + "'");
            // there are no changes
        }

        if (needBackup) {
            // new file was saved, need to update version
            // code below tries to update file "in transaction" with update version
            if (localFile.exists()) {
                final File bakTemp = new File(projectDir, path + "#oldbased_on_" + currentBaseVersion);
                FileUtil.move(localFile, bakTemp);
            }
            TeamSettings.set(VERSION_PREFIX + path, headVersion);
            if (tempOut.exists()) {
                FileUtil.move(tempOut, localFile);
            }
        }

        if (fileChangedLocally) {
            // new file already saved - need to commit
            String comment = rebaser.getCommentForCommit();
            provider.copyFilesFromProjectToRepo(path, rebaser.getFileCharset(localFile));
            String newVersion = provider.commitFileAfterVersion(path, headVersion, comment);
            if (newVersion != null) {
                // file was committed good
                TeamSettings.set(VERSION_PREFIX + path, newVersion);
            }
        }
    }

    public interface IRebase {
        /**
         * Rebaser should read and parse BASE version of file. It can't just remember file path because file
         * will be removed after switch into other version. Rebase can be called after that or can not be
         * called.
         * 
         * Case for non-exist file: it's correct call. That means file is just created in local box. But after
         * that, remote repository can also contain file, i.e. two users created file independently, then
         * rebase will be called. Implementation should interpret non-exist file as empty data.
         */
        void parseBaseFile(File file) throws Exception;

        /**
         * Rebaser should read and parse HEAD version of file. It can't just remember file path because file
         * will be removed after switch into other version. Rebase can be called after that or can not be
         * called.
         * 
         * Case for non-exist file: it's correct call. That means file was removed from repository.
         * Implementation should interpret non-exist file as empty data.
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

        /**
         * Get charset of file for convert EOL to repository. Implementation can return null if conversion not
         * required.
         */
        String getFileCharset(File file) throws Exception;
    }
}
