# Prompt
“Fix HttpMessageNotWritableException caused by lazy collection (tags) during GET serialization.”

# Why
Jackson failed because `Customer.tags` was lazy-loaded and the session closed before serialization.

# Outcome
Mapper now uses `List.copyOf(tags)` to detach collections and `@ElementCollection(fetch = EAGER)` for safety.  
No more lazy-init errors; all tests pass.

# Model
ChatGPT (GPT-5 Thinking)
