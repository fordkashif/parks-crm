# Prompt
“Make entities auditable so `createdBy`, `updatedBy`, etc. are never null in tests; update tests to check those fields.”

# Why
The initial tests failed because auditing fields were null.  
Needed to wire `@EnableJpaAuditing` and an `AuditorAware<String>` bean defaulting to `system`.

# Outcome
Auditing now populates correctly; tests assert created/updated fields exist and are non-blank.

# Model
Claude 3.5 Sonnet
