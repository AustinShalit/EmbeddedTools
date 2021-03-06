package edu.wpi.first.embeddedtools;

import com.jcraft.jsch.JSch;

import org.apache.log4j.Logger;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.nativeplatform.plugins.NativeComponentPlugin;

import edu.wpi.first.embeddedtools.deploy.DeployPlugin;
import edu.wpi.first.embeddedtools.log.ETLoggerFactory;
import edu.wpi.first.embeddedtools.nativedeps.NativeDepsPlugin;
import edu.wpi.first.embeddedtools.toolchains.ToolchainsPlugin;

public class EmbeddedTools implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("embeddedTools", EmbeddedToolsExtension.class, project);

        ETLoggerFactory.INSTANCE.addColorOutput(project);

        project.getPluginManager().apply(DeployPlugin.class);

        // Only apply the ToolchainsPlugin and NativeDepsPlugin if we're building a native project.
        project.getPlugins().withType(NativeComponentPlugin.class).all(plugin -> {
            Logger.getLogger(this.getClass()).info("Native Project detected: " +plugin.getClass().getName());
            project.getPluginManager().apply(ToolchainsPlugin.class);
            project.getPluginManager().apply(NativeDepsPlugin.class);
        });
    }

    private static JSch jsch;
    public static JSch getJsch() {
        if (jsch == null) jsch = new JSch();
        return jsch;
    }

    public static boolean isDryRun(Project project) {
        return project.hasProperty("deploy-dry");
    }

    public static boolean isSkipCache(Project project) {
        return project.hasProperty("deploy-dirty");
    }
}
