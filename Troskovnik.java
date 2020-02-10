package Ket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class Troskovnik {// rad nad tabelom u kojoj se vodi racuna o mesecnim troskovima
	String connectionString;
	Connection con;

	public Troskovnik(String connStr) {
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

	public void unesiTrosak() {// unos troskova u bazu troskova
		Scanner sc = new Scanner(System.in);
		System.out.println("TipTroska, Poverilac, KolicinaNovca, PoFakturi");
		try {
			String tipTroska = sc.nextLine();
			String poverilac = sc.nextLine();
			int kolicinaNovca = sc.nextInt();
			String faktura = sc.next();// mora biti jedna rec
			LocalDate datum = LocalDate.now();
			String datum2 = sc.nextLine();

			String upit = "insert into BazaTroskova (TipTroska, Poverilac, KolicinaNovca, PoFakturi, Datum, DatumZaPlacanje)\n"
					+ "values ('" + tipTroska + "', '" + poverilac + "', " + kolicinaNovca + ", '" + faktura + "', '"
					+ datum + "', '" + datum2 + "');"; // valja uneti ono sa skenera
			Statement st = con.createStatement();
			st.executeUpdate(upit);
			System.out.println("Trosak " + tipTroska + " uspesno unesen u bazu.");
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sviTroskovi() {// troskovi od dana kada je program napravljen, dakle od nultog dana
		Scanner sc = new Scanner(System.in);
		int suma = 0;

		try {
			String upit = "select sum (KolicinaNovca) from BazaTroskova";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);

			rs.next();
			suma = rs.getInt(1);
			System.out.println("Ukupni troskovi su: " + suma);

			sc.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void troskoviMesecno() {// zapravo su troskovi od - do datuma, ukupni troskovi
		Scanner sc = new Scanner(System.in);
		System.out.println("Datum unosite u formatu yyyy-mm-dd");
		int mesecniTrosak = 0;

		try {
			String datumOd = sc.nextLine();
			String datumDo = sc.nextLine();// prvo vadi sve troskove i prikazuje ih
			String upit = "SELECT * from BazaTroskova where (Datum BETWEEN '" + datumOd + "' AND '" + datumDo + "')";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);

			while (rs.next()) {// TipTroska, Poverilac, KolicinaNovca, PoFakturi, Datum
				// na ovaj nacin ne izbacuje prvi trosak
				System.out.println("ID Troska: " + rs.getInt("IDTroska") + "\t Tip troska: " + rs.getString("TipTroska")
						+ "\t Poverilac: " + rs.getString("Poverilac") + "\t Cena: " + rs.getInt("KolicinaNovca")
						+ "\t Faktura: " + rs.getString("PoFakturi") + "\t Datum: " + rs.getString("Datum"));
			}

			String upit2 = "select sum (KolicinaNovca) from BazaTroskova where Datum between '" + datumOd + "' and '"
					+ datumDo + "'";// ukupna vrednost troskova od - do datuma
			Statement st2 = con.createStatement();
			ResultSet rs2 = st2.executeQuery(upit2);

			rs2.next();
			mesecniTrosak = rs2.getInt(1);
			sc.close();
			System.out.println("\nKolicina novca potrosena od " + datumOd + " do " + datumDo + " je: " + mesecniTrosak);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void pretragaTroskova() {// pretraga troskova prema tipu troska, datumu i fakture (Nije pretraga za kes)

		Scanner sc = new Scanner(System.in);
		System.out.println("Unos: ");
		try {
			String a = sc.nextLine();// ovde unosi TipTroska, Poverilac, PoFakturi, Datum...
			String b = sc.nextLine();// sluzi za unos racun za struju, ili ime poverioca tipa Telenor, unese fakturu,
										// ili yyyy-mm-dd
			String upit = "select * from BazaTroskova where " + a + " = '" + b + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);

			while (rs.next()) {
				System.out.println("ID Troska: " + rs.getInt("IDTroska") + "\t Tip troska: " + rs.getString("TipTroska")
						+ "\t Poverilac: " + rs.getString("Poverilac") + "\t Cena: " + rs.getInt("KolicinaNovca")
						+ "\t Faktura: " + rs.getString("PoFakturi") + "\t Datum: " + rs.getString("Datum"));
			}
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void pretragaTroskovaFaktura() {
		// funkcija koja pretrazuje sve troskove koji nisu placeni u kesu, a po datumu
		// uplate. Dakle pretrazuje troskove po fakturama
		Scanner sc = new Scanner(System.in);
		System.out.println("Datum uneti u formatu yyyy-mm-dd");
		try {
			String datum1 = sc.nextLine();
			String datum2 = sc.nextLine();
			String upit = "select * from BazaTroskova where PoFakturi != 'Kes' and Datum between '" + datum1 + "' and '"
					+ datum2 + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(upit);
			while (rs.next()) {
				System.out.println("ID Troska: " + rs.getInt("IDTroska") + "\t Tip troska: " + rs.getString("TipTroska")
						+ "\t Poverilac: " + rs.getString("Poverilac") + "\t Cena: " + rs.getInt("KolicinaNovca")
						+ "\t Faktura: " + rs.getString("PoFakturi") + "\t Datum: " + rs.getString("Datum"));
			}
			sc.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	// posle toga unos za buduce troskove gde Djura postavlja datum u formatu
	// yyyy-mm-dd
	// ovo je implementirano u f-ji unesiTrosak() gde je samo ostavljena mogucnost
	// da Djura unese datum za placanje

	public void dugovanja() {// ovaj deo sluzi da unosi dugovanja prema dobavljacima, od koga je, sta uzeo,
								// po kojoj fakturi, prvi datum automatski generisan, drugi on unosi
		Scanner sc = new Scanner(System.in);
		LocalDate datum = LocalDate.now();
		try {
			String dizajner = sc.nextLine();
			String fakturaBr = sc.nextLine();
			int iznosFakture = sc.nextInt();
			int uplata = sc.nextInt();
			int ukupnoZaduzenje = iznosFakture - uplata;
			String banka = sc.nextLine();
			String datum2 = sc.nextLine();
			String upit = "insert into DugDobavljacima (Dizajner, Datum, FakturaBr, IznosFakture, Uplata, UkupnoZaduzenje, Banka, DatumUplate)"
					+ "values('" + dizajner + "', '" + datum + "', '" + fakturaBr + "'," + iznosFakture + ", " + uplata
					+ ", " + ukupnoZaduzenje + ", '" + banka + "', '" + datum2 + "');";
			Statement st = con.createStatement();
			st.executeUpdate(upit);

			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void brisanjeDugovanja() {//kada izmiri dugovanje prema dobavljacu, brise ga po broju fakture
		Scanner sc = new Scanner(System.in);
		try {
			String faktura = sc.nextLine();
			String upit = "delete from DugDobavljacima where FakturaBr = '"+faktura+"'";
			Statement st = con.createStatement();
			st.executeUpdate(upit);
			
			sc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
