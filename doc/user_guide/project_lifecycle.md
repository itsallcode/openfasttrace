# Project Lifecycle

This is a free and open-source project. Updates are publicly available and free of charge.

Feature, documentation, bugfix and security updates are always provided with the latest release.

## End of Life

This project uses [semantic versioning](https://semver.org/). Versions with the same major version are guaranteed to be backward-compatible to previous versions with that major version.

Minor version updates add features that do not break compatibility and do not change hardware or software environment requirements beyond reasonable update rules. Fix versions only resolve bugs and / or add security updates.

| Version line | First release | End of support | Java version |
|--------------|---------------|----------------|--------------|
| 0.x.y        | 2017-08-13    | 2018-06-30     | 8            |
| 1.x.y        | 2018-06-30    | 2018-10-13     | 8            |
| 2.x.y        | 2018-10-13    | 2020-04-21     | 8            |
| 3.x.y        | 2020-04-21    | 2024-06-03     | 11           |
| 4.x.y        | 2024-06-03    | 2027-10-01     | 17           |
| 5.x.y        | 2027-10-01    | 2029-12-01     | 21           |

We are synchronizing the EoL with the [Temurin LTS release support](https://adoptium.net/support/). Our strategy is to take a Java version that is mature and stable and long enough out to be available on the majority of platforms and machines and then support it as long as the Temurin project supports the JRE. This way users don't need the latest top-of-the-line installations to run OFT.

## Planned Deprecations and Removals

### Features Scheduled for Removal in OFT 5.0.0

The **SpecObject** format is a legacy from ReqM2. This includes importer, exporter and `aspec` reporter. It lacks the clear structure of OFT's specification item trace model and is not very consistent. Also, JSON is now a more popular base format than XML, that's why we will replace the SpecObject format with OFT's own interchange format.

Switch from the SpecObject format to `.oftx.json` when you want to aggregate and exchange specification documents between projects. Generate `.oftr.json` reports instead of `aspec`.

The `.oftx.json` exchange format and `.oftr.json` report format will be available no later than September 30, 2026. SpecObject will then be deprecated and remain supported throughout OFT 4.x. The SpecObject importer, exporter, and aspec reporter will be removed in OFT 5.0.

**Short tags** are another ReqM2 legacy. While this is a little less typing effort, it breaks our rule of writing out specification item IDs. This is inconvenient for text searches. Also, short tags are the only reason why we need per directory configuration. OFT is designed to use auto-detection wherever possible, and this feature works against the auto-detection. We will remove the configuration together with the short tags.

Migrate to OFT's full coverage tags, and you can drop the per-directory tag-import configuration. Short tags will be officially deprecated with OFT 4.10.0.

## Security Updates

Users need to check the [changelog](../changes/changes.md) to stay informed about security updates. You need to install the provided security updates in a timely manner to keep your setup secure. This is also true for any dependencies of this software that do not come bundled. An example is the Java Runtime Environment.

Itsallcode.org provides security updates until the EoL listed above.

Please refer to our [security policy](../../SECURITY.md) for details on coordinated vulnerability disclosure.

### Retaining Updates

Itsallcode.org distributes updates via GitHub releases. Even if the project should be archived, the releases remain accessible for download. Itsallcode.org will keep each security update accessible for at least 10 years.