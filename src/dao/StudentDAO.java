package dao;
import java.sql.*;
import java.util.ArrayList;
import model.*;

/**
 * Classe d'acces aux donnees contenues dans la table student
 * 
 * @author ESIGELEC - TIC Department
 * @version 2.0
 * */
public class StudentDAO extends ConnectionDAO {
	/**
	 * Constructor
	 * 
	 */
	public StudentDAO() {
		super();
	}

	/**
	 * Permet d'ajouter un eleve dans la table student.
	 * Le mode est auto-commit par defaut : chaque insertion est validee
	 * 
	 * @param student le eleve a ajouter
	 * @return retourne le nombre de lignes ajoutees dans la table
	 */
	public int add(Student student) {
		Connection con = null;
		PreparedStatement ps = null;
		int returnValue = 0;
	

		// connexion a la base de donnees
		try {

			// tentative de connexion
			con = DriverManager.getConnection(URL, LOGIN, PASS);
			// preparation de l'instruction SQL, chaque ? represente une valeur
			// a communiquer dans l'insertion.
			// les getters permettent de recuperer les valeurs des attributs souhaites
			if(student.getSector().equals("Apprenti")) {
				ps = con.prepareStatement("INSERT INTO student(id, name, fistName, mail, password, year, idsector) VALUES(?, ?, ?, ?, ?, ?, 2)");

			}
			else if(student.getSector().equals("Classique")) {
				ps = con.prepareStatement("INSERT INTO student(id, name, fistName, mail, password, year, idsector) VALUES(?, ?, ?, ?, ?, ?, 1)");

			}
			ps.setInt(1, student.getId());
			ps.setString(2, student.getName());
			ps.setString(3, student.getFirstName());
			ps.setString(4, student.getMail());
			ps.setString(5, student.getPassword());
			ps.setInt(6, student.getYear());
			

			// Execution de la requete
			returnValue = ps.executeUpdate();

		} catch (Exception e) {
			if (e.getMessage().contains("ORA-00001"))
				System.out.println("Cet identifiant de eleve existe déjà. Ajout impossible !");
			else
				e.printStackTrace();
		} finally {
			// fermeture du preparedStatement et de la connexion
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception ignore) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception ignore) {
			}
		}
		return returnValue;
	}

	/**
	 * Permet de modifier un eleve dans la table student.
	 * Le mode est auto-commit par defaut : chaque modification est validee
	 * 
	 * @param student le eleve a modifier
	 * @return retourne le nombre de lignes modifiees dans la table
	 */
	public int update(Student student) {
		Connection con = null;
		PreparedStatement ps = null;
		int returnValue = 0;

		// connexion a la base de donnees
		try {

			// tentative de connexion
			con = DriverManager.getConnection(URL, LOGIN, PASS);
			// preparation de l'instruction SQL, chaque ? represente une valeur
			// a communiquer dans la modification.
			// les getters permettent de recuperer les valeurs des attributs souhaites
			ps = con.prepareStatement("UPDATE student set name = ?, firstName = ?, mail = ?, password= ?, year= ? WHERE id = ?");
			ps.setString(1, student.getName());
			ps.setString(2, student.getFirstName());
			ps.setString(3, student.getMail());
			ps.setString(4, student.getPassword());
			ps.setInt(5, student.getYear());
			ps.setInt(6, student.getId());
			
			// Execution de la requete
			returnValue = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// fermeture du preparedStatement et de la connexion
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception ignore) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception ignore) {
			}
		}
		return returnValue;
	}

	/**
	 * Permet de supprimer un eleve par id dans la table student.
	 * Si ce dernier possede des articles, la suppression n'a pas lieu.
	 * Le mode est auto-commit par defaut : chaque suppression est validee
	 * 
	 * @param id l'id du student à supprimer
	 * @return retourne le nombre de lignes supprimees dans la table
	 */
	public int delete(int id) {
		Connection con = null;
		PreparedStatement ps = null;
		int returnValue = 0;

		// connexion a la base de donnees
		try {

			// tentative de connexion
			con = DriverManager.getConnection(URL, LOGIN, PASS);
			// preparation de l'instruction SQL, le ? represente la valeur de l'ID
			// a communiquer dans la suppression.
			// le getter permet de recuperer la valeur de l'ID du eleve
			ps = con.prepareStatement("DELETE FROM student WHERE id = ?");
			ps.setInt(1, id);

			// Execution de la requete
			returnValue = ps.executeUpdate();

		} catch (Exception e) {
			if (e.getMessage().contains("ORA-02292"))
				System.out.println("Ce eleve possede des articles, suppression impossible !"
						         + " Supprimer d'abord ses articles ou utiiser la méthode de suppression avec articles.");
			else
				e.printStackTrace();
		} finally {
			// fermeture du preparedStatement et de la connexion
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception ignore) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception ignore) {
			}
		}
		return returnValue;
	}


	/**
	 * Permet de recuperer un eleve a partir de sa reference
	 * 
	 * @param reference la reference du eleve a recuperer
	 * @return le eleve trouve;
	 * 			null si aucun eleve ne correspond a cette reference
	 */
	public Student get(int id, String password) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Student returnValue = null;
		String sector= "Sector undefined";

		// connexion a la base de donnees
		try {

			con = DriverManager.getConnection(URL, LOGIN, PASS);
			ps = con.prepareStatement("SELECT * FROM student WHERE id = ? AND password= ?");
			ps.setInt(1, id);
			ps.setString(2, password);

			// on execute la requete
			// rs contient un pointeur situe juste avant la premiere ligne retournee
			rs = ps.executeQuery();
			// passe a la premiere (et unique) ligne retournee
			if (rs.next()) {
				if(rs.getInt("idsector")==2) {
					sector = "Apprenti";
				}
				else if(rs.getInt("idsector")==1) {
					sector = "Classique";
				}
				returnValue = new Student(rs.getInt("id"),
									       rs.getString("name"),
									       rs.getString("firstName"),
									       rs.getString("mail"),
									       rs.getString("password"),
									       rs.getInt("year"),
									       sector);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			// fermeture du ResultSet, du PreparedStatement et de la Connexion
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ignore) {
			}
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception ignore) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception ignore) {
			}
		}
		return returnValue;
	}

	/**
	 * Permet de recuperer tous les eleves stockes dans la table eleve
	 * 
	 * @return une ArrayList de eleve
	 */
	public ArrayList<Student> getList() {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Student> returnValue = new ArrayList<Student>();
		String sector= "Sector undefined";

		// connexion a la base de donnees
		try {
			con = DriverManager.getConnection(URL, LOGIN, PASS);
			ps = con.prepareStatement("SELECT * FROM student ORDER BY id");

			// on execute la requete
			rs = ps.executeQuery();
			// on parcourt les lignes du resultat
			while (rs.next()) {
				if(rs.getInt("idsector")==2) {
					sector = "Apprenti";
				}
				else if(rs.getInt("idsector")==1) {
					sector = "Classique";
				}
				returnValue.add(new Student(rs.getInt("id"),
					       rs.getString("name"),
					       rs.getString("firstName"),
					       rs.getString("mail"),
					       rs.getString("password"),
					       rs.getInt("year"),
					       sector));
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			// fermeture du rs, du preparedStatement et de la connexion
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ignore) {
			}
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ignore) {
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception ignore) {
			}
		}
		return returnValue;
	}

	
	/**
	 * ATTENTION : Cette méthode n'a pas vocation à être executée lors d'une utilisation normale du programme !
	 * Elle existe uniquement pour TESTER les méthodes écrites au-dessus !
	 * 
	 * @param args non utilisés
	 * @throws SQLException si une erreur se produit lors de la communication avec la BDD
	 */
	
	/*
	 public static void main(String[] args) throws SQLException {
		int returnValue;
		StudentDAO studentDAO = new StudentDAO();
		// Ce test va utiliser directement votre BDD, on essaie d'éviter les collisions avec vos données en prenant de grands ID
		int[] ids = {424242, 424243, 424244};
		// test du constructeur
		Student s1 = new Student(ids[0], "Mon eleve principal", "Rouen", "moneleveprincipal@mail.com");
		Student s2 = new Student(ids[1], "Mon eleve secondaire", "Le Havre", "monelevesecondaire@mail.com");
		Student s3 = new Student(ids[2], "Mon eleve de secours", "Paris", "monelevesecours@mail.com");
		// test de la methode add
		returnValue = studentDAO.add(s1);
		System.out.println(returnValue + " eleve ajoute");
		returnValue = studentDAO.add(s2);
		System.out.println(returnValue + " eleve ajoute");
		returnValue = studentDAO.add(s3);
		System.out.println(returnValue + " eleve ajoute");
		System.out.println();
		
		// test de la methode get
		Student sg = studentDAO.get(1);
		// appel implicite de la methode toString de la classe Object (a eviter)
		System.out.println(sg);
		System.out.println();
		
		// test de la methode getList
		ArrayList<Student> list = studentDAO.getList();
		for (Student s : list) {
			// appel explicite de la methode toString de la classe Object (a privilegier)
			System.out.println(s.toString());
		}
		System.out.println();
		// test de la methode delete
		// On supprime les 3 articles qu'on a créé
		returnValue = 0;
		for (int id : ids) {
//			returnValue = studentDAO.delete(id);
			System.out.println(returnValue + " eleve supprime");
		}
		
		System.out.println();
	}
*/}