/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2014 Alex Buloichik
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

import gen.core.project.RepositoryDefinition;
import gen.core.project.RepositoryMapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.lucene.analysis.kr.utils.StringUtil;
import org.omegat.core.data.ProjectProperties;
import org.omegat.util.FileUtil;
import org.omegat.util.StaticUtils;

/**
 * Class for process some repository commands.
 * 
 * Path, local path, repository path can be directory or one file only. Directory should be declared like
 * 'source/', file should be declared like 'source/text.po'.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class RemoteRepositoryProvider {
    public static final String REPO_SUBDIR = ".repositories/";

    final File projectRoot;
    final List<RepositoryDefinition> repositoriesDefinitions;
    final List<IRemoteRepository2> repositories = new ArrayList<IRemoteRepository2>();

    public RemoteRepositoryProvider(ProjectProperties config) throws Exception {
        projectRoot = config.getProjectRootDir();
        repositoriesDefinitions = config.getRepositories();

        checkDefinitions();
        initializeRepositories();
    }

    /**
     * Check repository definitions in the project. TODO: define messages for user
     */
    protected void checkDefinitions() {
        Set<String> dirs = new TreeSet<String>();
        for (RepositoryDefinition r : repositoriesDefinitions) {
            if (StringUtil.isEmpty(r.getUrl())) {
                throw new RuntimeException("There is no repository url");
            }
            if (!dirs.add(getRepositoryDir(r).getAbsolutePath())) {
                throw new RuntimeException("Duplicate repository URL");
            }
            for (RepositoryMapping m : r.getMapping()) {
                if (m.getLocal() == null || m.getRepository() == null) {
                    throw new RuntimeException("Wrong mapping");
                }
                if (m.getLocal().startsWith("/") || m.getRepository().startsWith("/")) {
                    throw new RuntimeException("Wrong mapping");
                }
                boolean localIsDir = m.getLocal().endsWith("/") || m.getLocal().isEmpty();
                boolean repositoryIsDir = m.getRepository().endsWith("/") || m.getRepository().isEmpty();
                if (localIsDir != repositoryIsDir) {
                    throw new RuntimeException("Wrong mapping");
                }
            }
        }
    }

    /**
     * Initialize repositories instances.
     */
    protected void initializeRepositories() throws Exception {
        for (RepositoryDefinition r : repositoriesDefinitions) {
            IRemoteRepository2 repo = RemoteRepositoryFactory.create(r.getType());
            repo.init(r, getRepositoryDir(r));
            repositories.add(repo);
        }
    }

    /**
     * Find mappings for specified path.
     */
    protected List<Mapping> getMappings(String path) {
        List<Mapping> result = new ArrayList<Mapping>();
        for (int i = 0; i < repositoriesDefinitions.size(); i++) {
            RepositoryDefinition rd = repositoriesDefinitions.get(i);
            for (RepositoryMapping repoMapping : rd.getMapping()) {
                Mapping m = new Mapping(path, repositories.get(i), rd, repoMapping);
                if (m.matches()) {
                    result.add(m);
                }
            }
        }
        return result;
    }

    /**
     * Found mapping must be one.
     */
    protected Mapping oneMapping(String path) {
        List<Mapping> mappings = getMappings(path);
        if (mappings.size() > 1) {
            throw new RuntimeException("Multiple mapping for file");
        } else if (mappings.size() == 0) {
            throw new RuntimeException("There is no mapping for file");
        }

        return mappings.get(0);
    }

    /**
     * Checks if path is under mapping.
     */
    public boolean isUnderMapping(String path) {
        return getMappings(path).size() > 0;
    }

    /**
     * Switch all repositories into latest version.
     */
    public void switchAllToLatest() throws Exception {
        for (IRemoteRepository2 r : repositories) {
            r.switchToVersion(null);
        }
    }

    /**
     * Switch repository that contains path to specified version. If version is null, need to switch to latest
     * version.
     */
    public File switchToVersion(String filePath, String version) throws Exception {
        return oneMapping(filePath).switchToVersion(version);
    }

    /**
     * Commit specific file after rebase. Used for omegat/project_save.tmx, glossaries, etc.
     */
    public String commitFileAfterVersion(String path, String version, String commentText) throws Exception {
        return oneMapping(path).repo.commit(version, commentText);
    }

    /**
     * Commit set of files without rebase - just local version. Used for target/*, etc.
     */
    public void commitFiles(String path, String commentText) throws Exception {
        Map<String, IRemoteRepository2> repos = new TreeMap<String, IRemoteRepository2>();
        // collect repositories one for one mapping
        for (Mapping m : getMappings(path)) {
            repos.put(m.repoDefinition.getUrl(), m.repo);
        }
        // commit only unique repositories
        for (IRemoteRepository2 repo : repos.values()) {
            repo.commit(null, commentText);
        }
    }

    /**
     * Copy all mappings that under specified directory path into project directory.
     * 
     * @param localPath
     *            directory name(that should be ended by '/'), or file name
     */
    public void copyFilesFromRepoToProject(String localPath) throws Exception {
        if (localPath.startsWith("/")) {
            throw new RuntimeException("Wrong path mapping");
        }
        for (Mapping m : getMappings(localPath)) {
            m.copyFromRepoToProject();
        }
    }

    /**
     * Copy all mappings that under specified directory path into repository directory.
     * 
     * @param localPath
     *            directory name(that should be ended by '/'), or file name
     */
    public void copyFilesFromProjectToRepo(String localPath) throws Exception {
        if (localPath.startsWith("/")) {
            throw new RuntimeException("Wrong path mapping");
        }
        for (Mapping m : getMappings(localPath)) {
            m.copyFromProjectToRepo();
        }
    }

    /**
     * Get version of specified file.
     */
    public String getVersion(String file) throws Exception {
        return oneMapping(file).getVersion();
    }

    protected void copyFile(File from, File to) throws IOException {
        FileUtil.copyFile(from, to);
    }

    protected void addForCommit(IRemoteRepository2 repo, String path) throws Exception {
        repo.addForCommit(path);
    }

    protected File getRepositoryDir(RepositoryDefinition repo) {
        String path = repo.getUrl().replaceAll("[^A-Za-z0-9\\.]", "_").replaceAll("__+", "_");
        return new File(new File(projectRoot, REPO_SUBDIR), path);
    }

    /**
     * Class for mapping by specified local path.
     */
    class Mapping {
        final String filterPrefix;
        final IRemoteRepository2 repo;
        final RepositoryDefinition repoDefinition;
        final RepositoryMapping repoMapping;

        public Mapping(String path, IRemoteRepository2 repo, RepositoryDefinition repoDefinition,
                RepositoryMapping repoMapping) {
            this.repo = repo;
            this.repoDefinition = repoDefinition;
            this.repoMapping = repoMapping;
            /**
             * Find common part - it should be one of path or local. If path and local have only common begin,
             * they will not be mapped. I.e. path=source/ and local=source/one - it's okay, path=source/one/
             * and local=source/ - also okay, but path=source/one/ and local=source/two - wrong.
             */
            if (path.isEmpty()) {
                // root(full project path) mapping
                filterPrefix = "";
            } else if (repoMapping.getLocal().equals(path)) {
                // path equals mapping (path="source/" for "source/"=>"...")
                filterPrefix = "";
            } else if (repoMapping.getLocal().startsWith(path) && path.endsWith("/")) {
                // path shorter than local and is directory (path="source/" for "source/first/..."=>"...")
                filterPrefix = "";
            } else if (path.startsWith(repoMapping.getLocal()) && repoMapping.getLocal().endsWith("/")) {
                // local is shorter than path and is directory (path="omegat/project_save" for
                // "omegat/"=>"...")
                filterPrefix = path.substring(repoMapping.getLocal().length());
            } else if (repoMapping.getLocal().isEmpty()) {
                // root(full project path) mapping (""=>"...")
                filterPrefix = path;
            } else {
                // otherwise path doesn't correspond with repoMapping
                filterPrefix = null;
            }
        }

        /**
         * Is path matched with mapping ?
         */
        public boolean matches() {
            return filterPrefix != null;
        }

        public void copyFromRepoToProject() throws Exception {
            if (filterPrefix == null) {
                throw new RuntimeException("Doesn't matched");
            }
            File from = new File(getRepositoryDir(repoDefinition), repoMapping.getRepository());
            File to = new File(projectRoot, repoMapping.getLocal());
            if (repoMapping.getRepository().endsWith("/") || repoMapping.getRepository().isEmpty()) {
                // directory mapping
                copy(from, to, filterPrefix, repoMapping.getIncludes(), repoMapping.getExcludes());
            } else {
                // file mapping
                if (!filterPrefix.isEmpty()) {
                    throw new RuntimeException();
                }
                copyFile(from, to);
            }
        }

        public void copyFromProjectToRepo() throws Exception {
            if (filterPrefix == null) {
                throw new RuntimeException("Doesn't matched");
            }
            File from = new File(projectRoot, repoMapping.getLocal());
            File to = new File(getRepositoryDir(repoDefinition), repoMapping.getRepository());
            if (repoMapping.getRepository().endsWith("/") || repoMapping.getRepository().isEmpty()) {
                // directory mapping or full mapping
                List<String> files = copy(from, to, filterPrefix, repoMapping.getIncludes(),
                        repoMapping.getExcludes());
                for (String f : files) {
                    addForCommit(repo, repoMapping.getRepository() + f);
                }
            } else {
                // file mapping
                if (!filterPrefix.isEmpty()) {
                    throw new RuntimeException();
                }
                copyFile(from, to);
                addForCommit(repo, repoMapping.getRepository());
            }
        }

        public File switchToVersion(String version) throws Exception {
            repo.switchToVersion(version);
            File to = new File(getRepositoryDir(repoDefinition), repoMapping.getRepository());
            return new File(to, filterPrefix);
        }

        public String getVersion() throws Exception {
            return repo.getFileVersion(repoMapping.getRepository() + filterPrefix);
        }

        protected List<String> copy(File from, File to, String prefix, List<String> includes,
                List<String> excludes) throws Exception {
            List<String> relativeFiles = StaticUtils.buildRelativeFilesList(from, includes, excludes);
            List<String> copied = new ArrayList<String>();
            for (String rf : relativeFiles) {
                if (rf.startsWith("/")) {
                    rf = rf.substring(1);
                }
                if (rf.startsWith(".repositories/")) {
                    continue; // list from root - shouldn't travel to .repositories/
                }
                if (prefix.isEmpty()) {
                    // there is no filter
                    copyFile(new File(from, rf), new File(to, rf));
                    copied.add(rf);
                } else if (prefix.endsWith("/")) {
                    // prefix is directory
                    if (rf.startsWith(prefix)) {
                        copyFile(new File(from, rf), new File(to, rf));
                        copied.add(rf);
                    }
                } else {
                    // prefix is file
                    if (rf.equals(prefix)) {
                        copyFile(new File(from, rf), new File(to, rf));
                        copied.add(rf);
                    }
                }
            }
            return copied;
        }
    }
}
