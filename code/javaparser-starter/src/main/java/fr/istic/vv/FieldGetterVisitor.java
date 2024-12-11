package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FieldGetterVisitor extends VoidVisitorAdapter<Void> {

    private final List<FieldRecord> records = new ArrayList<>();

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        String packageName = unit.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");

        for (TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(new ClassVisitor(packageName), null);
        }
    }

    /**
     * Écrit les enregistrements collectés dans un fichier CSV.
     *
     * @param outputPath Le chemin vers le fichier CSV de sortie.
     */
    public void writeReport(String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {

            writer.write("Field,Class,Package,HasGetter\n");
            for (FieldRecord record : records) {
                writer.write(String.format("%s,%s,%s,%s\n",
                        record.fieldName,
                        record.className,
                        record.packageName,
                        record.hasGetter ? "Yes" : "No"));
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du rapport : " + e.getMessage());
        }
    }

    /**
     * Représente un enregistrement d'un champ avec son statut de getter.
     */
    private static class FieldRecord {
        String fieldName;
        String className;
        String packageName;
        boolean hasGetter;

        FieldRecord(String fieldName, String className, String packageName, boolean hasGetter) {
            this.fieldName = fieldName;
            this.className = className;
            this.packageName = packageName;
            this.hasGetter = hasGetter;
        }
    }

    /**
     * Visiteur pour les déclarations de classes.
     */
    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private final String packageName;

        private String className;
        private Set<String> privateFields = new HashSet<>();
        private Set<String> getterFields = new HashSet<>();

        ClassVisitor(String packageName) {
            this.packageName = packageName;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
            if (!declaration.isPublic()) {
                return;
            }

            className = declaration.getNameAsString();

            for (FieldDeclaration field : declaration.getFields()) {
                if (field.isPrivate()) {
                    for (VariableDeclarator var : field.getVariables()) {
                        privateFields.add(var.getNameAsString());
                    }
                }
            }

            for (MethodDeclaration method : declaration.getMethods()) {
                if (isPublicGetter(method)) {
                    String fieldName = extractFieldNameFromGetter(method.getNameAsString());
                    if (fieldName != null && privateFields.contains(fieldName)) {
                        if (returnsField(method, fieldName)) {
                            getterFields.add(fieldName);
                        }
                    }
                }
            }

            for (String field : privateFields) {
                boolean hasGetter = getterFields.contains(field);
                records.add(new FieldRecord(field, className, packageName, hasGetter));
            }

            for (BodyDeclaration<?> member : declaration.getMembers()) {
                if (member instanceof ClassOrInterfaceDeclaration) {
                    member.accept(this, arg);
                }
            }
        }

        /**
         * Vérifie si une méthode est un getter public suivant la convention de nommage get<FieldName>.
         *
         * @param method La méthode à vérifier.
         * @return Vrai si c'est un getter public, sinon Faux.
         */
        private boolean isPublicGetter(MethodDeclaration method) {
            if (!method.isPublic()) {
                return false;
            }
            String name = method.getNameAsString();
            if (!name.startsWith("get") || name.length() <= 3) {
                return false;
            }
            if (!method.getParameters().isEmpty()) {
                return false;
            }
            return true;
        }

        /**
         * Extrait le nom du champ à partir du nom d'une méthode getter.
         * Par exemple, getName -> name
         *
         * @param getterName Le nom de la méthode getter.
         * @return Le nom du champ correspondant, ou null si non déterminable.
         */
        private String extractFieldNameFromGetter(String getterName) {
            String fieldPart = getterName.substring(3);
            if (fieldPart.isEmpty()) {
                return null;
            }
            return fieldPart.substring(0, 1).toLowerCase() + fieldPart.substring(1);
        }

        /**
         * Vérifie si la méthode getter retourne directement le champ spécifié.
         *
         * @param method    La méthode getter.
         * @param fieldName Le nom du champ.
         * @return Vrai si la méthode retourne le champ directement, sinon Faux.
         */
        private boolean returnsField(MethodDeclaration method, String fieldName) {
            if (!method.getBody().isPresent()) {
                return false;
            }
            BlockStmt body = method.getBody().get();
            List<ReturnStmt> returns = body.findAll(ReturnStmt.class);
            if (returns.size() != 1) {
                return false;
            }
            ReturnStmt returnStmt = returns.get(0);
            Optional<Expression> expr = returnStmt.getExpression();
            if (!expr.isPresent()) {
                return false;
            }
            return expr.get().isNameExpr() && expr.get().asNameExpr().getNameAsString().equals(fieldName);
        }
    }
}