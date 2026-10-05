---
layout: default
title: Frequently Asked Questions
parent: Developer Guide
nav_order: 7
---

# Frequently Asked Questions (FAQ)

## License

### Does OpenFastTrace Cost Anything?

No. OpenFastTrace is free software. "Free as in freedom" and "free as in beer".

### Will you Ever Charge for OpenFastTrace?

No. OpenFastTrace is non-commercial since its conception in 2015 and will remain so.

### Why is OpenFastTrace Licensed Under the GPL v3?

This is free software. We don't ask you for money. If you build on top of OpenFastTrace, we ask you to share your improvements with the community. The GPL is the best way to ensure that OpenFastTrace remains free and accessible to everyone.

### I Want to Build on the OFT Sources in a Commercial Project. Can I?

Yes. But you need to share your improvements with the community in accordance with the GPL v3.

### I Want to Use OFT in a Commercial Project. Can I?

Of course. If you only call the OFT binaries, you don't need to share your improvements with the community.

Please note, however, that embedding the OFT library into your own program is considered a derivative work and thus requires you to share your improvements with the community in accordance with the GPL v3.

### I Don't Like GPL v3. Can't you License it differently?

The GPL v3 is the best way to ensure that OpenFastTrace remains free and accessible to everyone. If you do not agree with our license, please pick a different tool.

### Why are the AI Skills Licensed Under MIT License Then?

Users typically heavily customize AI skills. Us picking the MIT license acknowledges that there is often little value in sharing these customizations. We are still happy if you share your improvements with the community, though.

### Can I use the OFT Logo in my Project? I Want to Build an Integration.

Sure. As long as you attribute OpenFastTrace, go ahead!

## Goals and Features

### What are the Goals of OpenFastTrace?

Above all else, OFT aims to be reliable. Requirement tracing is often found in safety and security-critical systems.

The second goal is speed. OFT is fast. It's in the name. Only if requirement tracing is fast, people integrate it into their build workflows.

Third, OFT is designed to run on any operating system and hardware platform. Requirement tracing is a universal need of software projects, and we do not want to exclude anyone from using it.

## Design

### Why Java?

Java is platform-independent. Java is also very reliable. There is a reason why Java is the language of choice for the enterprise world. Java runtimes are available for pretty much any machine, from servers to tablets.

Also, Java comes with a full-featured standard library. This allows OFT to come without any additional dependencies.

### Why is Feature XYZ not Implemented?

Our main goal is and remains reliability. We are intentionally conservative in our feature set.

Also, we believe in the Unix philosophy. We do not want to add features that are already available in other tools. Instead, we encourage users to combine OFT with other tools to achieve the desired functionality.

### What if I Need a Certain Feature?

You can use the project's issue tracker to request new features. We are open to adding new features if they align with our goals and do not introduce additional complexity or security risks.

### Why Does OFT not Have Network Functionality?

OFT is designed to be a lightweight tool that focuses on reliability and speed. We believe that network functionality is not necessary for the majority of use cases and can introduce additional complexity and potential security risks. Instead, we encourage users to combine OFT with other tools that provide network functionality if needed.

Also, enterprise projects often have tight security constraints. We do not want to introduce additional security risks by adding network functionality to OFT.

Last but not least, we believe in privacy. OFT has no tracking or telemetry. Like so many other tools, it does not need them. We are consequent enough to acknowledge that and do not want to snoop on our users.

### Why did OFT Choose the Specification Item ID Format?

OFT uses the following format:

```
<artifact-type>~<specification-item-name>~<revision-number>
```

This format does not contain any characters that are interpreted as comment markers in any programming language, which is very important for reliable parsing.

It is also very easy to read and understand. The double tilde (`~`) usually does not appear in any code, especially not with the square brackets around it that we use in coverage markers. Again, this is to avoid any potential parsing issues.

### Why is the Revision Number not a Semantic Version?

Because it does not need to be. Version control happens outside of OFT. The revision number serves only one puropose: mark when a requirement is **semantically changed**. You don't need to track minor or fix versions in a specification item. As long as the semantics did not change, it is still the same requirement.

Not using semantic versioning with multiple digits was an intentional design decision. It simplifies the format and avoids potential parsing issues.

## Using OFT

### Which Platforms Does OFT Support?

All platforms for which a Java runtime is available. So pretty much everywhere: Linux, macOS, Windows, mobile operating systems and more.

### Where can I get OFT?

Check out our [user guide](https://openfasttrace.itsallcode.org/user_guide/installation/installation.html) to learn how to install OFT.

### How do I get Started?

We recommend watching the [3-minute video tutorial](https://www.youtube.com/watch?v=tlzMT6RaVWA).

Then, proceed to the [use cases section](https://openfasttrace.itsallcode.org/user_guide/use_cases/use_cases.html) of the user guide.

### What Except OFT do I Need?

Minimally: a text editor and a terminal. Using a [supported IDE](https://openfasttrace.itsallcode.org/user_guide/tool_support/tool_support.html) makes it a lot more convenient to work with OFT though.

### Can I use OFT With AI?

Yes. OFT is a command line tool with a well-defined interface that can be easily integrated with AI systems. Check our [AI Skills repository](https://github.com/itsallcode/openfasttrace-ai-skills) to get started quickly.

### Why is There no MCP Server?

Because it does not add any real benefit over the existing CLI and skills. LLMs have no problems writing OFT specification items or coverage markers. They can also read the reports just fine.

### Can't I Simply Trace Requirements With AI?

If you do not mind that the outcome is non-deterministic, then yes, you can. It's a risky move, and you should definitely not do that in a safety or security-critical environment.

We recommend using OFT to get fast and reliable traces.

Also, using OFT is a **lot** faster and saves you tokens.

Consider it a safety harness around handling requirements with AI.

AIs forget and make mistakes. OFT helps mitigate that.

## General Questions

### How old is OpenFastTrace?

OFT started at the end of 2015 and the first release was in 2016. See more in our [About Us](../about_us.md) article. Since then, it has always been in active development.

### What Does the OFT Logo Mean?

We probably do not need to explain the letters "OFT" in the logo. The double chevron symbol has two meanings: it represents chaining requirements. And — as the older among our community might remember — it is also the fast-forward symbol found on video players or audio decks. So it's also a symbol for speed.

The corners are a result of the fact that the first logo was ASCII art. We liked how it looked and decided to keep it.

### How Does OFT Compare to Other Tools?

We don't believe in telling users which tools to use. Instead, we encourage users to evaluate OFT against their specific needs and compare it to other tools they are already using. The whole point of free software is choice.

One thing that certainly speaks in favor of OFT is its maturity.
