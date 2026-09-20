# oft(1)

## NAME

oft - trace specification item coverage and convert requirements formats

## SYNOPSIS

```text
oft trace [OPTION]... [INPUT_FILE_OR_DIRECTORY]...
oft convert [OPTION]... [INPUT_FILE_OR_DIRECTORY]...
oft help
oft --help
```

## DESCRIPTION

OpenFastTrace (OFT) is a requirement tracing tool. It reads specification
artifacts and coverage tags from input files or directories, then checks the
coverage links between specification items.

Use `oft trace` to create a tracing report. Use `oft convert` to convert
requirements to another format. With no output file, OFT writes the result to
standard output.

An input path may name a file or directory. OFT detects supported input formats
from their extensions. Its native specification formats are Markdown and
reStructuredText; source and markup files can contain coverage tags in comments.
OFT also supports Gherkin and the legacy SpecObject XML format inherited from
ReqM2.

A specification item is the atomic unit of a specification. Coverage is the
relationship between a specification item and the items that detail, implement,
or verify it. A coverage requester demands coverage, while a coverage provider
fulfills that demand.

## COMMANDS

`trace`
: Create a tracing report. The default report format is `plain`.

`convert`
: Convert requirements to another requirements format. The default format is
  `specobject`.

`help`, `-h`, `--help`
: Display command-line usage and the OFT version.

## TRACE OPTIONS

`-o`, `--output-format FORMAT`
: Select the report format: `plain`, `html`, or `aspec`. The default is
  `plain`.

`-v`, `--report-verbosity LEVEL`
: Select the amount of report content. The default is `failure_details`.
  `LEVEL` is one of:

  `quiet`
  : Produce no report output; use the exit status only.

  `minimal`
  : Print `ok` or `not ok`.

  `summary`
  : Print only the summary.

  `failures`
  : List specification items with defects.

  `direct_failures`
  : List specification items with direct, non-transitive defects.

  `failure_summaries`
  : Print summaries for specification items with defects.

  `direct_failure_summaries`
  : Print summaries for specification items with direct, non-transitive
    defects.

  `failure_details`
  : Print summaries and details for specification items with defects.

  `direct_failure_details`
  : Print summaries and details for specification items with direct,
    non-transitive defects.

  `overview`
  : Print summaries, coverage-link details, and tags for all specification
    items, without their descriptions.

  `all`
  : Print summaries and details for all specification items.

`--details-section-display STATUS`
: Set the initial display status for the details section in an HTML report.
  `STATUS` is `collapse` (the default) or `expand`.

## CONVERT OPTIONS

`-o`, `--output-format FORMAT`
: Select the requirements format. `FORMAT` is `specobject`, which is the
  default.

  SpecObject is a legacy ReqM2 format. The SpecObject importer and exporter,
  along with the `aspec` reporter, are scheduled for removal in OFT 5.0.0.
  Migrate interchange documents to `.oftx.json` and reports to `.oftr.json`
  when those formats are available.

`-s`, `--show-origin`
: Include the origin of each specification item, for example its file and line
  number.

## COMMON OPTIONS

`-a`, `--wanted-artifact-types TYPES`
: Import only specification items whose artifact type is in the
  comma-separated list `TYPES`.

`-c`, `--color-scheme SCHEME`
: Select the output color scheme: `black-and-white`, `monochrome`, or `color`.
  The default is `color`. This option is ignored when `-f` is set.

`-f`, `--output-file PATH`
: Write output to `PATH` instead of standard output. If the output consists of
  more than one file, `PATH` is the output path.

`-w`, `--wanted-statuses STATUSES`
: Import only specification items whose status is in the comma-separated list
  `STATUSES`.

`-n`, `--newline FORMAT`
: Set the newline format to `unix`, `windows`, or `oldmac`.

`-t`, `--wanted-tags [_,]TAGS`
: Import only specification items that have at least one tag in the
  comma-separated list `TAGS`. Prefix the list with `_,` to include items that
  have no tags.

`-l`, `--log-level LEVEL`
: Set the console log level to one of `OFF`, `SEVERE`, `WARNING`, `INFO`,
  `CONFIG`, `FINE`, `FINER`, `FINEST`, or `ALL`. The default is `WARNING`.

## EXIT STATUS

`0`
: Success.

`1`
: OFT error.

`2`
: Command-line error.

## EXAMPLES

Create a plain-text tracing report for all supported files below `spec/` and
`src/`:

```sh
oft trace spec src
```

Create an HTML report with all specification items and initially expanded
details:

```sh
oft trace --output-format html --report-verbosity all \
    --details-section-display expand --output-file report.html spec src
```

Check only approved requirements and write a compact result suitable for a
script:

```sh
oft trace --wanted-statuses approved --report-verbosity minimal spec src
```

Trace only the specification items tagged `authentication`:

```sh
oft trace --wanted-tags authentication spec src
```

Split tracing work by checking only features and requirements at the
requirements level:

```sh
oft trace --wanted-artifact-types feat,req spec src
```

## FILES

Markdown files with the `.md` or `.markdown` extension are native
specification artifacts. In Markdown, place a full coverage tag in a standalone
HTML comment, for example:

```markdown
<!-- [impl->dsn~validate-authentication-request~1] -->
```

## SEE ALSO

The [OpenFastTrace homepage](https://openfasttrace.itsallcode.org), the
[OpenFastTrace User Guide](https://openfasttrace.itsallcode.org/user_guide/user_guide.html),
the [OpenFastTrace GitHub project](https://github.com/itsallcode/openfasttrace),
and the OpenFastTrace terminology reference.
