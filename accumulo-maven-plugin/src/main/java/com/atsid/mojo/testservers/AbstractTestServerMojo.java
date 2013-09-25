package com.atsid.mojo.testservers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wagon.repository.Repository;

public abstract class AbstractTestServerMojo extends AbstractMojo {

	private List<String> resolvedClasspath;

	/**
	 * @component
	 * @readonly
	 */
	ArtifactMetadataSource source;

	/**
	 * @component
	 * @readonly
	 */
	private ArtifactResolver artifactResolver;
	/**
	 * 
	 * @parameter property="localRepository"
	 * @readonly
	 */
	private ArtifactRepository localRepository;
	/**
	 * 
	 * @parameter property="project.remoteArtifactRepositories"
	 * @required
	 * @readonly
	 */
	private List<Repository> pomRemoteRepositories;

	/**
	 * @parameter property="project"
	 */
	protected MavenProject project;

	/**
	 * @component
	 * @readonly
	 */
	private ArtifactFactory artifactFactory;
	/**
	 * @parameter property="plugin.artifacts"
	 */
	private List<Artifact> artifacts;

	public AbstractTestServerMojo() {
		super();
	}

	@SuppressWarnings("unchecked")
	public List<String> resolveClasspath() throws Exception {

		if (resolvedClasspath == null) {
			Artifact dummyOriginatingArtifact = artifactFactory
					.createBuildArtifact("org.apache.maven.plugins",
                            "maven-downloader-plugin", "1.0", "jar");

			Set<Artifact> lookupArtifacts = new HashSet<Artifact>(artifacts);
			ArtifactResolutionResult result = artifactResolver
					.resolveTransitively(lookupArtifacts,
                            dummyOriginatingArtifact, pomRemoteRepositories,
                            localRepository, source);
			resolvedClasspath = (List<String>) CollectionUtils.collect(
					result.getArtifacts(), new Transformer() {
						public Object transform(Object input) {
							return ((Artifact) input).getFile().getPath();
						}
					});

			resolvedClasspath.add(project.getArtifact().getFile().getPath());
		}
		return resolvedClasspath;
	}

}
