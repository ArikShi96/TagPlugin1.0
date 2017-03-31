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

import Controller.TagInitController;

public class InitTest extends AbstractHandler {

	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			String resource = "Conf.xml";
			Reader is = Resources.getResourceAsReader(resource);
			SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(is);
			SqlSession session = sessionFactory.openSession();

			TagInitController controller = new TagInitController();
			Scanner scanner = new Scanner(System.in);
			String str = scanner.nextLine();

			while (str != null && !str.equals("-1")) {
				if (controller.initProject(str, session)) {
					//session.commit();
					System.out.println("000!");
				}
				else System.out.println("failed!");
				str=scanner.nextLine();
			}
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
