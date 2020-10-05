# Lollipop

A rule based HTTP proxy written in clojure for redirecting your traffic.

![Lollipop Banner](doc/assets/images/lollipop.png)

A tribute to all those brave [lollipop ladies](https://www.google.com/search?q=lollipop+lady&oq=lollip&aqs=chrome.0.69i59l2j46j0j46j69i57j0.1339j0j7&sourceid=chrome&ie=UTF-8)
out there who direct traffic for kids.

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

