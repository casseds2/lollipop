# Rules Configuration

## Rule Fields
### Type
The type field is used to indicate what type of rule the configuration
represents. Types have the following possible values:
- `exact`
- `regex`
- `redirect`

#### Exact
An exact type rule will only match if he source URI is exactly the same as
the `source field.

#### Regex
A regex type rule will convert the source field into a regular expression and
attempt to match this against the URI.

#### Redirect
A redirect type rule will redirect the user to a given URL with a `302` response.

### Allowed Methods
The allowed methods field us used to configure what HTTP methods are allowed in
a rule. All possible values are:
- `get`
- `put`
- `post`
- `delete`
- `patch`
- `any`

Even if the source matched the URI, the request will be blocked if the
HTTP method isn't contained in the allowed methods array. The `any` method
is shorthand for allowing all HTTP methods to access the destination.

### Source
This is the source URI of the request that will be used to identify if the rule
should be applied. It can be a plain string or a regular expression.

### Destination
This is the destination that the request will be proxied to if the rule matched.