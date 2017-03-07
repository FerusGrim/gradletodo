/*
 * This file is part of GradleTodo, licensed under the MIT License.
 * Copyright 2017 FerusGrim
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.ferusgrim.gradletodo;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryTree;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.util.PatternSet;
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
                    LOGGER.warn("{} TODO{} found in `{}`", matches.size(), matches.size() == 1 ? "" : "s", file.getName());

                    for (final Map.Entry<Integer, String> match : matches.entrySet()) {
                        LOGGER.warn("L{}: {}", match.getKey(), match.getValue());
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

    void setInput(final SourceDirectorySet input) {
        this.input = input;
    }
}
