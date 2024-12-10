# Extending PMD

Use XPath to define a new rule for PMD to prevent complex code. The rule should detect the use of three or more nested `if` statements in Java programs so it can detect patterns like the following:

```Java
if (...) {
    ...
    if (...) {
        ...
        if (...) {
            ....
        }
    }

}
```
Notice that the nested `if`s may not be direct children of the outer `if`s. They may be written, for example, inside a `for` loop or any other statement.
Write below the XML definition of your rule.

You can find more information on extending PMD in the following link: https://pmd.github.io/latest/pmd_userdocs_extending_writing_rules_intro.html, as well as help for using `pmd-designer` [here](https://github.com/selabs-ur1/VV-ISTIC-TP2/blob/master/exercises/designer-help.md).

Use your rule with different projects and describe you findings below. See the [instructions](../sujet.md) for suggestions on the projects to use.

## Answer

The rule is designed to identify and flag instances in Java code where there are **three or more nested `if` statements**. Such deep nesting can increase code complexity, making it harder to read, maintain, and debug.

**XPath Expression Used**:
```xpath
<rule name="PreventComplexity"
    language="java"
    message="Avoid having three or more nested if statements to reduce code complexity."
    class="net.sourceforge.pmd.lang.rule.xpath.XPathRule">

    <priority>3</priority>

    <properties>
        <property name="xpath">
            <value>
                <![CDATA[//IfStatement[ancestor::IfStatement[ancestor::IfStatement]]]]>
            </value>
        </property>
    </properties>
</rule>
```
**Explanation**:
- This XPath targets any `IfStatement` node that has an ancestor `IfStatement`, which in turn has another ancestor `IfStatement`. In simpler terms, it looks for `if` statements nested within two other `if` statements, effectively identifying the third level of nesting.

---

### **2. Findings Across Apache Commons Projects**

The custom PMD rule was executed against the following Apache Commons projects, resulting in the detection of violations as detailed below:

| **Project**                     | **Repository Link**                                             | **Violations Detected** |
|---------------------------------|-----------------------------------------------------------------|-------------------------|
| **Apache Commons Collections**  | [GitHub Link](https://github.com/apache/commons-collections)    | 93                      |
| **Apache Commons CLI**          | [GitHub Link](https://github.com/apache/commons-cli)            | 35                      |
| **Apache Commons Math**         | [GitHub Link](https://github.com/apache/commons-math)           | 339                     |
| **Apache Commons Lang**         | [GitHub Link](https://github.com/apache/commons-lang)           | 181                     |
