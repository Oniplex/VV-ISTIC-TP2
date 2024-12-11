# Cyclomatic Complexity with JavaParser

With the help of JavaParser implement a program that computes the Cyclomatic Complexity (CC) of all methods in a given Java project. The program should take as input the path to the source code of the project. It should produce a report in the format of your choice (TXT, CSV, Markdown, HTML, etc.) containing a table showing for each method: the package and name of the declaring class, the name of the method, the types of the parameters and the value of CC.
Your application should also produce a histogram showing the distribution of CC values in the project. Compare the histogram of two or more projects.


Include in this repository the code of your application. Remove all unnecessary files like compiled binaries. Do include the reports and plots you obtained from different projects. See the [instructions](../sujet.md) for suggestions on the projects to use.

You may use [javaparser-starter](../code/javaparser-starter) as a starting point.


## Answer

**Principe du code :**  
Le programme utilise JavaParser pour analyser statiquement le code source Java d'un projet. Il parcourt chaque classe et interface, et pour chaque méthode trouvée, calcule la complexité cyclomatique (CC).

Le calcul se fait selon la formule de base :  
**CC = 1 + (nombre de structures de décision et de branchement)**

Fonctionnement :
- Parcourt le code source fourni.
- Pour chaque méthode, compte le nombre de `if`, `for`, `while`, `switch`, `catch`, etc.
- Additionne ces occurrences à une base de 1 pour obtenir la CC.
- Stocke les résultats (package, classe, méthode, paramètres, CC) dans une liste.
- Génère un fichier CSV listant la CC de chaque méthode.
- Produit également un histogramme montrant la distribution des valeurs de CC dans le projet.

Le fichier csv généré se trouve dans le repertoire [Exercise5](../code/Exercise5/cc_report.csv).

**Histogramme de la complexité cyclomatique :**  
L'histogramme fourni montre la répartition des valeurs de CC sur l'ensemble du projet :

- **CC=1:** Très nombreuses méthodes (5883), indiquant que la majorité du code est constitué de méthodes simples, sans conditions ni boucles complexes.
- **CC=2:** 988 méthodes, un peu plus complexes, contenant généralement une structure conditionnelle simple.
- **CC=3 à CC=5:** Plusieurs centaines de méthodes, indiquant des blocs logiques plus complexes.
- Au-delà de CC=5, les méthodes sont moins nombreuses, mais plus complexes. On observe par exemple des méthodes atteignant CC=20 ou plus, signe d'une logique plus élaborée et potentiellement plus difficile à maintenir.

**Interprétation des résultats :**
- Une majorité de méthodes avec une CC faible (1 ou 2) est généralement un bon signe : cela signifie que le code est, dans l'ensemble, bien structuré et facile à comprendre ou à tester.
- Les méthodes avec une CC élevée (supérieure à 10) méritent une attention particulière, éventuellement une refactorisation pour les simplifier, améliorer leur lisibilité et leur maintenabilité.