package Handler;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import Controller.FetchController;
import Controller.TagInitController;
import Model.Class;

public class GetClassTest extends AbstractHandler {

	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			String resource = "Conf.xml";
			Reader is = Resources.getResourceAsReader(resource);
			SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(is);
			SqlSession session = sessionFactory.openSession();

			FetchController controller = new FetchController();
			Scanner scanner = new Scanner(System.in);
			String str = scanner.nextLine();

			while (str != null && !str.equals("-1")) {
				for(Class cl:controller.getClassByType(str, 1, 2, session)){
					System.out.println(cl.getName());
					System.out.println(cl.getContent());
				}
				str=scanner.nextLine();
			}
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
