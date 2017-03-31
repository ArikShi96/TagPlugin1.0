package Handler;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodVisitor extends ASTVisitor{
	ArrayList<MethodDeclaration> methods=new ArrayList<>();
	ArrayList<EnumDeclaration> enums=new ArrayList<>();
	public MethodVisitor() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean visit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		methods.add(node);
		return super.visit(node);
	}
	@Override
	public boolean visit(EnumDeclaration node) {
		// TODO Auto-generated method stub
		enums.add(node);
		return super.visit(node);
	}
	public ArrayList<MethodDeclaration> getMethods(){
		return methods;
	}
	public ArrayList<EnumDeclaration> getEnums(){
		return enums;
	}
}