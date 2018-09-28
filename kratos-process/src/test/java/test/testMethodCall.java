package test;

import java.io.File;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class testMethodCall {

	private static final String FILE_PATH = "C:/Users/Twilight/Desktop/parserTest/A.java";

	public static void main(String[] args) throws Exception {
		TypeSolver typeSolver = new CombinedTypeSolver(
				new ReflectionTypeSolver(), new JavaParserTypeSolver(new File(
						"C:/Users/Twilight/Desktop/parserTest/")));

		CompilationUnit cu = JavaParser.parse(new File(FILE_PATH));

		System.out.format("Analyzing method calls of file %s\n", cu
				.getStorage().get().getPath());

		List<MethodCallExpr> methodCalls = cu.findAll(MethodCallExpr.class);
		for (MethodCallExpr mc : methodCalls) {
			try {
				ResolvedMethodDeclaration correspondingDeclaration = JavaParserFacade
						.get(typeSolver).solve(mc)
						.getCorrespondingDeclaration();
				if (correspondingDeclaration instanceof JavaParserMethodDeclaration) {
					JavaParserMethodDeclaration declaration = (JavaParserMethodDeclaration) correspondingDeclaration;
					Node wrappedNode = declaration.getWrappedNode();
					MethodDeclaration wrappedDeclaration = (MethodDeclaration) wrappedNode;
					System.out.format("L%dC%d,L%dC%d -> %s, (L%dC%d,L%dC%d)\n",
							mc.getBegin().get().line,
							mc.getBegin().get().column, mc.getEnd().get().line,
							mc.getEnd().get().column,
							correspondingDeclaration.getQualifiedSignature(),
							wrappedDeclaration.getBegin().get().line,
							wrappedDeclaration.getBegin().get().column,
							wrappedDeclaration.getEnd().get().line,
							wrappedDeclaration.getEnd().get().column);
					Node temp = wrappedNode.findRootNode();
					if (temp instanceof CompilationUnit) {
						CompilationUnit compilationUnit = (CompilationUnit) temp;
						if (compilationUnit.getStorage().isPresent()) {
							System.out.format("Path: %s\n", compilationUnit
									.getStorage().get().getPath());
						} else {
							System.out.println("Couldn't resolve path");
						}
					}
				}
			} catch (Exception e) {
				System.err.println("Cannot Solve");
			}
		}
	}

}
