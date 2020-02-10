package Ket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class SviArtikli {// ovaj deo se odnosi na samu tabelu svi artikli, azuriranja i slicno
	String connectionString;
	Connection con;

	public SviArtikli(String connStr) {
		connectionString = connStr;
	}

	public void connect() {// konektovanje na bazu
		try {
			con = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {// disconnect sa baze
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sifre() {// sifre u bazu podataka inicijalno kada sam tek napravio bazu
		// ovde sam pokupio Sifru, SifruDizajnera i ID iz baze
		// i onda ih spojio u jednu rec i napravio listu tih reci
		// koje predstavljaju SifruProizvoda
		// kod do ubacivanja u listu mogu da iskoristim kasnije za ubacivanje
		// novih sifri proizvoda (pri kreiranju novih podataka za bazu

		try {

			String upit = "select Sifra, SifraDizajnera, ID from SviArtikli";
			Statement stm = con.createStatement();
			ResultSet eres = stm.executeQuery(upit);

			while (eres.next()) {

				String sifra = eres.getString("Sifra");
				String sifrad = eres.getString("SifraDizajnera");
				int id = eres.getInt("ID");

				String sifraProizvoda = sifra.toUpperCase() + sifrad.toUpperCase() + id;// ovde kreiram SifruSroizvoda
				// ukoliko dogovorim sa Djurom da mu slova u sifri budu velika slova, samo
				// stavim
				// sifra.toUpperCase(); sifrad.toUpperCase()

				String kveri = "UPDATE SviArtikli SET SifraProizvoda = '" + sifraProizvoda + "' WHERE ID=" + id;
				Statement st = con.createStatement();
				st.executeUpdate(kveri);

			}
			System.out.println("Unosim podatke u bazu");// indikator da podatke ubacujem u bazu

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// UNOS NOVOG PROIZVODA, SVE INFORMACIJE!!!
	public void unesiProizvod() {// unos novog proizvoda u bazu. Ovde ne unosi stanje za vec postojeci proizvod!
									// To radi u azuriranju!
		Scanner sc = new Scanner(System.in);

		// int izlaznaCena=sc.nextInt();//izlazna cena ce biti automatski upisana kad
		// proizvod bude prodat
		// int zarada=sc.nextInt();//zaradu sam izracuna program i skladisti odmah to u
		// bazu za taj proizvod kada bude prodat

		String sifraProizvoda = "";// lokalna varijabla koja skladisti sifru proizvoda
		int id = 0;// definisana i promenljiva za ID u bazi

		try {// sve radi! Lepo unosi i azurira bazu
			System.out.println(
					"Unesite: Vrsta proizvoda, Naziv dizajnera, ImeProizvoda, MaterijalDimenzija, Sifra, SifraDizajnera");
			// prvi upit pocinje unos u sql tabelu dok korisnik ne unese sifru i sifru
			// dizajnera
			String vrstaProizvoda = sc.nextLine();// sve skenere, povezati sa svojim buducim poljem!
			String nazivDizajnera = sc.nextLine();
			String imeProizvoda = sc.nextLine();
			String materijalDimenzija = sc.nextLine();
			String sifra = sc.nextLine().toUpperCase();
			String sifraDizajnera = sc.nextLine().toUpperCase();

			String upit = "insert into SviArtikli (VrstaProizvoda, NazivDizajnera, ImeProizvoda, MaterijalDimenzija, Sifra, SifraDizajnera)\n"
					+ "values ('" + vrstaProizvoda + "','" + nazivDizajnera + "','" + imeProizvoda + "','"
					+ materijalDimenzija + "','" + sifra + "','" + sifraDizajnera + "' )";
			Statement st = con.createStatement();
			st.executeUpdate(upit);

			// kada korisnik unese sifru i sifru dizajnera, program salje upit u sql i vadi
			// ID na osnovu imena proizvoda
			String upit2 = "select ID from SviArtikli where ImeProizvoda='" + imeProizvoda + "'";
			Statement st2 = con.createStatement();
			ResultSet rs2 = st2.executeQuery(upit2);
			while (rs2.next()) {

				id = rs2.getInt("ID");

				sifraProizvoda = sifra + sifraDizajnera + id;
				System.out.println("Sifra proizvoda je: " + sifraProizvoda);
				// ovde kreiram SifruSroizvoda na osnovu sifre/sifre dizajnera koje je uneo
				// korisnik i ID koji je
				// automatski generisan pri kreiranju baze
			}

			// nastavljamo sa radom nad tabelom posto smo dobili sifruProizvoda
			System.out.println("Unesite Euro ulaz, Ulaznu cenu, PDV, Prodajna Cena, Rabat, Kurs, Katalog, Stanje: ");
			int euroUlaz = sc.nextInt();// Sve skenere povezati sa svojim buducim poljem!!!
			int ulaznaCena = sc.nextInt();
			int pdv = sc.nextInt();
			int prodajnaCena = sc.nextInt();
			int rabat = sc.nextInt();
			LocalDate datum = LocalDate.now();
			String kurs = sc.next();
			String katalog = sc.next();
			int stanje = sc.nextInt();

			String update = "UPDATE SviArtikli SET SifraProizvoda = '" + sifraProizvoda + "', EuroUlaz=" + euroUlaz
					+ ", UlaznaCena=" + ulaznaCena + ", PDV=" + pdv + ", ProdajnaCena=" + prodajnaCena + ", Rabat="
					+ rabat + ", Datum='" + datum + "', Kurs=" + kurs + ", Katalog='" + katalog + "',  Stanje=" + stanje
					+ " WHERE ID=" + id;
			Statement st3 = con.createStatement();
			st3.executeUpdate(update);
			sc.close();

			// update stanje u SviArtikli, update raditi po sifri proizvoda

			System.out.println(imeProizvoda + " unesen u bazu.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// PRETRAGA PROIZVODA
	public void pretragaPoVrsti() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Unesite vrstu proizvoda: ");
		String vrsta = sc.nextLine();
		vrsta = vrsta.substring(0, 1).toUpperCase() + vrsta.substring(1).toLowerCase();
		// ukoliko unese prvo slovo malo, kod iznad ga menja u veliko(samo prvo slovo)

		try {
			String upit = "select * from SviArtikli where VrstaProizvoda = '" + vrsta + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);
			while (rs.next()) {
				String vrstaProizvoda = rs.getString("VrstaProizvoda");
				String imeProizvoda = rs.getString("ImeProizvoda");
				String nazivDizajnera = rs.getString("NazivDizajnera");
				String materijalDimenzija = rs.getString("MaterijalDimenzija");
				String sifraProizvoda = rs.getString("SifraProizvoda");
				int cena = rs.getInt("IzlaznaCena");
				int stanje = rs.getInt("Stanje");

				System.out.println("Vrsta proizvoda: " + vrstaProizvoda + "\n" + "Ime proizvoda: " + imeProizvoda + "\n"
						+ "Dizajner: " + nazivDizajnera + "\n" + "Materijal: " + materijalDimenzija + "\n"
						+ "Sifra proizvoda: " + sifraProizvoda + "\n" + "Cena: " + cena + " RSD \n Stanje: " + stanje);

			}
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void pretragaPoDizajneru() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Unesite dizajnera: ");
		String dizajner = sc.nextLine().toUpperCase();

		try {
			String upit = "select * from SviArtikli where NazivDizajnera = '" + dizajner + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);
			while (rs.next()) {
				String vrstaProizvoda = rs.getString("VrstaProizvoda");
				String imeProizvoda = rs.getString("ImeProizvoda");
				String materijalDimenzija = rs.getString("MaterijalDimenzija");
				String sifraProizvoda = rs.getString("SifraProizvoda");
				int cena = rs.getInt("IzlaznaCena");
				int stanje = rs.getInt("Stanje");

				System.out.println("Vrsta proizvoda: " + vrstaProizvoda + "\n" + "Ime proizvoda: " + imeProizvoda + "\n"
						+ "Materijal: " + materijalDimenzija + "\n" + "Sifra proizvoda: " + sifraProizvoda + "\n"
						+ "Cena: " + cena + " RSD \n Stanje: " + stanje);

			}
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void pretragaPoImenuProizvoda() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Unesite ime proizvoda: ");
		String imeProizvoda = sc.nextLine().toUpperCase();

		try {
			String upit = "select * from SviArtikli where ImeProizvoda = '" + imeProizvoda + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);
			while (rs.next()) {
				String vrstaProizvoda = rs.getString("VrstaProizvoda");
				String nazivDizajnera = rs.getString("NazivDizajnera");
				String materijalDimenzija = rs.getString("MaterijalDimenzija");
				String sifraProizvoda = rs.getString("SifraProizvoda");
				int cena = rs.getInt("IzlaznaCena");
				int stanje = rs.getInt("Stanje");

				System.out.println("Vrsta proizvoda: " + vrstaProizvoda + "\n" + "Naziv dizajnera: " + nazivDizajnera
						+ "\n" + "Materijal: " + materijalDimenzija + "\n" + "Sifra proizvoda: " + sifraProizvoda + "\n"
						+ "Cena: " + cena + " RSD \n Stanje: " + stanje);

			}
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}// SsIP8367

	public void pretragaPoSifriProizvoda() {// pretraga artikala po sifri proizvoda
		Scanner sc = new Scanner(System.in);
		System.out.println("Unesite sifru proizvoda: ");
		String sifraProizvoda = sc.nextLine().toUpperCase();
		// ukoliko sa Djurom dogovorim da mu slova u sifri budu velika
		// String sifraProizvoda = sc.nextLine().toUpperCase;
		// tako da ce moci da ih upisuje sve malim slovima, ukoliko neku sifru zna
		// napamet

		try {
			String upit = "select * from SviArtikli where SifraProizvoda = '" + sifraProizvoda + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);
			while (rs.next()) {
				String vrstaProizvoda = rs.getString("VrstaProizvoda");
				String imeProizvoda = rs.getString("ImeProizvoda");
				String nazivDizajnera = rs.getString("NazivDizajnera");
				String materijalDimenzija = rs.getString("MaterijalDimenzija");
				int cena = rs.getInt("IzlaznaCena");
				int stanje = rs.getInt("Stanje");

				System.out.println("Vrsta proizvoda: " + vrstaProizvoda + "\n" + "Ime proizvoda: " + imeProizvoda + "\n"
						+ "Dizajner: " + nazivDizajnera + "\n" + "Materijal: " + materijalDimenzija + "\n"
						+ "Sifra proizvoda: " + sifraProizvoda + "\n" + "Cena: " + cena + " RSD \n Stanje: " + stanje);

			}
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// AZURIRANJE

	public void azuriranje() {
		// azuriranje baze moze da bude uradjeno samo po sifri proizvoda.
		// dakle ako ne znamo sifru proizvoda, nema menjanja cene ili slicno
		// Sugestija je da kada ovim putem stavlja proizvod na stanje da stavi broj, a
		// ne tekst!
		// verovatno cu dodati funkcionalnost da kada nesto sa stanja proda, da mu
		// smanji stanje za 1 -- radim na tome 08.11. 12:00
		// trenutno moze rucno da azurira stanje
		Scanner sc = new Scanner(System.in);
		// azuriranje i datuma kad se azurira artikal
		String unos1, unos2, unos3;

		try {
			System.out.println("Azurirate tabelu SviArtikli: ");
			System.out.println("Kolona: ");
			unos1 = sc.nextLine();// sta
			System.out.println("Vrednost: ");
			unos2 = sc.nextLine();// koliko
			System.out.println("Gde je Sifra proizvoda: ");
			unos3 = sc.nextLine();// koja

			String upit = "update SviArtikli " + "set " + unos1 + "= '" + unos2 + "'" + "where SifraProizvoda = '"
					+ unos3 + "'";
			Statement st = con.createStatement();
			st.executeUpdate(upit);
			System.out
					.println("Baza SviArtikli, kolona " + unos1 + " za SifruProizvoda" + unos3 + " uspesno azurirana.");
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void brisanje() {// brisanje proizvoda iz baze
		Scanner sc = new Scanner(System.in);

		try {
			String unos = sc.next();
			String upit = "delete from SviArtikli where SifraProizvoda ='" + unos + "'";
			Statement st = con.createStatement();
			st.executeUpdate(upit);
			System.out.println("Proizvod sifra: " + unos + " je obrisan iz baze.");
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
