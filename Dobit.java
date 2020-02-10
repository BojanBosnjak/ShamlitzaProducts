package Ket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class Dobit { // rad nad tabelom u kojoj se vodi racun o prodatim artiklima
	String connectionString;
	Connection con;

	public Dobit(String connStr) {// puni bazu prodatim artiklima i kolicinom novca koju je zaradio
									// na kraju se nalazi metoda koja cita ukupne cifre: za koliko je
									// ukupno kupljeno, za koliko je ukupno prodato i na kraju kolika je ukupna
									// zarada
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

	public void prodatiArtikal() {// unos prodatih artikala (pojedinacno) vraca kao rezultat artikal koji je
									// upravo prodat i zaradu
		Scanner sc = new Scanner(System.in);
		System.out.println(
				"SifraProizvoda, ImeProizvoda, NazivDizajnera, Faktura, ProdajnaCena, NabavnaCena, Datum, Zarada");
		// zaradu automatski generise i ubacuje u tabelu
		int stanjeProizvoda=0;
		try {//ovde moram napraviti da se cifre mnoze sa brojem prodatih komada
			String sifraProizvoda = sc.nextLine();
			String imeProizvoda = sc.nextLine();
			String nazivDizajnera = sc.nextLine();
			String faktura = sc.nextLine();
			int prodajnaCena = sc.nextInt();
			int nabavnaCena = sc.nextInt();
			LocalDate datum = LocalDate.now();
			int zarada = prodajnaCena - nabavnaCena;
			int brProdatih = sc.nextInt();//da li je 1, 2, 3, 5... ovo se kasnije oduzima od stanja u radnji

			String upit = "insert into BazaProdatihArtikala (SifraProizvoda, ImeProizvoda, NazivDizajnera, Faktura, ProdajnaCena, NabavnaCena, Datum, Zarada, BrProdatih)\n"
					+ "values ('" + sifraProizvoda + "', '" + imeProizvoda + "', '" + nazivDizajnera + "', '" + faktura
					+ "', " + prodajnaCena + " , " + nabavnaCena + " , '" + datum + "'," + zarada + ", "+brProdatih+" )";
			Statement st = con.createStatement();
			st.executeUpdate(upit);//on je ovde zavrsio sa ubacivanjem prodatih artikala, imamo sve unesene parametre
			
			System.out.println("Zarada na proizvodu: " + imeProizvoda + " je:" + zarada);
			
			String upit1="select Stanje from SviArtikli where SifraProizvoda = '"+sifraProizvoda+"'";
			Statement st1 = con.createStatement();
			ResultSet rs1 = st1.executeQuery(upit1);
			
			rs1.next();
			stanjeProizvoda = rs1.getInt(1);
			
			// staviti da se skida prodati artikalsa stanja. Drugim recima update stanja

			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void vrednostSvihProdatih() { // vraca vrednost svih prodatih artikala u toku celog perioda
		int suma = 0;

		try {
			String upit = "select sum (Zarada) from BazaProdatihArtikala";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);

			rs.next();
			suma = rs.getInt(1);
			System.out.println("Ukupna zarada je: " + suma);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void prodatoMesecno() {// izbacuje artikle koji su prodati od - do datuma koji navede
									// na kraju iznese ukupnu zaradu
		Scanner sc = new Scanner(System.in);
		System.out.println("Datum unosite u formatu yyyy-mm-dd");
		int mesecnaZarada = 0;

		try {
			String datumOd = sc.nextLine();
			String datumDo = sc.nextLine();// prvo vadi sve troskove i prikazuje ih
			String upit = "SELECT * from BazaProdatihArtikala where (Datum BETWEEN '" + datumOd + "' AND '" + datumDo
					+ "')";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);

			while (rs.next()) {// TipTroska, Poverilac, KolicinaNovca, PoFakturi, Datum, Zarada
				// na ovaj nacin ne izbacuje prvi trosak
				System.out.println("Sifra proizvoda: " + rs.getString("SifraProizvoda") + "\t Ime proizvoda: "
						+ rs.getString("ImeProizvoda") + "\t Naziv dizajnera: " + rs.getString("NazivDizajnera")
						+ "\t Faktura: " + rs.getString("Faktura") + "\t Prodajna cena: " + rs.getInt("ProdajnaCena")
						+ "\t Nabavna cena: " + rs.getInt("NabavnaCena") + "\t Datum: " + rs.getString("Datum")
						+ "\t Zarada: " + rs.getInt("Zarada"));
			}

			String upit2 = "select sum (Zarada) from BazaProdatihArtikala where Datum between '" + datumOd + "' and '"
					+ datumDo + "'"; // potom ukupnu vrednost zarade na prodatim
										// artiklima u tom periodu
			Statement st2 = con.createStatement();
			ResultSet rs2 = st2.executeQuery(upit2);

			rs2.next();
			mesecnaZarada = rs2.getInt(1);
			System.out
					.println("\nKolicina novca zaradjenog od " + datumOd + " do " + datumDo + " je: " + mesecnaZarada);
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void razlikaUkupno() {
		// ovde racuna razliku izmedju zaradjenog i potrosenog novca ukupno

		try {
			String upit = "select sum (Zarada) from BazaProdatihArtikala";// ukupna vrednost zarade na prodatim
																			// artiklima
			Statement st = con.createStatement();
			ResultSet rs;
			rs = st.executeQuery(upit);

			rs.next();
			int zarada = rs.getInt(1);

			String upit1 = "select sum (KolicinaNovca) from BazaTroskova";// ukupna vrednost zarade mesecnih troskova
			Statement st1 = con.createStatement();
			ResultSet rs1;
			rs1 = st1.executeQuery(upit1);

			rs1.next();
			int trosak = rs1.getInt(1);

			int dobit = zarada - trosak;

			System.out.println("Mesecna dobit je: " + dobit);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void razlikaMesecno() {
		// napraviti razliku izmedju mesecne zarade i troska
	}

}
