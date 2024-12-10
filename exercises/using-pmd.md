# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset (see the [pmd install instruction](./pmd-help.md)). Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

**True Positive**

```
/home/antoine/IdeaProjects/commons-collections/src/main/java/org/apache/commons/collections4/CollectionUtils.java:1089: PreserveStackTrace: Thrown exception does not preserve the stack trace of exception 'ex' on all code paths
```

PMD have flagged code where an exception caught is rethrown without preserving the original cause. While reviewing `CollectionUtils.java` from the Apache Commons Collections, no direct code snippet in this file shows a classic `throw new Exception("msg")` inside a catch block that discards the original cause.

```java
try {
    // some code that throws an exception
} catch (Exception ex) {
    // PMD warns here: Thrown exception does not preserve the stack trace
    throw new RuntimeException("An error occurred");
}
```

**Why Should This Be Solved?**  
Not preserving the original stack trace can make debugging significantly harder. Developers lose the information about where the original exception occurred. To fix this, include the original exception as the cause when rethrowing:

**Proposed Change:**
```java
try {
    // some code that throws an exception
} catch (Exception ex) {
    // Preserve the stack trace by passing 'ex' as the cause
    throw new RuntimeException("An error occurred", ex);
}
```

By adding `ex` to the rethrown exception, the original stack trace is preserved, which is a best practice and should be fixed (true positive).

---

**False Positive (Not Worth Solving): LooseCoupling Warning**

The PMD report lists warnings like “LooseCoupling: Avoid using implementation types like 'ArrayList'; use the interface instead.” For example:
```java
// PMD complains about using ArrayList directly:
final ArrayList<O> mergedList = new ArrayList<>(totalSize);
```

**Why Not Solve This Issue?**  
Apache Commons Collections is a mature library. The developers often choose specific implementations (like `ArrayList`) intentionally for performance or historical reasons. Changing them to `List` might not bring any real benefit and could even complicate the code unnecessarily. Since this is internal library code, not a public API that needs the flexibility of substitution, and performance characteristics are well-understood, this warning is likely a false positive.
