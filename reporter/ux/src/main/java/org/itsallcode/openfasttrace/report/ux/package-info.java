/**
 * OpenFastTrace UX Reporter Package.
 *
 * <p>This package provides the UX (User Experience) reporter functionality for OpenFastTrace,
 * which generates JavaScript data structures for interactive web-based visualization and analysis of specification
 * traceability.</p>
 *
 * <h2>Overview</h2>
 *
 * <p>The UX reporter transforms OpenFastTrace's internal specification item model into a
 * JavaScript data structure that can be consumed by web applications for interactive traceability visualization. The
 * main components are:</p>
 *
 * <ul>
 * <li>{@link org.itsallcode.openfasttrace.report.ux.UxReporter} - Main reporter implementation</li>
 * <li>{@link org.itsallcode.openfasttrace.report.ux.Collector} - Transforms specification items into UX model</li>
 * <li>{@link org.itsallcode.openfasttrace.report.ux.generator.JsGenerator} - Generates JavaScript output</li>
 * <li>{@link org.itsallcode.openfasttrace.report.ux.model.UxModel} - Container for project metadata and items</li>
 * <li>{@link org.itsallcode.openfasttrace.report.ux.model.UxSpecItem} - Individual specification item data</li>
 * </ul>
 *
 * <h2>Generated JavaScript Data Structure</h2>
 *
 * <p>The {@link org.itsallcode.openfasttrace.report.ux.generator.JsGenerator} produces a JavaScript object
 * with the following structure:</p>
 *
 * <pre>{@code
 * window.specitem = {
 *   project: { project metadata  },
 *   specitems: [ array of specification items
 * }
 * }</pre>
 *
 * <p>The {@code project} object contains high-level information about the specification project:</p>
 *
 * <table border="1">
 * <caption>Project Object Fields</caption>
 * <tr><th>Field</th><th>Type</th><th>Description</th></tr>
 * <tr><td>projectName</td><td>string</td><td>Name of the project</td></tr>
 * <tr><td>types</td><td>string[]</td><td>Array of artifact types (e.g., "itest", "feat", "req", "arch", "utest")</td></tr>
 * <tr><td>tags</td><td>string[]</td><td>Array of all tags used in the project</td></tr>
 * <tr><td>status</td><td>string[]</td><td>Array of possible item statuses ("approved", "proposed", "draft", "rejected")</td></tr>
 * <tr><td>wrongLinkNames</td><td>string[]</td><td>Array of wrong link type names ("version", "orphaned", "unwanted")</td></tr>
 * <tr><td>item_count</td><td>number</td><td>Total number of specification items</td></tr>
 * <tr><td>item_covered</td><td>number</td><td>Number of items that are covered</td></tr>
 * <tr><td>item_uncovered</td><td>number</td><td>Number of items that are uncovered</td></tr>
 * <tr><td>type_count</td><td>number[]</td><td>Count of items per type (indexed by types array)</td></tr>
 * <tr><td>uncovered_count</td><td>number[]</td><td>Count of uncovered items per type</td></tr>
 * <tr><td>status_count</td><td>number[]</td><td>Count of items per status</td></tr>
 * <tr><td>tag_count</td><td>number[]</td><td>Count of items per tag</td></tr>
 * </table>
 *
 * <h3>Specification Items Array</h3>
 *
 * <p>Each entry in the {@code specitems} array represents a single specification item with the following structure:</p>
 *
 * <h4>Basic Properties</h4>
 * <table border="1">
 * <caption>Basic Properties of Specification Items</caption>
 * <tr><th>Field</th><th>Type</th><th>Description</th></tr>
 * <tr><td>index</td><td>number</td><td>Unique sequential index of the item in the array</td></tr>
 * <tr><td>type</td><td>number</td><td>Index into the types array indicating the item type</td></tr>
 * <tr><td>title</td><td>string</td><td>Full title of the specification item</td></tr>
 * <tr><td>name</td><td>string</td><td>Short name identifier of the item</td></tr>
 * <tr><td>id</td><td>string</td><td>Full unique identifier in format "type:name[:version]"</td></tr>
 * <tr><td>tags</td><td>number[]</td><td>Array of indices into the tags array</td></tr>
 * <tr><td>version</td><td>number</td><td>Revision number of the specification item</td></tr>
 * <tr><td>status</td><td>number</td><td>Index into the status array</td></tr>
 * </table>
 *
 * <h4>Content Properties</h4>
 * <table border="1">
 * <caption>Content Properties of Specification Items</caption>
 * <tr><th>Field</th><th>Type</th><th>Description</th></tr>
 * <tr><td>content</td><td>string</td><td>Description/content of the specification item</td></tr>
 * <tr><td>comments</td><td>string</td><td>Additional comments for the item</td></tr>
 * <tr><td>path</td><td>string[]</td><td> File path components where the item is defined</td></tr>
 * <tr><td>sourceFile</td><td>string</td><td> Source file path where the item is located</td></tr>
 * <tr><td>sourceLine</td><td>number</td><td> Line number in the source file (0 if not available)</td></tr>
 * </table>
 *
 * <h4>Traceability Properties</h4>
 * <table border="1">
 * <caption>Traceability Properties of Specification Items</caption>
 * <tr><th>Field</th><th>Type</th><th>Description</th></tr>
 * <tr><td>provides</td><td>number[]</td><td>Array of type indices that this item provides coverage for</td></tr>
 * <tr><td>needs</td><td>number[]</td><td>Array of type indices that this item needs coverage from</td></tr>
 * <tr><td>covered</td><td>number[]</td><td>Array of {@link org.itsallcode.openfasttrace.report.ux.model.Coverage} enum IDs per type (0=NONE, 1=UNCOVERED, 2=COVERED, 3=MISSING)</td></tr>
 * <tr><td>uncovered</td><td>number[]</td><td>Array of type indices that are uncovered or missing for this item</td></tr>
 * <tr><td>covering</td><td>number[]</td><td>Array of indices of items that this item covers</td></tr>
 * <tr><td>coveredBy</td><td>number[]</td><td>Array of indices of items that cover this item</td></tr>
 * <tr><td>depends</td><td>number[]</td><td>Array of indices of items that this item depends on</td></tr>
 * </table>
 *
 * <h4>Link Validation Properties</h4>
 * <table border="1">
 * <caption>Link Validation Properties of Specification Items</caption>
 * <tr><th>Field</th><th>Type</th><th>Description</th></tr>
 * <tr><td>wrongLinkTypes</td><td>number[]</td><td>Array of indices into wrongLinkNames for invalid link types</td></tr>
 * <tr><td>wrongLinkTargets</td><td>string[]</td><td>Array of invalid link targets with format "target[reason]"</td></tr>
 * </table>
 *
 * <h3> Data Interpretation</h3>
 *
 * <h4>Index-Based References</h4>
 * <p>Most numeric arrays in the specitems use indices to reference:</p>
 * <ul>
 * <li><strong>Type indices:</strong> Reference the {@code project.types} array</li>
 * <li><strong>Tag indices:</strong> Reference the {@code project.tags} array</li>
 * <li><strong>Status indices:</strong> Reference the {@code project.status} array</li>
 * <li><strong>Item indices:</strong> Reference other items in the {@code specitems} array</li>
 * <li><strong>Wrong link type indices:</strong> Reference the {@code project.wrongLinkNames} array</li>
 * </ul>
 *
 * <h4> Coverage Arrays</h4>
 * <p>The {@code covered} array contains {@link org.itsallcode.openfasttrace.report.ux.model.Coverage} enum IDs per type,
 * where each position corresponds to a type in the {@code project.types} array.
 * The {@link org.itsallcode.openfasttrace.report.ux.model.Coverage} enum values are:</p>
 * <ul>
 * <li><strong>0 = NONE:</strong> No coverage relationship exists for this type</li>
 * <li><strong>1 = UNCOVERED:</strong> Coverage is required but missing for this type</li>
 * <li><strong>2 = COVERED:</strong> Coverage is complete for this type</li>
 * <li><strong>3 = MISSING:</strong> Coverage exists but has issues (e.g., missing target items)</li>
 * </ul>
 *
 * <p>For example, if {@code project.types = ["itest", "feat", "fea", "req", "arch", "utest"]} and
 * {@code covered = [0, 0, 2, 2, 1, 3]}, this means:</p>
 * <ul>
 * <li><strong>0 = NONE:</strong> No coverage relationship exists for this type</li>
 * <li><strong>1 = UNCOVERED:</strong> The specItem is not fully covered</li>
 * <li><strong>2 = COVERED:</strong> The specItem is fully covered</li>
 * <li><strong>3 = MISSING:</strong> These coverage type are needed by this specItems or specItems that cover this specItem
 * (deep coverage) but are not covered</li>
 * </ul>
 *
 * <h4>Uncovered Arrays</h4>
 * <p>The {@code uncovered} array is calculated by {@link org.itsallcode.openfasttrace.report.ux.Collector} toUncoveredIndexes()
 * and contains type indices where the coverage state is either {@code UNCOVERED} or {@code MISSING}.</p>
 *
 * <h4>Wrong Link Targets Format</h4>
 * <p>Wrong link targets are formatted as {@code "target[reason]"} where:</p>
 * <ul>
 * <li>{@code target} is the invalid link target specification</li>
 * <li>{@code reason} explains why the link is invalid (e.g., "orphaned", "unwanted coverage", "outdated coverage")</li>
 * </ul>
 * <p>Example: {@code "itest:itest_wrong_type[unwanted coverage]"}</p>
 *
 * <h3>Example JavaScript Output</h3>
 *
 * <pre>{@code
 * {
 *   index: 0,
 *   type: 2,
 *   title: 'Title fea~fea1',
 *   name: 'fea1',
 *   id: 'fea:fea1',
 *   tags: [],
 *   version: 1,
 *   content: 'Descriptive text for fea~fea1',
 *   provides: [],
 *   needs: [3],
 *   covered: [0, 0, 2, 2, 1, 3],
 *   uncovered: [4, 5],
 *   covering: [],
 *   coveredBy: [1, 2],
 *   depends: [],
 *   status: 0,
 *   path: [],
 *   sourceFile: '',
 *   sourceLine: 0,
 *   comments: '',
 *   wrongLinkTypes: [],
 *   wrongLinkTargets: []
 * }
 * }</pre>
 *
 * <h2>Implementation Notes</h2>
 *
 * <ul>
 * <li>All string values are properly escaped for JavaScript (quotes, HTML entities)</li>
 * <li>Long content strings are automatically wrapped across multiple lines with string concatenation</li>
 * <li>Empty arrays and strings indicate no data for that property</li>
 * <li>The data structure is designed for efficient lookup and filtering in web interfaces</li>
 * <li>Indices provide memory-efficient references while maintaining data integrity</li>
 * </ul>
 *
 * <h2>Usage</h2>
 *
 * <p>This data structure enables comprehensive traceability analysis and visualization in
 * OpenFastTrace-UX web applications. The generated JavaScript can be consumed by frontend
 * frameworks to create interactive dashboards, coverage reports, and traceability matrices.</p>
 *
 * @since 4.2.0
 */

package org.itsallcode.openfasttrace.report.ux;
