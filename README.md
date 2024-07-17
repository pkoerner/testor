# testor

A Clojure library designed to quickly add tests during development.

Intended to work with a typical leiningen setup, because that's what I work with.

## Rationale

My typical REPL workflow is as follows:

1. Come up with a function that sounds like a good idea.
2. In the same file, write an example call, possibly in a rich comment.
3. Fix the function I just wrote because getting things right is hard.
4. Repeat steps 2 and 3, adding more examples on the way.
5. Finally, the code stabilises.
6. Regret that I did not define anything as a test.

This library is intended to quickly add tests to the corresponding test file
while still sitting at the source file during development.

## Usage

First, import the library.

```
(use 'testor.core)
```

Add a test:

```
(fixate!! form)
```

This will add a test with the assertion `(= form (eval form))` to a Clojure file located under `test/your/awesome/name_space-test`,
if your are currently editing the `your.awesome.name-space` namespace.
Note that this is a particularly bad idea if you are working with an infinite sequence.

Sometimes, you want to add an expected value - in particular,
if you work with objects that are not serializable.
You can then use:

```
(fixate!! form expected)
```

which will add a test of the form `(= form expected)` to the corresponding test namespace.

If namespaces for tests to not exist yet, they will be created.

## License

Copyright © 2024 pkoerner

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
