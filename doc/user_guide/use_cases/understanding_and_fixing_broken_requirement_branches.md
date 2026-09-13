### Understanding and Fixing Broken Requirement Branches

Requirements — or specification items as we call them more broadly — in OFT are internally organized in a graph. If you haven't heard of that term, don't worry. In most cases it is close enough to think of the relationships between the specification items like a forest where the highest level of the specification are tree trunks from which details branch out into big branches, twigs and eventually leaves.

Requirement engineering calls this the "traceability matrix". That term is a bit clunky, but we thought, you should have heard of it at least once.

#### Everything That can go Wrong…

What we want to achieve in any role that has to do with requirement engineering is healthy trees with their leaves attached all the way to the trunks. We don't want twigs without leaves, and we definitely don't want leaves lying on the ground.

Unfortunately, we are only human, and humans make mistakes. Here is a non-exhaustive list of typical mistakes that happen when maintaining a traceability matrix:

| Mistake                         | How it manifests in OFT                            |
|---------------------------------|----------------------------------------------------|
| Unimplemented feature           | missing leaves in the implementation               |
| Missing tests                   | missing leaves in the test                         |
| Typos in requirement IDs        | causing branches to be cut somewhere in the middle |
| Wrong artifact type in coverage | both missing and unexpected coverage               |

#### Reading and Understanding the Link Error Types

Depending, from where you look at a specification item, it can have links that point towards it (incoming) or away from it (outgoing). And those links can be broken. This is a typical sign that the requirement matrix contains wrong coverage, is incomplete or has excess parts.

#### Outgoing Link Statuses

| Status           | Explanation                                                            | Ok |
|------------------|------------------------------------------------------------------------|----|
| Covers           | This item covers another item                                          | ✔️ |
| Predated         | This item covers a newer revision of another item                      | ❌  |
| Outdated         | This item covers an older revision of another item                     | ❌  |
| Ambiguous        | Two items with the same id are covered by another item                 | ❌  |
| Unwanted         | This item covers another item that does not require this coverage      | ❌  |
| Orphaned         | This item covers a non-existing item                                   | ❌  |

"Covers" means everything is fine.

When the outgoing link from this item is "predated", that means it points to a newer version of the covered item than it should. This is usually a typo that you need to simply fix. On rarer occasion it can hint at a merge error or a problem when multiple teams contribute to the same specification. Check the document history if unsure.

"Outdated" coverage typically happens when an existing specification item was updated, but the coverage wasn't. This is one of the most useful safeguards in OFT.

Copy & paste often leads to "ambiguous" coverage, where two items are defined with the same ID. Treat this like you would fix a typo, but be careful, if the ID wasn't updated, then there are likely other copy & paste errors hiding in the vicinity. 

Coverage is "unwanted" when the specification item that it points to didn't ask for it. Check for typos in both IDs. The most common mistake here is that either the required artifact types or the coveraging artifact types are wrong.

"Orphaned" finally means that this item claims to cover a requirement that does not exist. Or ceased to exist. In this case first check for typos, and if it is not a typo, check the history of the documents to see if there is maybe coverage left for something that has been obsoleted higher up in a specification.


#### Incoming Link Statuses

| Status           | Explanation                                                              | Ok |
|------------------|--------------------------------------------------------------------------|----|
| Covered Shallow  | This item is directly covered by another item                            | ✔️ |
| Covered Unwanted | This item is covered by another item though it does not require coverage | ❌  |
| Covered Predated | This item is covered by another item that specifies a newer revision     | ❌  |
| Covered Outdated | This item is covered by another item that specifies an older revision    | ❌  |

If you see "covered shallow" on an incoming link, this means that there is at least one specification item providing the required coverage.

"Covered unwanted" means that another item covers the one you are looking at, but it shouldn't, because that coverage was not required. In most cases you are looking at a copy & paste error. Sometimes it is simply a typo in the artifact types. In rarer circumstances this happens the person who wrote the higher level item disagreed with the one who did the coverage. The last variant can be solved by talking to each other.

"Covered predated" means some other specification item claims to cover a newer version of this item than is currently present in the spec. "Covered outdated" is the opposite situation. The problem resolution is the same as in the [section above](#outgoing-link-statuses) where the predated and outdated incoming links were discussed.

#### Bidirectional Link Statuses

| Status           | Explanation                                                              | Ok |
|------------------|--------------------------------------------------------------------------|----|
| Duplicate        | Two items have the same ID                                               | ❌  |

Duplicate links are special. They don't have a clear direction, since OFT cannot tell which specification item is the original and which one is the duplicate. In either case, you are most likely looking at a copy & past error again. Handle this with care and check if you maybe forgot to adapt other aspects of the copy too, not only the ID.

---

← [HTML Tracing Reports](html_tracing_reports.md) | ↑ [Use Cases](use_cases.md) | [Reference](../reference/reference.md) →
