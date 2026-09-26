---
layout: default
title: Console Tracing Report
parent: Reference
grand_parent: User Guide
nav_order: 4
---

### Console Tracing Report

The Console Tracing Report is the standard report format of OFT. Its main purpose is to quickly debug broken tracing links. In this section you learn how to read this report.

Below you see a typical example of a requirement from a design document.

    ok [ in:  2 /  2 ✔ | out:  1 /  1 ✔ ] dsn~cli.tracing.default-format~1 (impl, utest)
    
      The CLI uses plain text as requirement tracing report format if none is given as a parameter.
    
      [covered shallow  ] ← impl~cli.tracing.default-format-2215031703~0
      [covers           ] → req~cli.tracing.default-output-format~1
      [covered shallow  ] ← utest~cli.tracing.default-format-3750270139~0

Let's go through its elements one by one.

The first line is the summary.

It starts with the status of the requirement &mdash; OK in this case.

> **ok** [ in:  2 /  2 ✔ | out:  1 /  1 ✔ ] `dsn~cli.tracing.default-format~1` (impl, utest)

Next we have a couple of numbers.

The first pair shows how many of the incoming good links this requirement has (two), and how many in total (two).

> ok [ **in:  2 /  2 ✔** | out:  1 /  1 ✔ ] `dsn~cli.tracing.default-format~1` (impl, utest)

Consequently, the next pair informs you how many (one) of the overall (one) outgoing links are good.

Please note that OFT cannot predict the exact number of required incoming links, because often we are talking about one-to-many relations. So OFT does not try to. The checkmark and crossmark in the square brackets are only a quick indicator of if the existing links are okay. This goes so far that in case of zero links, no mark is displayed at all.

>  ok [ in:  2 /  2 ✔ | **out:  1 /  1 ✔** ] `dsn~cli.tracing.default-format~1` (impl, utest)

The [Specification Item ID](../../terminology.md#specification-item-id) in the middle is the unique technical ID of this requirement.

>  ok [ in:  2 /  2 ✔ | out:  1 /  1 ✔ ] **dsn****~****cli.tracing.default-format****~****1** (impl, utest)

In the brackets you find which artifact types this item expects as coverage. If the type is covered correctly, you see just the name there. 

>  ok [ in:  2 /  2 ✔ | out:  1 /  1 ✔ ] `dsn~cli.tracing.default-format~1` (**impl, utest**)

If it is not covered, the name is lead in by a minus:

>  **not ok** &hellip;  (**-impl**, utest)
 
If an artifact type provides coverage that is not requested, you find this indicated with a plus in front.

> **not ok** &hellip; (impl, **+itest**, utest)

<a name="transitive-defects"></a>
If an item is covered correctly, but one of the items it covers has a defect itself, this is called a transitive defect. In this case, the item is marked as `not ok (transitive)`:

> **not ok (transitive)** &hellip; (impl, utest)

If there were any other specification objects defined with the same ID, you would see the following at the end of the summary line:

> [has 3 duplicates]

Everything after that line are details of the requirement. Indented text indicates this. The first part of the details is the description.

    The CLI uses plain text as requirement tracing report format if none is given as a parameter.

The section with the arrows provides details about incoming and outgoing links. Arrows pointing to the left are incoming links, arrows pointing to the right are outgoing. You can easily remember this, since the arrows either point towards the ID of the connected specification item or away from it.

The following line means that this design requirement is covered in the implementation.

> [covered shallow  ] ← `impl~cli.tracing.default-format-2215031703~0`

The ID of the implementation comes from the Tag Importer and is for its most part auto-generated. The artifact type `dsn` is simply replaced by `impl` here and a number is attached for disambiguation.

> [covered shallow  ] ← **impl**~cli.tracing.default-format-**2215031703**~0

In the square brackets you find the status of the link.

Just in case you are wondering about the extra spaces in some places of the report, those exist as padding to align multiple similar items in lists.
