# testor

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.pkoerner/testor.svg)](https://clojars.org/org.clojars.pkoerner/testor)

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

This will add a test with the assertion `(= form (eval form))` to a Clojure file located under `test/your/awesome/name_space-test.clj`,
if your are currently editing the `your.awesome.name-space` namespace.
Note that this is a particularly bad idea if you are working with an infinite sequence.

This (and only this) arity of `fixate!!` will try to simplify some generated testing predicates.
If your form is `(= ...)`, it will not try to generate `(= (= ...) true)` or `(= (= ...) false)`,
but simply put `(is form)` or `(is (not= ...))`.
Similarly, forms with comparison operators like `<` or `<=` are not compared with `true` or `false`.
In general, this will also occur if the function name of the outermost form ends in a `?`.


---

Sometimes, you want to add an expected value - in particular,
if you work with objects that are not serializable.
You can then use:

```
(fixate!! form expected)
```

which will add a test of the form `(= form expected)` to the corresponding test namespace.

---

By default, the testname will be a combination of the function name of the form, the current timestamp and a gensym'd number,
something like `foo-test-1740132174551-2240`.
This is ugly.
You can provide a better name by calling:

```
(fixate!! awesome-test-name form expected)
```

Here, `awesome-test-name` should be a symbol.

---

Finally, complex tests might not be clear to read later on.
The final arity lets you also provide a description suitable for clojure.test:

```
(fixate!! awesome-test-name "I guess this should work" form expected)
```

By default, the description claims that you thought this was correct behaviour when you developed it.

---

Mostly, I care more that the test exists and less about its name and description.
Often, I find myself working and debugging in a rich comment block.
Then, if I end up with a number of calls, it is unwieldy to call `fixate!!` on each individual form.
Thus, there also is `fixate-all!!`, which does not allow clever names and descriptions,
but adds each call as a test.
It is a small macro that expands to a number of `fixate!!` calls.

```
(fixate-all!! (= 1 1)
              (= 1 2)
              (< 1 2))
```

---

If namespaces for tests do not exist yet, they will be created.

## License

Copyright © 2025 pkoerner

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
