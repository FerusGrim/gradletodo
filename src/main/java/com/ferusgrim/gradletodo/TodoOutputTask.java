package com.ferusgrim.gradletodo;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryTree;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.internal.impldep.com.beust.jcommander.internal.Maps;
import org.gradle.internal.impldep.com.google.common.base.Charsets;
import org.gradle.internal.impldep.com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodoOutputTask extends DefaultTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(GradleTodo.class);
    private static final Pattern TODO_PATTERN = Pattern.compile("(//.*?\\bTODO\\b)(.*$)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    @InputFiles private SourceDirectorySet input;

    @TaskAction
    public void run() throws IOException {
        final PatternSet patternSet = new PatternSet();
        patternSet.setIncludes(this.input.getIncludes());
        patternSet.setExcludes(this.input.getExcludes());

        for (final DirectoryTree dirTree : this.input.getSrcDirTrees()) {
            final File dir = dirTree.getDir();

            if (!dir.exists() || !dir.isDirectory()) {
                continue;
            }

            final FileTree tree = this.getProject().fileTree(dir).matching(this.input.getFilter()).matching(patternSet);

            for (final File file : tree) {
                final Map<Integer, String> matches = this.getFileMatches(Files.toString(file, Charsets.UTF_8));

                if (!matches.isEmpty()) {
                    System.out.println();
                    LOGGER.warn("{} TODOs found: {}", matches.size(), file.getName());

                    for (final Map.Entry<Integer, String> match : matches.entrySet()) {
                        LOGGER.info("Line {}: {}", match.getKey(), match.getValue());
                    }
                }
            }
        }
    }

    private Map<Integer, String> getFileMatches(String source) {
        final Map<Integer, String> matches = Maps.newHashMap();

        int lineNumber = 0;
        for (final String line : source.split("\\r?\\n|\\r")) {
            lineNumber += 1;
            final Matcher matcher = TODO_PATTERN.matcher(line);

            if (matcher.find()) {
                matches.put(lineNumber, matcher.group(2));
            }
        }

        return matches;
    }
}
