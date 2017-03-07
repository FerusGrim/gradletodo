package com.ferusgrim.gradletodo;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.internal.impldep.com.google.common.collect.ImmutableMap;

public class GradleTodo implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        this.makeTask(project);
    }

    @SuppressWarnings("unchecked")
    private <T extends Task> T makeTask(final Project project) {
        return (T) project.task(ImmutableMap.of("name", "todo", "type", TodoOutputTask.class), "todo");
    }
}
