# Kos
Zero-overhead and minimalist web framework for [Vert.x](https://vertx.io/).

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.skullabs.kos/kos-core/badge.svg?style=flat-square&logo=appveyor)](https://maven-badges.herokuapp.com/maven-central/io.skullabs.kos/kos-core)

## Goals
This small project aims to leverage Vert.x web developers productivity by providing them
a zero-overhead and minimalistic API layer. It was carefully designed to not hide
from developers how the software works underneath. Kos also embrace Vert.x philosophy
and doesn't enforce developers to follow opinionated conventions - as all of them are
optional - but provide them an easy and transparent mechanism to avoid repetitive tasks.

Kos is for you if you need:
- a reflection-free layer over Vert.x web API
- a simplified way to spin up the web server
- automatically deploy Vert.x Verticles
- automatically load vertx-config configuration

## History
This project was started between late 2018 and early 2019 on an attempt to provide
[Sizebay](https://sizebay.com) a simple API to replace their previous solution
(based on [undertow](https://undertow.io)) for microservices. The idea came up
when we had the necessity to include more developers on the team and we wanted
to avoid them to talk directly to Undertow and XNIO as they was designed as
low-level APIs.

The project name **kos** (pronounces like _coosh_) came from Norwegian and is
a word used to describe all the things that make you feel cozy and warm inside.

## Documentation and support
- [Official documentation](https://skullabs.github.io/kos/)
- [GitHub issues](https://github.com/skullabs/kos/issues)

## Reporting Bugs/Feature Requests
We welcome you to use the GitHub issue tracker to report bugs or suggest features.

When filing an issue, please check existing open, or recently closed, issues to make sure somebody else hasn't already
reported the issue. Please try to include as much information as you can. Details like these are incredibly useful:

* A reproducible test case or series of steps
* The version of our code being used
* Any modifications you've made relevant to the bug
* Anything unusual about your environment or deployment


## Contributing via Pull Requests
Contributions via pull requests are much appreciated. Before sending us a pull request, please ensure that:

1. You are working against the latest source on the *master* branch.
2. You check existing open, and recently merged, pull requests to make sure someone else hasn't addressed the problem already.
3. You open an issue to discuss any significant work - we would hate for your time to be wasted.

To send us a pull request, please:

1. Fork the repository.
2. Modify the source; please focus on the specific change you are contributing. If you also reformat all the code, it will be hard for us to focus on your change.
3. Ensure local tests pass.
4. Commit to your fork using clear commit messages.
5. Send us a pull request, answering any default questions in the pull request interface.
6. Pay attention to any automated CI failures reported in the pull request, and stay involved in the conversation.

GitHub provides additional document on [forking a repository](https://help.github.com/articles/fork-a-repo/) and
[creating a pull request](https://help.github.com/articles/creating-a-pull-request/).


## Finding contributions to work on
Looking at the existing issues is a great way to find something to contribute on. As our projects, by default, use the default GitHub issue labels ((enhancement/bug/duplicate/help wanted/invalid/question/wontfix), looking at any 'help wanted' issues is a great place to start.

## License
This is release under the Apache License 2 terms.
