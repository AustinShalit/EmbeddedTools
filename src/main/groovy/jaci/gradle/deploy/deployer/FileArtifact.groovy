package jaci.gradle.deploy.deployer

import jaci.gradle.deploy.DeployContext
import jaci.gradle.deploy.cache.Cacheable

class FileArtifact extends ArtifactBase implements Cacheable {
    FileArtifact(String name) {
        super(name)
    }

    File file       = null
    String filename = null

    @Override
    void deploy(DeployContext ctx) {
        ctx.sendFile(file, (filename == null ? file.name : filename), cache)
    }
}
