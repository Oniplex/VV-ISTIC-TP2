# No getter!

With the help of JavaParser implement a program that obtains the private fields of public classes that have no public getter in a Java project. 

A field has a public getter if, in the same class, there is a public method that simply returns the value of the field and whose name is `get<name-of-the-field>`.

For example, in the following class:

```Java

class Person {
    private int age;
    private String name;
    
    public String getName() { return name; }

    public boolean isAdult() {
        return age > 17;
    }
}
```

`name` has a public getter, while `age` doesn't.

The program should take as input the path to the source code of the project. It should produce a report in the format of your choice (TXT, CSV, Markdown, HTML, etc.) that lists for each detected field: its name, the name of the declaring class and the package of the declaring class.

Include in this repository the code of your application. Remove all unnecessary files like compiled binaries. See the [instructions](../sujet.md) for suggestions on the projects to use.

*Disclaimer* In a real project not all fields need to be accessed with a public getter.


## Answer

## Principe du Code

Le programme se compose principalement de deux classes :

1. **`Main.java`** : Point d'entrée de l'application. Il configure le processus d'analyse en parcourant récursivement les fichiers sources Java.

2. **`NoGetterDetector.java`** : Classe qui étend `VoidVisitorAdapter<Void>` de JavaParser. Elle contient la logique pour :
    - Identifier les classes publiques dans les fichiers sources.
    - Extraire les champs privés de ces classes.
    - Vérifier l'existence de méthodes publiques correspondant aux getters de ces champs, suivant la convention de nommage `get<NomDuChamp>`.
    - Enregistrer les informations pertinentes (nom du champ, classe, package, présence d'un getter) dans une liste structurée.
    - Générer un rapport au format CSV à partir des données collectées.

### Fonctionnement Détailé

1. **Parcours des Fichiers Sources** : `Main.java` utilise `SourceRoot` de JavaParser pour parcourir tous les fichiers `.java` dans le répertoire spécifié.

2. **Visite des Classes Publiques** : Pour chaque fichier source, `NoGetterDetector` visite les déclarations de types (classes, interfaces, enums) et filtre celles qui sont publiques.

3. **Extraction des Champs Privés** : Au sein de chaque classe publique, le programme collecte tous les champs privés déclarés.

4. **Détection des Getters Publics** : Pour chaque champ privé, le programme recherche une méthode publique sans paramètres dont le nom suit la convention `get<NomDuChamp>` et qui retourne directement la valeur du champ.

5. **Compilation des Résultats** : Les informations recueillies sont structurées et exportées dans un fichier CSV, listant chaque champ privé avec son statut de getter.

## Résultats

Après exécution du programme sur le code source analysé, un fichier CSV a été généré. Il se trouve dans le repertoire [Exercise4](../code/Exercise4/no_getter_report.csv)

### Analyse des Résultats

La majorité des champs listés n'ont pas de getters publics (`HasGetter = No`). Cela inclut des champs tels que `serialVersionUID`, souvent utilisés pour la sérialisation, qui sont généralement déclarés `private static final` et ne nécessitent pas de getters.
