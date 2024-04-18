
# About us

## Origins

```
ReqMgr   ReqMgrNG     ReqM/ReqM2   Allosaurus     OFT   OFT 1.0
  |         | T-Reqs     |             |           |       |
--+---------+--+---------+-------------+-----------+-------+-------+-> t
 2003      2004                      ~2012       2015    2018
                      
```

OFT's roots go back to the year 2003 when it's first predecessor with the unimaginative name 'ReqMgr' (Requirement Manager) first saw the light of day at 3SOfT GmbH in Erlangen Tennenlohe (Germany). Being a software supplier for the automotive industry, 3SOFT had a need for requirement tracing to fulfill the strict rules for safety-critical software.

3SOFT was later acquired by the Finish Elektrobit group. [Bernd "Poldi" Haberstumpf](https://github.com/poldi2015) rewrote the complete requirement tracing code in the context of the [Autosar](https://www.autosar.org/) introduction to allow multi-level tracing in 2004. This new version was dubbed 'ReqMgrNG'. The introduction of the ASIL-D Autosar OS with microkernel eventually led to a stripped down version of 'ReqM2' which was missing a lot of functionality of the 'ReqMgrNG'.

In parallel another group of developers at Elektrobit worked on T-Reqs, a Java tool which did not see a lot of adoption.

In even later projects performance became an issue, so ReqM2 and T-Reqs were both superseded by the much faster Allosaurus (yes, at this point in time everyone went full pun-mode).

While a lot faster, Allosaurus was somewhat clunky, and that was what started the development of OFT as a free software project. [Christoph Pirkl](https://github.com/kaklakariada/) and [Sebastian Bär](https://github.com/redcatbear) wrote OFT in their spare time while still working at Elekrobit with generous requirement engineering wisdom kindly supplied by Poldi.

[Thomas Fleischmann](https://github.com/quarterbit) was the main person to make OFT popular in the automotive industry, by tirelessly explaining its benefits to engineers and managers alike who were plagued by existing systems that were slow, proprietary and had terrible user experience.

In automotive projects that span over a decade, it is vital to manage requirements in the same manner as the codebase. This approach enables diffing of requirements, implementing changes through PRs, and the easier merging of requirements between Start-of-Production (SOP) branches.

## Free and Open Source

One thing was clear for the four original founders of OFT. This time we wanted a broader community around our requirement tracing suite. And, since we were convinced that there is a need for requirement engineering in general and tracing in particular, we decided to start OpenFastTrace as an open source project on GitHub with the [first commit](https://github.com/itsallcode/openfasttrace/commit/f4e9167cedad499c168ab4bb9a4e20d762f33f8b) in December 2015. The made the first [release 0.1.0](https://github.com/itsallcode/openfasttrace/releases/tag/0.1.0) was in August 2017, and we reached the Minimum Viable Product (MVP) June 2018 with version [1.0.0](https://github.com/itsallcode/openfasttrace/releases/tag/1.0.0). OFT From then on OFT found its way into other industries outside  the automotive world and also into the build toolchains of other open source projects. And that is the best thing we could ask for.
