<head><link href="oft_spec.css" rel="stylesheet"></link></head>

![oft-logo](../src/main/resources/openfasttrack_logo.svg)

# OpenFastTrace User Guide

## In a Nutshell


## What is Requirement Tracing?

OpenFastTrace is a requirement tracing suite. Requirement tracing helps you keeping track of whether you actually implemented everything you planned to in your specifications. It also identifies obsolete parts of your product and helps you getting rid of them.

The foundation of all requirement tracing are links between documents, implementation, test, reports and whatever other artifacts your product consists of.

Let's assume you compiled a list of five main features your users asked for. They are very coarse but provide a nice overview of what your project is expected to achieve. Next you decide to write a few dozen user stories to flesh out the details of what your users want.

In order not to forget anything important, you create a link from each user story to the corresponding feature.

After you are done you want to make sure everything is in order. Instead of checking all the links by hand, you let OFT check if every feature is covered in at least one user story.

OFT comes back with a result that one of your features is not covered. You realize that you indeed forgot that one, write two new user stories and link them to the sofar uncovered feature. This time OFT comes back with an assuring "OK".

Step-by-step you repeat this pattern for your design document and all resulting artifacts.

## Why do I Need Requirement Tracing?

Requirement tracing is a safety net for non-trivial software projects:

* protects you from forgetting planned parts of your product
* finds orphaned code, documents and resources
* helps you track progress towards milestones
* allows you to prove due diligence during quality audits and customer reviews

