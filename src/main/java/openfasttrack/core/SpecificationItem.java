package openfasttrack.core;

public class SpecificationItem
{
	private final String id;
	private final String artifactType;
	private final String description;

	private SpecificationItem(final String id, final String description,
			final String artifactType)
	{
		this.id = id;
		this.description = description;
		this.artifactType = artifactType;
	}

	public final String getId()
	{
		return id;
	}

	public final String getArtifactType()
	{
		return artifactType;
	}

	public final String getDescription()
	{
		return description;
	}

	public static class Builder
	{
		private final String id;
		private String artifactType;
		private String description;

		public Builder(final String id)
		{
			this.id = id;
		}

		public Builder artifactType(final String artifactType)
		{
			this.artifactType = artifactType;
			return this;
		}

		public Builder description(final String description)
		{
			this.description = description;
			return this;
		}

		public SpecificationItem build()
		{
			return new SpecificationItem(id, description, artifactType);
		}
	}
}