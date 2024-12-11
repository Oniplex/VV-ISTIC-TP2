package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cette classe visite l'AST d'un projet Java à l'aide de JavaParser et calcule
 * la complexité cyclomatique de chaque méthode rencontrée.
 * Les résultats sont stockés dans une liste interne et peuvent être exportés sous forme
 * de rapport CSV et d'histogramme.
 */
public class ComplexityCalculator extends VoidVisitorWithDefaults<Void> {

    private final List<MethodInfo> methodInfos = new ArrayList<>();
    private String currentPackageName = "";
    private String currentClassName = "";

    /**
     * Contient les informations essentielles d'une méthode analysée.
     */
    private static class MethodInfo {
        final String packageName;
        final String className;
        final String methodName;
        final String parameterTypes;
        final int cyclomaticComplexity;

        MethodInfo(String packageName, String className, String methodName, String parameterTypes, int cyclomaticComplexity) {
            this.packageName = packageName;
            this.className = className;
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
            this.cyclomaticComplexity = cyclomaticComplexity;
        }
    }

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        currentPackageName = unit.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");
        unit.getTypes().forEach(type -> type.accept(this, arg));
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    /**
     * Visite une déclaration de type (classe, interface, enum), met à jour
     * le nom de la classe courante et visite les méthodes et types imbriqués.
     */
    private void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        currentClassName = declaration.getNameAsString();

        declaration.getMethods().forEach(m -> m.accept(this, arg));

        declaration.getMembers().stream()
                .filter(member -> member instanceof TypeDeclaration)
                .forEach(member -> member.accept(this, arg));
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        int cc = calculateCyclomaticComplexity(declaration);
        String params = declaration.getParameters().stream()
                .map(p -> p.getType().asString())
                .collect(Collectors.joining(", "));
        methodInfos.add(new MethodInfo(currentPackageName, currentClassName, declaration.getNameAsString(), params, cc));
    }

    /**
     * Calcule la complexité cyclomatique (CC) d'une méthode.
     * CC = 1 + (nombre de points de décision)
     * Un point de décision est par exemple: if, for, foreach, while, do-while, switch (et ses cas), catch.
     */
    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        int complexity = 1;

        complexity += countOccurrences(method, IfStmt.class);
        complexity += countOccurrences(method, ForStmt.class);
        complexity += countOccurrences(method, ForEachStmt.class);
        complexity += countOccurrences(method, WhileStmt.class);
        complexity += countOccurrences(method, DoStmt.class);
        complexity += method.findAll(SwitchStmt.class).stream()
                .mapToInt(s -> s.getEntries().size())
                .sum();
        complexity += countOccurrences(method, CatchClause.class);

        return complexity;
    }

    /**
     * Compte le nombre d'occurrences d'un certain type de noeud AST dans la méthode donnée.
     */
    private <T extends com.github.javaparser.ast.Node> int countOccurrences(MethodDeclaration method, Class<T> nodeClass) {
        return method.findAll(nodeClass).size();
    }

    /**
     * Retourne la distribution de la complexité cyclomatique sous forme d'une liste de paires
     * (valeur de CC, nombre de méthodes).
     */
    public List<Map.Entry<Integer, Long>> getComplexityDistribution() {
        return methodInfos.stream()
                .collect(Collectors.groupingBy(m -> m.cyclomaticComplexity, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    /**
     * Écrit un rapport CSV contenant : package, classe, méthode, paramètres et CC.
     */
    public void writeReport(String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("Package,Class,Method,Parameters,CC\n");
            for (MethodInfo mi : methodInfos) {
                String params = mi.parameterTypes.contains(",") ? "\"" + mi.parameterTypes + "\"" : mi.parameterTypes;
                writer.write(String.format("%s,%s,%s,%s,%d\n",
                        mi.packageName, mi.className, mi.methodName, params, mi.cyclomaticComplexity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche un histogramme de la distribution de CC sur la console.
     */
    public void printHistogram() {
        System.out.println("\nHistogramme de la complexité cyclomatique:");
        for (Map.Entry<Integer, Long> entry : getComplexityDistribution()) {
            int cc = entry.getKey();
            long count = entry.getValue();
            String bar = String.join("", Collections.nCopies((int) count, "#"));
            System.out.printf("CC=%d: %s (%d méthodes)%n", cc, bar, count);
        }
    }

    public List<Map.Entry<Integer, Long>> getDistribution() {
        return getComplexityDistribution();
    }
}