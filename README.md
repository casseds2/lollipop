# Proxy Protect

A rule-based clojure API protection suite for securing resources.

## To Run:
Default rules are provided in `resources/rules.json`. These will be read in on
startup. To run with defaults:

```bash
$ lein run
```

Otherwise, rules can be passed into the program with:
```bash
$ lein run --rules <path_to_rules>
```

## Configuring Rules
Guide for configuring rules [here](doc/Rules.md).

