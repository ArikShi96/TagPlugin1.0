package Controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import IOperation.IClassOperation;
import IOperation.IClass_TagOperation;
import IOperation.IPackageOperation;
import IOperation.IProjectOperation;
import IOperation.ISourceFileOperation;
import Model.Class;
import Model.Class_Tag;
import Model.Import_Info;
import Model.Package;
import Model.Project;
import Model.SourceFile;

public class TagInitController {

	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

	public TagInitController() {
		// TODO Auto-generated constructor stub
	}

	public boolean initProject(String name, SqlSession session) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		// Loop over all projects
		for (IProject iproject : projects) {
			try {
				if (iproject.isNatureEnabled(JDT_NATURE) && iproject.getName().equals(name)) {
					// handle the project info
					Project project = new Project();
					project.setName(name);
					project.setSource(iproject.getLocation().toString());

					IProjectOperation operation = session.getMapper(IProjectOperation.class);
					//operation.insert(project);

					if(!initPackages(project.getId(), session, iproject)){
						return false;
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public boolean initPackages(int projectid, SqlSession session, IProject iproject) throws JavaModelException{
		IPackageFragment[] packages = JavaCore.create(iproject).getPackageFragments();
		for (IPackageFragment ipackage : packages) {
			if (ipackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				//handle the package info
				Package pack=new Package();
				pack.setName(ipackage.getElementName());
				pack.setProject_id(projectid);
				
				IPackageOperation operation=session.getMapper(IPackageOperation.class);
				//operation.insert(pack);
				
				if(!initSources(pack.getId(), session, ipackage)){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean initSources(int packageid,SqlSession session, IPackageFragment ipackage) throws JavaModelException{
		for (ICompilationUnit iunit : ipackage.getCompilationUnits()) {
			//handle the source file info
			SourceFile sourcefile=new SourceFile();
			sourcefile.setName(iunit.getElementName());
			sourcefile.setPackage_id(packageid);
			
			ISourceFileOperation operation=session.getMapper(ISourceFileOperation.class);
			//operation.insert(sourcefile);
			
			if(!initClasses(sourcefile.getId(), session, iunit)){
				return false;
			}
		}
		return true;
	}

	public boolean initClasses(int fileid,SqlSession session, ICompilationUnit iunit) throws JavaModelException {
		for (IType itype : iunit.getAllTypes()) {
//			System.out.println("itype ******** 1  " + itype.toString());
//			System.out.println("itype ******** 2 " + itype.getElementName());
			//parse source file tags
			IImportDeclaration[] imports=iunit.getImports();
			List<Import_Info> import_Infos=Import_Info.getImport_Infos(session);		
			Set<Integer> tagidset=new HashSet<>();
			for(IImportDeclaration item:imports){
				for(Import_Info import_Info:import_Infos){
					if(parseImport(item.getElementName(), import_Info.getKeyword())){
						tagidset.add(import_Info.getTag_id());
					}
				}
			}
			
			if(itype.isClass()){
				Class cl=new Class();
				cl.setName(itype.getElementName());
				cl.setFile_id(fileid);
				cl.setLangusge("Java");
				cl.setContent(itype.getSource());
				
				//handle the source file info
				
				IClassOperation operation=session.getMapper(IClassOperation.class);
				//operation.insert(cl);
				
				IClass_TagOperation tagOperation=session.getMapper(IClass_TagOperation.class);
				for(int i:tagidset){
					Class_Tag class_tag=new Class_Tag();
					class_tag.setClass_id(cl.getId());
					class_tag.setFunc_tag_id(i);
					//tagOperation.insert(class_tag);
				}
				
			}
			IMethod[] methods = itype.getMethods();
			
			for(IMethod method : methods){
			}
			
		}
		return true;
	}
	
	private static boolean parseImport(String item,String keyword){
		if(keyword.contains("*")&&item.contains(keyword.substring(0,keyword.length()-2))){
			return true;
		}
		else if(item.equals(keyword)){
			return true;
		}
		return false;
	}
}