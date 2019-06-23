# Overview
_Kos_ is small layer over Vert.x to leverage web developers productivity by providing them a
zero-overhead and minimalistic API. It was carefully designed to not hide from developers
how the software works underneath. Kos also embrace Vert.x philosophy and doesn't enforce
developers to follow opinionated conventions - as all of them are optional - but provide
them an easy and transparent mechanism to avoid repetitive tasks.

## Features
* a reflection-free layer over Vert.x web API
* a simplified way to spin up the web server
* automatically deploy Vert.x Verticles
* automatically load vertx-config configuration
* introduce customization entries for sensible mechanisms (like serializers)

## Motivation
Vert.x shines in the JVM ecosystem as blistering fast toolkit to build scalable and reliable web applications. Aside of
its well crafted non-blocking event-loop and a versatile multi-propose web layer, it comes with a set of reactive tools
and non-blocking drivers that leverages the application performance to another level. It comes though with a trade-off
as some may see it as niche solution due to its low-level API.

According to a [Stack Overflow survey](https://insights.stackoverflow.com/survey/2018/#developer-roles)
almost 60% of the developers identify themselves as backend-developers. It's known that, aside from a few
edge-cases, backend-developers are mostly focused on delivery new features to increase the aggregated value
of a program, letting the of perform low-level tuning to _software architects and scientists_, squeezing bits
and bytes whenever possible to reduce cost or improve the user usage experience.

Steady, repeatable and predictable (a.k.a. _boring_) routine directly conflicts with the developer's desire of
learning something new - or arguably more fun. You can see traits of this behaviour everywhere, specially regarding
[sluggish performance](https://medium.com/@hussachai/scalas-immutable-collections-can-be-slow-as-a-snail-da6fc24bc688)
of frameworks,
[lack of innovative approaches](https://rclayton.silvrback.com/i-m-quitting-java-and-moving-to-go) and
old-minded communities.

Keeping a relevant role in the developers routine by providing an updated and open-minded development alternative,
whilst ensuring developers productivity, is definitely a hard task. By keeping Vert.x as foundation of his existence,
Kos aims to fill this gap between by encouraging the development of high-performance applications with less hassle.

## Philosophy
These are the principles that guides Kos development:

- _Keep things simple but flexible_. Conventions are Pareto-friendly, therefore, doesn't solve all the problems. Kos will simplify
things whenever possible, but developers are always free to switch back _vanilla Vert.x_.
- _Keep it fast_. Simplifying repetitive routines should never hurt the software performance.
- Vert.x as _first-class citizen_. While Kos wraps some of Vert.x API, it encourages developers to make use of it
whenever needed. Kos wrappers always returns the Vert.x equivalent for further usage.
- _Never block the event loop_. You may have [heard](https://servicesblog.redhat.com/2019/07/01/troubleshooting-the-performance-of-vert-x-applications-part-i-the-event-loop-model/)
about that before, but it worth to mention you should never block the event-loop.

## History
This project was started between late 2018 and early 2019 on an attempt to provide Sizebay
a simple API to replace their previous undertow-based microservices framework. The idea came
up when we had the necessity to include more developers on the team and we wanted to avoid
them to talk directly to Undertow and XNIO as they was designed as low-level APIs.

The project name kos (pronounces like coosh) came from Norwegian and is a word used to describe
all the things that make you feel cozy and warm inside.

## License
Kos is release under Apache License 2 terms.
