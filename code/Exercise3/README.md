# Code of your exercise

Put here all the code created for this exercise

```xml
<?xml version="1.0"?>

<ruleset name="Custom Rules"
         xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">

  <description>
    My custom rules
  </description>

  <rule name="PreventComplexity"
        language="java"
        message="Avoid having three or more nested if statements to reduce code complexity."
        class="net.sourceforge.pmd.lang.rule.xpath.XPathRule">
    <priority>3</priority>
    <properties>
      <property name="xpath">
        <value>
          <![CDATA[//IfStatement[descendant::IfStatement[descendant::IfStatement]]]]>
        </value>
      </property>
    </properties>
  </rule>
</ruleset>
```