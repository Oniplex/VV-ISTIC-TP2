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

Bien sûr, voici la traduction du compte rendu en français :

---

La règle est conçue pour identifier et signaler les instances dans le code Java où **trois instructions `if` imbriquées ou plus** sont présentes. Une telle imbrication peut augmenter la complexité du code, le rendant plus difficile à lire, à maintenir et à déboguer.

**Expression XPath Utilisée** :
```xpath
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
```
**Explication** :
- Cette expression XPath cible tout nœud `IfStatement` qui a un descendant `IfStatement`, lequel a à son tour un autre descendant `IfStatement`. En termes plus simples, elle recherche des instructions `if` imbriquées au sein de deux autres instructions `if`, identifiant ainsi le troisième niveau d'imbrication.

---

### **2. Résultats dans les Projets Apache Commons**

La règle PMD personnalisée a été exécutée sur les projets Apache Commons suivants :

| **Projet**                      | **Lien du Dépôt**                                              | **Violations Détectées** |
|---------------------------------|----------------------------------------------------------------|--------------------------|
| **Apache Commons Collections**  | [Lien GitHub](https://github.com/apache/commons-collections)    | 49                       |
| **Apache Commons CLI**          | [Lien GitHub](https://github.com/apache/commons-cli)            | 25                       |
| **Apache Commons Math**         | [Lien GitHub](https://github.com/apache/commons-math)           | 220                      |
| **Apache Commons Lang**         | [Lien GitHub](https://github.com/apache/commons-lang)           | 137                      |

---