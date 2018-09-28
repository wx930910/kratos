package test;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

public class testReference {

	public static String FILE_PATH = "C:/Users/Twilight/Desktop/test2.java";

	public static void main(String[] args) throws FileNotFoundException {

		testReference.getVariableParameterType();

	}

	public static void typeDeclarationVisitor() throws FileNotFoundException {
		CompilationUnit cu = JavaParser.parse(new File(FILE_PATH));
		for (TypeDeclaration td : cu.getTypes()) {
			// td.getm
			// System.out.println(td.getClassName());
		}
	}

	public static void classVisitor() throws FileNotFoundException {

		CompilationUnit cu = JavaParser.parse(new File(FILE_PATH));
		VoidVisitor<?> cv = new classVisitor();
		cv.visit(cu, null);

	}

	public static void getVariableParameterType() throws FileNotFoundException {
		TypeSolver typeSolver = new CombinedTypeSolver();
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
		JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

		CompilationUnit cu = JavaParser.parse(new File(FILE_PATH));
		NodeList<BodyDeclaration<?>> parameters = cu
				.getClassByName("COSString").map(coid -> coid.getMembers())
				.orElseThrow(RuntimeException::new);
		for (BodyDeclaration<?> p : parameters) {
			if (p.isFieldDeclaration()) {
				System.out.println("Parameter Type: "
						+ p.asFieldDeclaration().getVariable(0).getType());
			} else if (p.isConstructorDeclaration()) {
				// System.out.println("Constructor Dec: ");
				// System.out
				// .println(p.asConstructorDeclaration().getParameters());
			}

		}

		// System.out.println();

		cu.findAll(VariableDeclarationExpr.class).forEach(ae -> {
			// ResolvedType resolvedType = ae.calculateResolvedType();
			// System.out.println(ae.toString() + " is a: " + resolvedType);
			// System.out.println(ae.toString());
				System.out.println("Variables Type: " + ae.getElementType());
			});
	}

	public static void variableVisitor() throws FileNotFoundException {

		CompilationUnit cu = JavaParser.parse(new File(FILE_PATH));
		VoidVisitor<?> variablePrinter = new variablePrinter();

		variablePrinter.visit(cu, null);

	}

	public static void methodVisitor() throws FileNotFoundException {
		CompilationUnit cu = JavaParser.parse(new File(FILE_PATH));
		VoidVisitor<?> methodNameVisitor = new MethodNamePrinter();
		methodNameVisitor.visit(cu, null);
	}

	private static class MethodNamePrinter extends VoidVisitorAdapter<Void> {

		@Override
		public void visit(MethodDeclaration md, Void arg) {
			super.visit(md, arg);
			System.out.println("Method Name Printed: " + md.getName());

		}

	}

	private static class classVisitor extends VoidVisitorAdapter<Void> {

		@Override
		public void visit(ClassOrInterfaceDeclaration coidd, Void arg) {
			super.visit(coidd, arg);
			System.out.println(coidd.getNameAsString());
		}

	}

	private static class variablePrinter extends VoidVisitorAdapter<Void> {

		@Override
		public void visit(VariableDeclarationExpr vd, Void arg) {
			super.visit(vd, arg);
			System.out.println("Variable Name Printed: " + vd.toString());
			System.out.println("Type: " + vd.getElementType());
			System.out.println("Type Status: "
					+ vd.getElementType().getParentNodeForChildren());
			// vd.get
		}

	}

	public static void getDeclaration() throws FileNotFoundException {

		CompilationUnit cu = JavaParser.parse(new File(FILE_PATH));
		FieldDeclaration fieldDeclaration = Navigator.findNodeOfGivenClass(cu,
				FieldDeclaration.class);
		System.out.println(fieldDeclaration.getVariables());

	}

}